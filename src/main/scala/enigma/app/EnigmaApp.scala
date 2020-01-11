package enigma.app

import enigma.machine.{Alphabet, Enigma, Reflector, Rotor}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.StringProperty
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import scalafx.scene.Scene
import scalafx.scene.paint.Color._

object EnigmaApp extends JFXApp {
  private val rotors = Seq(
    Rotor.I('A'),
    Rotor.II('A'),
    Rotor.III('A'),
  )

  private val rotorPositions = rotors.map(rotor => StringProperty(Alphabet(rotor.position - 1).toString))

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
        rotorPositions(i)() = Alphabet(rotors(i).position - 1).toString
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
        layoutY = 40
        spacing = 40
        alignment = Pos.TopCenter

        children = Seq(
          new RotorCase(rotorPositions),
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
        if (Alphabet.alphabet.contains(c) && encodedValue() == null) {
          encodedValue() = enigma.encode(c).toString
          rotors.indices.foreach(i => {
            rotorPositions(i)() = Alphabet(rotors(i).position - 1).toString
          })
          keyboard.keys(c).strokeWidth = 4
          keyboard.keys(c).radius = 14
        }
      }

      onKeyReleased = e => {
        val c = e.getCode.getChar.toUpperCase.charAt(0)
        encodedValue() = null
        keyboard.keys(c).strokeWidth = 2
        keyboard.keys(c).radius = 15
      }
    }
  }
}
