package enigma.app

import enigma.machine.{Alphabet, Enigma, Reflector, Rotor}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.geometry.Pos
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.{Cursor, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle
import scalafx.scene.transform.Rotate

object EnigmaApp extends JFXApp {
  private val rotors = Seq(
    Rotor.I('A'),
    Rotor.II('A'),
    Rotor.III('A'),
  )

  private val rotorPositions = rotors.map(rotor => IntegerProperty(rotor.position))

  private val enigma = Enigma(
    rotors.head,
    rotors(1),
    rotors(2),
    Reflector.B,
    Seq(('A', 'B'))
  )

  private val encodedValue = new StringProperty

  enigma.setPositions('A', 'A', 'A')

  private val keyboard = new Keyboard(
    c => {
      encodedValue() = enigma.encode(c).toString
      rotors.indices.foreach(i => {
        rotorPositions(i)() = rotors(i).position
      })
    },
    _ => encodedValue() = null
  )

  new PrimaryStage {
    scene = new Scene(600, 800) {enigmaScene =>
      fill = gray(0.1)
      minWidth = 460

      private val layout: VBox = new VBox {
        minWidth <== enigmaScene.width
        maxWidth <== enigmaScene.width
        layoutY = 30
        spacing = 30
        alignment = Pos.TopCenter

        children = Seq(
          new RotorCase(rotorPositions, rotors),
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
