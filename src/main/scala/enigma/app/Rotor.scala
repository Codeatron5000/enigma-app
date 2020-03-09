package enigma.app

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

/**
 * The enigma rotors contained 26 connections on each side that scramble the
 * pressed character depending on the internal wiring, the setting of one side
 * relative to the other and the position of the rotor inside the machine that
 * changes with each letter.
 *
 * The rotor consists of 3 cylinders. The cylinder of numbers that indicate the
 * position of the rotor. The spacer cylinder that holds the pin for locking a
 * configuration. And the gear cylinder that is used to turn the rotor.
 *
 * @param rotor The observable rotor property that dictates the position and the
 *              setting of the rotor.
 */
case class Rotor(rotor: RotorProperty) extends HBox with Rotatable { tumbler =>
    private val locked = BooleanProperty(true)
    // The first cylinder that shows the 26 numbers indicating the rotors
    // position.
    // These sections can be rotated separately to the rest of the rotor if the
    // setting pin is unlocked.
    private val sections: Cylinder = new Cylinder(
        (i, _) => {
            Seq(new Text("%02d".format(i + 1)) {
                font = Font("Arial", 13)
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
    // The second cylinder creates a space where the locking pin is, that
    // indicates the current setting of the rotor.
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
    // The final gear cylinder that doesn't really do anything.
    private val gear = new Cylinder {
        sectionWidth = 5
        sectionHeight = 24
        sectionFill = Gray
        disableDrag = true
    }
    // The onClicked callback is used to remove the rotor from the machine, but
    // only if the click was not part of a drag.
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
    private var sectionsRotating: Boolean = false

    onRotated = i => {
        sections.orderSections()
        spacerSections.orderSections()
        isRotating = true
        val newPosition = i + rotor.setting() - 1
        rotor.position = if (newPosition > 26) newPosition - 26 else newPosition
        isRotating = false
    }
    private var isRotating = false

    children = Seq(
        sections,
        spacerSections,
        gear
    )

    // Map the position of the rotor node to the position on the property.
    rotateTo(rotor.position(), 0)
    rotor.position.onChange((_, _, v) => {
        if (!isRotating && !sectionsRotating) {
            val newPosition = v.intValue() - rotor.setting() + 1
            rotateTo(if (newPosition < 1) newPosition + 26 else newPosition)
        }
    })

}
