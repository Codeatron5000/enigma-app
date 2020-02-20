package enigma.app

import enigma.machine.{ Reflector, Rotor }
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.StringProperty
import scalafx.geometry.Pos
import scalafx.scene.{ Cursor, Scene }
import scalafx.scene.layout.{ StackPane, VBox }
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{ Circle, Rectangle }
import scalafx.scene.text.{ Font, Text }

object EnigmaApp extends JFXApp {
    private val rotors = Seq(
        Rotor.I(1),
        Rotor.II(1),
        Rotor.III(1),
    )

    private val enigma = new EnigmaProperty(
        rotors.head,
        rotors(1),
        rotors(2),
        Reflector.B,
        Seq(('A', 'B'))
    )

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

                    new RotorCase(
                        enigma.slowRotor,
                        enigma.mediumRotor,
                        enigma.fastRotor
                    ),

                    new LightBox(encodedValue),

                    new Keyboard {
                        onKeyDown((c: Char) => {
                            encodedValue() = enigma.encode(c).toString
                        })
                        onKeyUp(_ => encodedValue() = "")

                        enigmaScene.onKeyPressed = e => {
                            val c = e.getCode.getChar.toUpperCase.charAt(0)
                            pressKey(c)
                        }

                        enigmaScene.onKeyReleased = e => {
                            val c = e.getCode.getChar.toUpperCase.charAt(0)
                            releaseKey(c)
                        }
                    },

                    new PlugBoard(enigma.connections) {
                        onConnectionAdded(enigma.addConnection)
                        onConnectionRemoved(enigma.removeConnection)
                    },
                )
            })
        }
    }
}
