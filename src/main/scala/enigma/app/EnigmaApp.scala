package enigma.app

import enigma.machine.{ Reflector, Rotor => MachineRotor }
import javafx.scene.input.MouseDragEvent
import scalafx.animation.TranslateTransition
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{ BooleanProperty, StringProperty }
import scalafx.geometry.{ Point2D, Pos }
import scalafx.scene.{ Cursor, Group, Scene }
import scalafx.scene.layout.{ HBox, StackPane, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{ Circle, Rectangle, Shape }
import scalafx.scene.text.{ Font, Text }
import scalafx.util.Duration

import scala.collection.mutable

object EnigmaApp extends JFXApp {
    private val rotors = Seq(
        MachineRotor.I(1),
        MachineRotor.II(1),
        MachineRotor.III(1),
    )

    private val enigma = new EnigmaProperty(
        rotors.head,
        rotors(1),
        rotors(2),
        Reflector.B,
        Seq(('A', 'B'))
    )

    private var usedRotors: mutable.Seq[Option[Rotor]] = mutable.Seq(
        None, None, None
    )

    private var unusedReflectors: mutable.Seq[Option[Cylinder]] = mutable.Seq(
        Some(new Cylinder {
            disableDrag = true
            sectionWidth = 30
            sectionStrokeWidth = 0
            cursor = Cursor.Hand
        }),
        Some(new Cylinder {
            disableDrag = true
            sectionWidth = 30
            sectionStrokeWidth = 0
            cursor = Cursor.Hand
        }),
    )

    private var unusedRotors: mutable.Seq[Option[Rotor]] = mutable.Seq(
        Some(new Rotor(new RotorProperty(MachineRotor.I(1)))),
        Some(new Rotor(new RotorProperty(MachineRotor.II(1)))),
        Some(new Rotor(new RotorProperty(MachineRotor.III(1)))),
        Some(new Rotor(new RotorProperty(MachineRotor.IV(1)))),
        Some(new Rotor(new RotorProperty(MachineRotor.V(1)))),
    )

    private val isSetup = BooleanProperty(false)

    private def updateIsSetup(): Unit = {
        val allRotorsSet = unusedRotors.count(_.isEmpty) == 3 &&
            unusedReflectors.count(_.isEmpty) == 1

        if (allRotorsSet != isSetup()) {
            isSetup() = allRotorsSet
        }
    }

    private def isDisabled = {
        usedRotors.contains(None)
    }

    private val encodedValue = new StringProperty("")
    private val cipherStream = new StringProperty("")
    encodedValue.onChange((_, _, v) => cipherStream() = cipherStream() + v)
    cipherStream.onChange((_, _, v) => {
        if (v.length > 3 && !v.substring(v.length - 4).contains(' ')) {
            cipherStream() = v + " "
        }
    })
    cipherStream.onChange((_, _, v) => {
        if (v.length > 56) {
            cipherStream() = v.substring(v.length - 56)
        }
    })

    private val rotorCase = new RotorCase()

    new PrimaryStage {
        scene = new Scene(600, 800) {enigmaScene =>
            fill = gray(0.1)
            minWidth = 460

            getChildren.add(new VBox {
                minWidth <== enigmaScene.width
                maxWidth <== enigmaScene.width
                spacing = 30
                alignment = Pos.TopCenter

                children = Seq(
                    new StackPane {
                        children = Seq(
                            new Rectangle {
                                width <== enigmaScene.width
                                height = 30
                                fill = gray(0.5, 0.5)
                            },
                            new Text {
                                text <== cipherStream
                                fill = White
                                font = Font("Noto Mono", 15)
                            },
                            new StackPane {
                                onMouseClicked = _ => cipherStream() = ""
                                managed = false
                                layoutX <== enigmaScene.width
                                layoutY = 15
                                translateX = -20
                                cursor = Cursor.Hand
                                children = Seq(
                                    Circle(10, gray(0.9, 0.8)),
                                    new Text("X") {
                                        font = Font(15)
                                    }
                                )
                            }
                        )
                    },

                    rotorCase,

                    new LightBox(encodedValue),

                    new Keyboard {
                        onKeyDown((c: Char) => {
                            if (!isDisabled) {
                                encodedValue() = enigma.encode(c).toString
                            }
                        })
                        onKeyUp(_ => encodedValue() = "")

                        enigmaScene.onKeyPressed = e => {
                            if (!isDisabled) {
                                val c = e.getCode.getChar.toUpperCase.charAt(0)
                                pressKey(c)
                            }
                        }

                        enigmaScene.onKeyReleased = e => {
                            if (!isDisabled) {
                                val c = e.getCode.getChar.toUpperCase.charAt(0)
                                releaseKey(c)
                            }
                        }
                    },

                    new PlugBoard(enigma.connections) {
                        onConnectionAdded(enigma.addConnection)
                        onConnectionRemoved(enigma.removeConnection)
                    },
                )
            })

            def buildCover: Shape = {
                val cover = Shape.subtract(new Rectangle {
                    height = enigmaScene.height()
                    width = enigmaScene.width()
                }, new Rectangle {
                    width = rotorCase.width()
                    height = rotorCase.height()
                    layoutX = rotorCase.layoutX()
                    layoutY = rotorCase.layoutY()
                })
                if (isSetup()) {
                    cover.fill = Color.Transparent
                } else {
                    cover.fill = Color.gray(0, 0.5)
                }
                cover.mouseTransparent <== isSetup
                cover
            }

            private var cover = buildCover

            getChildren.add(cover)

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

            isSetup.onChange((_, _, v) =>{
                if (v) {
                    cover.fill = Color.Transparent
                } else {
                    cover.fill = Color.gray(0, 0.5)
                }
            })

            getChildren.add(
                new HBox {node =>
                    alignment = Pos.Center
                    spacing = 40
                    layoutY = if (isSetup()) -300 else 300
                    scene.onChange((_, _, v) => {
                        if (v != null) {
                            minWidth <== v.widthProperty()
                        }
                    })

                    isSetup.onChange((_, _, v) => {
                        val trans = new TranslateTransition(new Duration(500), node)
                        trans.toY = if (v) -600 else 0
                        trans.play()
                    })
                    private def buildChildren(): Unit = {
                        children =
                            unusedReflectors.indices.map(i => {
                                unusedReflectors(i) match {
                                    case Some(r) => new VBox() {
                                        alignment = Pos.Center
                                        spacing = 20
                                        children = Seq(
                                            new Text(if (i == 0) "B" else "C") {
                                                font = Font(30)
                                                fill = Color.White
                                            },
                                            new Group {
                                                children = Seq(r)

                                                private var previousLocation: Option[Point2D] = None

                                                onDragDetected = e => {
                                                    startFullDrag()
                                                    previousLocation = Some(new Point2D(e.getSceneX, e.getSceneY))
                                                }

                                                onMouseDragged = e => {
                                                    previousLocation.foreach(v => {
                                                        translateX = e.getSceneX - v.x
                                                        translateY = e.getSceneY - v.y
                                                    })
                                                }

                                                onMouseDragReleased = _ => {
                                                    val placed = rotorCase.dropReflector(r)
                                                    if (placed) {
                                                        unusedReflectors(i) = None
                                                        i match {
                                                            case 0 => enigma.reflector = Reflector.B
                                                            case 1 => enigma.reflector = Reflector.C
                                                        }
                                                        r.onMouseClicked = _ => {
                                                            rotorCase.removeReflector()
                                                            unusedReflectors(i) = Some(r)
                                                            r.onMouseClicked = _ => ()
                                                            buildChildren()
                                                            updateIsSetup()
                                                        }
                                                        buildChildren()
                                                        updateIsSetup()
                                                    }
                                                    translateX = 0
                                                    translateY = 0
                                                    previousLocation = None
                                                }
                                            }
                                        )
                                    }
                                    case _ => Rectangle(30, 0)
                                }
                            }) ++
                            unusedRotors.map {
                                case Some(r) => new VBox() {
                                    alignment = Pos.Center
                                    spacing = 20
                                    children = Seq(
                                        new Text(r.rotor() match {
                                            case MachineRotor.I => "I"
                                            case MachineRotor.II => "II"
                                            case MachineRotor.III => "III"
                                            case MachineRotor.IV => "IV"
                                            case MachineRotor.V => "V"
                                        }) {
                                            font = Font(30)
                                            fill = Color.White
                                        },
                                        new Group {
                                            children = Seq(r)
                                            r.disableDrag = true

                                            private var previousLocation: Option[Point2D] = None

                                            onDragDetected = e => {
                                                startFullDrag()
                                                previousLocation = Some(new Point2D(e.getSceneX, e.getSceneY))
                                            }

                                            onMouseDragged = e => {
                                                previousLocation.foreach(v => {
                                                    translateX = e.getSceneX - v.x
                                                    translateY = e.getSceneY - v.y
                                                })
                                            }

                                            onMouseDragReleased = _ => {
                                                val placed = rotorCase.dropRotor(r)
                                                placed.foreach(v => {
                                                    v match {
                                                        case 0 => enigma.slowRotor = r.rotor
                                                        case 1 => enigma.mediumRotor = r.rotor
                                                        case 2 => enigma.fastRotor = r.rotor
                                                    }
                                                    usedRotors(v) = Some(r)
                                                    val index = unusedRotors.indexOf(Some(r))
                                                    unusedRotors(index) = None
                                                    r.onClicked = () => {
                                                        rotorCase.removeRotor(r)
                                                        unusedRotors(index) = Some(r)
                                                        r.onClicked = () => ()
                                                        buildChildren()
                                                        updateIsSetup()
                                                    }
                                                    buildChildren()
                                                    updateIsSetup()
                                                })
                                                translateX = 0
                                                translateY = 0
                                                previousLocation = None
                                            }
                                        }
                                    )
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
