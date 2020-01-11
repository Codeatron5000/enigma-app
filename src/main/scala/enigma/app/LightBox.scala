package enigma.app

import scalafx.beans.property.{ObjectProperty, ReadOnlyDoubleProperty, StringProperty}
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.paint.Color.{Black, White, Yellow}
import scalafx.scene.shape.Circle
import scalafx.scene.text.{Font, FontWeight, Text}

class LightBox(encodedValue: StringProperty) extends VBox {
  spacing = 15
  children = KeypadOrder().map(row => {
    new HBox {
      alignment = Pos.Center
      spacing = 15
      children = row.map(c => {
        new StackPane {
          private val color = ObjectProperty(White)
          private val strokeColor = ObjectProperty(Black)
          encodedValue.onChange((_, _, v) => {
            color() = if (v == c.toString) Yellow else White
            strokeColor() = if (v == c.toString) Yellow else Black
          })
          children = Seq(
            new Circle {
              radius = 15
              fill = Black
              strokeWidth = 1
              stroke <== strokeColor
            },
            new Text {
              text = c.toString
              fill <== color
              font = Font("Alias", FontWeight.Bold, 15)
            }
          )
        }
      })
    }
  })
}
