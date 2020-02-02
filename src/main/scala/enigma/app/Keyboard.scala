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

  private var pressedKey: Option[Char] = None

  def pressKey(letter: Char): Unit = {
    if (keys.contains(letter) && pressedKey.isEmpty) {
      val circle = keys(letter)
      circle.strokeWidth = 4
      circle.radius = 14
      onKeyPress(letter)
      pressedKey = Some(letter)
    }
  }

  def releaseKey(letter: Char): Unit = {
    if (keys.contains(letter) && pressedKey.nonEmpty) {
      val circle = keys(letter)
      circle.strokeWidth = 2
      circle.radius = 15
      onKeyRelease(letter)
      pressedKey = None
    }
  }

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
            pressKey(c)
          }
          onMouseReleased = _ => {
            circle.strokeWidth = 2
            circle.radius = 15
            releaseKey(c)
          }
        }
      })
    }
  })

  var beingPressed: Option[Char] = None;
}
