package enigma.app

import javafx.geometry.Point3D
import scalafx.animation.TranslateTransition
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Cursor
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
    private var sectionsRotating: Boolean = false
    private val sections: Cylinder = new Cylinder(
        (i, _) => {
            Seq(new Text("%02d".format(i + 1)) {
                font = Font(13)
            })
        }
    ) {
        disableDrag <== locked

        sectionFill = White

        onRotated = i => {
            tumbler.disableDrag = true
            orderSections()
            sectionsRotating = true
            rotor.setting = i
            val newPosition = i + tumbler.currentPosition - 1
            rotor.position = ((newPosition - 1) % 26) + 1
            sectionsRotating = false
        }

        onRotateEnded = () => {
            tumbler.disableDrag = false
        }


        rotateTo(rotor.setting(), 0)
        rotor.setting.onChange((_, _, v) => {
            if (!sectionsRotating) {
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

                pane.onMouseClicked = e => {
                    e.consume()
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

    var onClicked: () => Unit = () => ()

    onMouseClicked = _ => {
        if (!dragging) {
            onClicked()
        } else {
            dragging = false
        }
    }

    cursor = Cursor.Hand
    disableDrag = false

    private var isRotating = false

    onRotated = i => {
        sections.orderSections()
        spacerSections.orderSections()
        isRotating = true
        val newPosition = i + rotor.setting() - 1
        rotor.position = if (newPosition > 26) newPosition - 26 else newPosition
        isRotating = false
    }

    private val gear = new Cylinder {
        sectionWidth = 5
        sectionHeight = 24
        sectionFill = Gray
        disableDrag = true
    }

    children = Seq(
        sections,
        spacerSections,
        gear
    )

    rotateTo(rotor.position(), 0)
    rotor.position.onChange((_, _, v) => {
        if (!isRotating && !sectionsRotating) {
            val newPosition = v.intValue() - rotor.setting() + 1
            rotateTo(if (newPosition < 1) newPosition + 26 else newPosition)
        }
    })

}
