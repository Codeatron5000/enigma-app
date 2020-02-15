package enigma.app

import enigma.machine.{ Reflector, Rotor }
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.StringProperty
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color._

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

    private val encodedValue = new StringProperty

    private val keyboard = new Keyboard()
    keyboard.onKeyDown(c => {
        encodedValue() = enigma.encode(c).toString
    })
    keyboard.onKeyUp(_ => encodedValue() = null)

    new PrimaryStage {
        scene = new Scene(600, 800) {
            enigmaScene =>
            fill = gray(0.1)
            minWidth = 460

            private val layout: VBox = new VBox {
                minWidth <== enigmaScene.width
                maxWidth <== enigmaScene.width
                layoutY = 30
                spacing = 30
                alignment = Pos.TopCenter

                children = Seq(
                    new RotorCase(enigma),
                    new LightBox(encodedValue),
                    keyboard,
                    new PlugBoard(
                        enigmaScene,
                        enigma
                    ),
                )
            }

            getChildren.add(layout)

            onKeyPressed = e => {
                val c = e.getCode.getChar.toUpperCase.charAt(0)
                keyboard.pressKey(c)
            }

            onKeyReleased = e => {
                val c = e.getCode.getChar.toUpperCase.charAt(0)
                keyboard.releaseKey(c)
            }
        }
    }
}
