package enigma.app

import enigma.machine.{ Rotor => MachineRotor }
import scalafx.animation.TranslateTransition
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{ BooleanProperty, StringProperty }
import scalafx.geometry.Pos
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{ Rectangle, Shape }
import scalafx.scene.{ Cursor, Scene }
import scalafx.util.Duration

import scala.collection.mutable

/**
 * The entry point to the Enigma application.
 */
object EnigmaApp extends JFXApp {

    // The enigma app starts with all reflectors and rotors outside the case
    // and ready to be placed.
    // Here we initialise them in mutable arrays to indicate the state of the
    // app.
    private val unusedReflectors: mutable.Seq[Option[Cylinder]] = mutable.Seq(
        Some(new Reflector),
        Some(new Reflector),
    )
    private val unusedRotors: mutable.Seq[Option[Rotor]] = mutable.Seq(
        Some(new Rotor(new RotorProperty(MachineRotor.I(1)))),
        Some(new Rotor(new RotorProperty(MachineRotor.II(1)))),
        Some(new Rotor(new RotorProperty(MachineRotor.III(1)))),
        Some(new Rotor(new RotorProperty(MachineRotor.IV(1)))),
        Some(new Rotor(new RotorProperty(MachineRotor.V(1)))),
    )
    // A boolean to indicate if all rotors and reflectors are placed.
    private val isSetup = BooleanProperty(false)
    // The string that is currently being encoded.
    private val encodedValue = new StringProperty("")
    // The stream of encoded/decoded letters.
    private val cipherStream = new StringProperty("")

    /**
     * Trigger the isSetup variable to update depending on the state of the
     * enigma property.
     */
    private def updateIsSetup(): Unit = {
        val isDisabled = EnigmaProperty.isDisabled

        if (isDisabled == isSetup()) {
            isSetup() = !isDisabled
        }
    }

    /**
     * A cylinder representing a reflector (there's nothing much to them).
     */
    class Reflector extends Cylinder {
        disableDrag = true
        sectionWidth = 30
        sectionStrokeWidth = 0
        cursor = Cursor.Hand
    }

    // Update the cipher stream when a letter is pressed.
    encodedValue.onChange((_, _, v) => cipherStream() = cipherStream() + v)
    cipherStream.onChange((_, _, v) => {
        // Split up the stream into groups of 4 (this is how the ciphers were
        // written down).
        if (v.length > 3 && !v.substring(v.length - 4).contains(' ')) {
            cipherStream() = v + " "
        }
        // Truncate the cipher stream if it gets too long.
        if (v.length > 56) {
            cipherStream() = v.substring(v.length - 56)
        }
    })

    new PrimaryStage {
        scene = new Scene(600, 800) { enigmaScene =>
            fill = gray(0.1)
            minWidth = 460
            minHeight = 800

            getChildren.add(new VBox {
                minWidth <== enigmaScene.width
                maxWidth <== enigmaScene.width
                spacing = 30
                alignment = Pos.TopCenter

                children = Seq(
                    // The line at the top that shows the encoded string.
                    CipherLine(cipherStream),

                    // The case that holds the three rotors and reflector.
                    RotorCase,

                    // The group of characters that light up indicating the
                    // encoded value.
                    LightBox(encodedValue),

                    // The keyboard that can be clicked or triggered using the
                    // computer keyboard.
                    {
                        // When a key is pressed, if the enigma machine is set
                        // up, write to the encoded value.
                        Keyboard.onKeyDown((c: Char) => {
                            if (!EnigmaProperty.isDisabled) {
                                encodedValue() = EnigmaProperty.encode(c).get.toString
                            }
                        })
                        // When the key is released reset the encoded value.
                        Keyboard.onKeyUp(_ => encodedValue() = "")

                        // When a key on the keyboard is pressed trigger the
                        // enigma keyboard key.
                        enigmaScene.onKeyPressed = e => {
                            if (!EnigmaProperty.isDisabled) {
                                val c = e.getCode.getChar.toUpperCase.charAt(0)
                                Keyboard.pressKey(c)
                            }
                        }

                        enigmaScene.onKeyReleased = e => {
                            if (!EnigmaProperty.isDisabled) {
                                val c = e.getCode.getChar.toUpperCase.charAt(0)
                                Keyboard.releaseKey(c)
                            }
                        }
                        Keyboard
                    },

                    // The plug board that holds all the substitution wires.
                    {
                        PlugBoard.onConnectionAdded(EnigmaProperty.addConnection)
                        PlugBoard.onConnectionRemoved(EnigmaProperty.removeConnection)
                        PlugBoard
                    },
                )
            })

            /**
             * Build an overlay that covers the whole app leaving a hole around
             * the rotor case as only that can be interacted with whilst any
             * rotors or the reflector are missing.
             * @return
             */
            def buildCover: Shape = {
                val cover = Shape.subtract(
                    new Rectangle {
                        height = enigmaScene.height()
                        width = enigmaScene.width()
                    }, new Rectangle {
                        width = RotorCase.width()
                        height = RotorCase.height()
                        layoutX = RotorCase.layoutX()
                        layoutY = RotorCase.layoutY()
                    })
                // Clear the overlay when the enigma machine is set up.
                cover.fill = if (isSetup()) {
                    Color.Transparent
                } else {
                    Color.gray(0, 0.5)
                }
                cover.mouseTransparent <== isSetup
                cover
            }

            private var cover = buildCover

            getChildren.add(cover)

            // Rebuild the overlay whenever the scene changes.
            enigmaScene.width.onChange((_, _, _) => {
                val index = getChildren.indexOf(cover)
                cover = buildCover
                getChildren.set(index, cover)
            })

            enigmaScene.height.onChange((_, _, _) => {
                val index = getChildren.indexOf(cover)
                cover = buildCover
                getChildren.set(index, cover)
            })

            RotorCase.layoutY.onChange((_, _, _) => {
                val index = getChildren.indexOf(cover)
                cover = buildCover
                getChildren.set(index, cover)
            })

            RotorCase.layoutX.onChange((_, _, _) => {
                val index = getChildren.indexOf(cover)
                cover = buildCover
                getChildren.set(index, cover)
            })

            isSetup.onChange((_, _, v) => {
                if (v) {
                    cover.fill = Color.Transparent
                } else {
                    cover.fill = Color.gray(0, 0.5)
                }
            })

            // Drawing all the unused rotors and reflectors to the screen.
            // They fly off the screen when the enigma machine is set up and
            // reappear whenever the machine is missing anything.
            getChildren.add(
                new HBox { node =>
                    alignment = Pos.Center
                    spacing = 40
                    layoutY = if (isSetup()) -300 else 300
                    scene.onChange((_, _, v) => {
                        if (v != null) {
                            minWidth <== v.widthProperty()
                        }
                    })

                    // Animating the components whenever the machine is set up
                    // or disassembled.
                    isSetup.onChange((_, _, v) => {
                        val trans = new TranslateTransition(new Duration(500), node)
                        trans.toY = if (v) -600 else 0
                        trans.play()
                    })

                    private def buildChildren(): Unit = {
                        children =
                            unusedReflectors.indices.map(i => {
                                unusedReflectors(i) match {
                                    case Some(r) => new DraggableReflector(r, if (i == 0) 'B' else 'C') {
                                        onPlaced = () => {
                                            unusedReflectors(i) = None
                                            r.onMouseClicked = _ => {
                                                RotorCase.removeReflector()
                                                EnigmaProperty.reflector = None
                                                unusedReflectors(i) = Some(r)
                                                r.onMouseClicked = _ => ()
                                                buildChildren()
                                                updateIsSetup()
                                            }
                                            buildChildren()
                                            updateIsSetup()
                                        }
                                    }
                                    case _ => Rectangle(30, 0)
                                }
                            }) ++
                                unusedRotors.map {
                                    case Some(r) => new DraggableRotor(r) {
                                        onPlaced = (i) => {
                                            val index = unusedRotors.indexOf(Some(r))
                                            unusedRotors(index) = None
                                            r.onClicked = () => {
                                                RotorCase.removeRotor(r)
                                                i match {
                                                    case 0 => EnigmaProperty.slowRotor = None
                                                    case 1 => EnigmaProperty.mediumRotor = None
                                                    case 2 => EnigmaProperty.fastRotor = None
                                                }
                                                unusedRotors(index) = Some(r)
                                                r.onClicked = () => ()
                                                buildChildren()
                                                updateIsSetup()
                                            }
                                            buildChildren()
                                            updateIsSetup()
                                        }
                                    }
                                    case _ => Rectangle(40, 0)
                                }
                    }

                    buildChildren()
                }
            )
        }
    }
}
