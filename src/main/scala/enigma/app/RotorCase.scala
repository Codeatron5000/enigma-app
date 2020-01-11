package enigma.app

import scalafx.beans.property.StringProperty
import scalafx.geometry.Pos
import scalafx.scene.layout.{HBox, StackPane}
import scalafx.scene.paint.Color.{Black, White, gray}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

class RotorCase(rotorPositions: Seq[StringProperty]) extends StackPane {
  children = Seq(
    new Rectangle {
      height = 100
      width = 250
      fill = Black
    },
    new HBox {
      alignment = Pos.Center
      spacing = 40
      children = rotorPositions.map(position => {
        new StackPane {
          children = Seq(
            new Rectangle {
              height = 40
              width = 20
              strokeWidth = 3
              stroke = gray(0.4)
              fill = gray(0.3)
            },
            new Text {
              text <== position
              fill = White
            }
          )
        }
      })
    }
  )
}
