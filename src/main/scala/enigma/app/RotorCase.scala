package enigma.app

import enigma.machine.{Rotor => MachineRotor}
import enigma.machine.Alphabet
import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.geometry.{Point3D, Pos}
import scalafx.scene.input.KeyCode.Control
import scalafx.scene.layout.{HBox, Region, StackPane}
import scalafx.scene.paint.Color.{Black, White, gray}
import scalafx.scene.shape.{Cylinder, DrawMode, Rectangle}
import scalafx.scene.text.Text
import scalafx.scene.transform.Rotate

import scala.collection.mutable

class RotorCase(rotorPositions: Seq[IntegerProperty], rotors: Seq[MachineRotor]) extends StackPane {
  children = Seq(
    new Rectangle {
      height = 220
      width = 300
      fill = Black
    },
    new HBox {
      alignment = Pos.Center
      spacing = 50
      private val cylinders = rotorPositions.indices.map(i => {
        val position = rotorPositions(i)
        val rotor = new Rotor(position(), pos => rotors(i).setPosition(pos))
        position.onChange((_, _, v) => rotor.rotateTo(v.intValue()))
        rotor
      })

      children = cylinders
    }
  )
}
