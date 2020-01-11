package enigma.app

import enigma.machine.Alphabet
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.Pos
import scalafx.scene.{Cursor, Scene}
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.paint.Color.{White, gray}
import scalafx.scene.shape.Circle
import scalafx.scene.text.{Font, Text}

class Keyboard(onKeyPress: Char => Unit, onKeyRelease: Char => Unit) extends VBox {
  spacing = 15

  val keys = Map(
    Alphabet.alphabet.map(letter => {
      (letter, new Circle {circle =>
        radius = 15
        fill = gray(0.2)
        strokeWidth = 2
        stroke = gray(0.4)
      })
    }) : _*
  )

  children = KeypadOrder().map(row => {
    new HBox {
      alignment = Pos.Center
      spacing = 15
      children = row.map(c => {
        new StackPane {
          private val circle = keys(c)
          children = Seq(
            circle,
            new Text {
              text = c.toString
              fill = White
              font = Font(15)
            }
          )
          cursor = Cursor.Hand
          onMousePressed = _ => {
            circle.strokeWidth = 4
            circle.radius = 14
            onKeyPress(c)
          }
          onMouseReleased = _ => {
            circle.strokeWidth = 2
            circle.radius = 15
            onKeyRelease(c)
          }
        }
      })
    }
  })

  var beingPressed: Option[Char] = None;
}
