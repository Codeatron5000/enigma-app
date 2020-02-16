package enigma.app

import scalafx.animation.{ KeyFrame, KeyValue, Timeline }
import scalafx.beans.property.IntegerProperty
import scalafx.geometry.Pos
import scalafx.scene.Cursor
import scalafx.scene.layout.{ HBox, StackPane }
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Black
import scalafx.scene.shape.{ Rectangle, Shape }
import scalafx.scene.transform.Rotate
import scalafx.util.Duration

class RotorCase(
    slowRotor: RotorProperty,
    mediumRotor: RotorProperty,
    fastRotor: RotorProperty
) extends StackPane {
    private val holes = (0 until 3).map(i => {
        Rectangle(89 + 70 * i, 98, 22, 25)
    })

    private val borders = (0 until 3).map(_ => {
        val border = Shape.subtract(
            Rectangle(30, 33),
            Rectangle(4, 4, 22, 25)
        )
        border.fill = Color.Gray
        border
    })

    private val rotors = Seq(
        slowRotor,
        mediumRotor,
        fastRotor
    )

    private var sheet: Shape = new Rectangle {
        height = 220
        width = 300
        fill = Black
    }

    holes.foreach(hole => {
        sheet = Shape.subtract(sheet, hole)
    })

    children = Seq(
        new Rectangle {
            height = 220
            width = 300
            fill = Color.gray(0.2)
        },
        new HBox {
            alignment = Pos.Center
            spacing = 30
            private val cylinders = rotors.map(new Rotor(_))

            children = new Cylinder {
                sectionWidth = 30

                sectionStrokeWidth = 0
            } +: cylinders
        },
        new StackPane { cover =>
            private var open = false

            private val rotation = new Rotate {
                pivotX <== cover.translateX
                pivotY <== cover.translateY
                axis = Rotate.XAxis
            }

            transforms.add(rotation)

            cursor = Cursor.OpenHand

            onMouseClicked = _ => {
                Timeline(
                    Seq(
                        KeyFrame(
                            Duration.Zero,
                            null, null,
                            Set(KeyValue(rotation.angle, if (open) 85 else 0)),
                        ),
                        KeyFrame(
                            Duration(1000),
                            null, null,
                            Set(KeyValue(rotation.angle, if (open) 0 else 85)),
                        ),
                    )
                ).play()

                open = !open
            }

            children = Seq(
                sheet,
                new HBox {
                    alignment = Pos.Center
                    spacing = 40
                    translateX = 20
                    children = borders
                }
            )
        }
    )
}
