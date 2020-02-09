package enigma.app

import enigma.machine.{Rotor => MachineRotor}
import scalafx.beans.property.IntegerProperty
import scalafx.geometry.Pos
import scalafx.scene.layout.{HBox, StackPane}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Black
import scalafx.scene.shape.{Rectangle, Shape}

class RotorCase(rotorPositions: Seq[IntegerProperty], rotors: Seq[MachineRotor]) extends StackPane {
  private var cover: Shape = new Rectangle {
    height = 220
    width = 300
    fill = Black
  }

  private val holes = (0 until 3).map(i => {
    Rectangle(38 + 90 * i, 98, 22, 25)
  })

  private val borders = (0 until 3).map(_ => {
    val border = Shape.subtract(
      Rectangle(30, 33),
      Rectangle(4, 4, 22, 25)
    )
    border.fill = Color.Gray
    border
  })

  holes.foreach(hole => {
    cover = Shape.subtract(cover, hole)
  })

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
    },
    new StackPane {
      children = Seq(
        cover,
        new HBox {
          alignment = Pos.Center
          spacing = 60
          translateX = -9
          children = borders
        }
      )
    }
  )
}
