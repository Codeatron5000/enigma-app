package enigma.app

import scalafx.beans.property.{ ObjectProperty, StringProperty }
import scalafx.geometry.Pos
import scalafx.scene.layout.{ HBox, StackPane, VBox }
import scalafx.scene.paint.Color.{ Black, White, Yellow }
import scalafx.scene.shape.Circle
import scalafx.scene.text.{ Font, FontWeight, Text }

/**
 * The light box that lights up the encoded output of the enigma machine.
 */
object LightBox extends VBox {
    private var encodedValue = new StringProperty()

    /**
     * Bind the encoded value to the local property.
     * @param value The string property that will shows the encoded value.
     * @return
     */
    def apply(value: StringProperty): LightBox.type = {
        encodedValue <== value
        this
    }

    spacing = 15
    children = KeypadOrder().map(row => {
        new HBox {
            alignment = Pos.Center
            spacing = 15
            children = row.map(c => {
                new StackPane {
                    // Whenever the encoded value changes update the stroke and
                    // fill to appear as though it's lighting up.
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
