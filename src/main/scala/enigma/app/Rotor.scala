package enigma.app

import scalafx.animation.TranslateTransition
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Cursor
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color.{ Gray, Red, White }
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{ Font, Text }
import scalafx.util.Duration

object Rotor {
    val radius: Double = 20 / (2 * Math.sin(Math.PI / 26))
    val radAngle: Double = 2 * Math.PI / 26
    val degAngle: Double = 360.0 / 26.0
}

case class Rotor(rotor: RotorProperty) extends HBox with Rotatable { tumbler =>
    private val locked = BooleanProperty(true)
    private val sections: Cylinder = new Cylinder(
        (i, _) => {
            Seq(new Text("%02d".format(i + 1)) {
                font = Font(13)
            })
        }
    ) {
        disableDrag <== locked

        sectionFill = White

        var isRotating = false

        onRotated = i => {
            orderSections()
            tumbler.disableDrag = true
            isRotating = true
            rotor.setting = i
            isRotating = false
        }

        onRotateEnded = () => {
            tumbler.disableDrag = false
        }


        rotateTo(rotor.setting(), 0)
        rotor.setting.onChange((_, _, v) => {
            if (!isRotating) {
                rotateTo(v.intValue())
            }
        })
    }
    private val spacerSections = new Cylinder(
        (i, pane) => {
            if (i == 0) {
                val pin = new Rectangle {
                    width = 5
                    height = 10
                    fill = Red
                    translateX = if (locked()) -3 else 3
                }

                locked.onChange((_, _, v) => {
                    val trans = new TranslateTransition(new Duration(100), pin)
                    trans.fromX = if (v) 3 else -3
                    trans.toX = if (v) -3 else 3
                    trans.play()
                })

                pane.onMouseClicked = _ => {
                    locked.value = !locked()
                }

                pane.cursor = Cursor.Crosshair

                Seq(
                    new Rectangle {
                        width = 5
                        height = 5
                        fill = White
                        translateX = -3
                    },
                    pin
                )
            } else {
                Seq()
            }
        }
    ) {
        sectionWidth = 13
        sectionHeight = 21
        sectionFill = Gray
        sectionStrokeWidth = 0
        disableDrag = true
    }

    cursor = Cursor.Hand
    disableDrag = false

    private var isRotating = false

    onRotated = i => {
        sections.orderSections()
        spacerSections.orderSections()
        isRotating = true
        rotor.position() = i
        isRotating = false
    }

    children = Seq(
        sections,
        spacerSections,
        new Cylinder {
            sectionWidth = 5
            sectionHeight = 24
            sectionFill = Gray
            disableDrag = true
        }
    )

    rotateTo(rotor.position(), 0)
    rotor.position.onChange((_, _, v) => {
        if (!isRotating) {
            rotateTo(v.intValue())
        }
    })
}
