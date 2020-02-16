package enigma.app

import scalafx.animation.RotateTransition
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Node
import scalafx.scene.transform.Rotate
import scalafx.util.Duration

trait Rotatable extends Node { node =>
    var currentPosition = 1
    private var previousY: Option[Double] = None

    def startFullDrag(): Unit

    private val _disableDrag: BooleanProperty = BooleanProperty(false)
    def disableDrag: BooleanProperty = _disableDrag
    def disableDrag_=(value: Boolean): Unit = _disableDrag() = value

    rotationAxis = Rotate.XAxis

    var onRotateEnded: () => Unit = () => ()

    def rotateTo(newPosition: Int, duration: Int = 200): Unit = {
        val relativeCurrentPosition = currentPosition % 26
        var diff = relativeCurrentPosition - newPosition
        if (diff > 13) {
            diff -= 26
        } else if (diff < -13) {
            diff += 26
        }
        val absolutePosition = currentPosition - diff
        new RotateTransition(new Duration(duration), node) {
            fromAngle = (currentPosition - 1) * Rotor.degAngle
            toAngle = (absolutePosition - 1) * Rotor.degAngle
            play()
        }
        currentPosition = absolutePosition
        onRotated(newPosition)
    }
    onDragDetected = e => {
        node.startFullDrag()
        previousY = Some(e.getSceneY)
    }

    onMouseDragged = e => {
        if (!disableDrag()) {
            previousY.foreach(value => {
                val y = e.getSceneY
                val diff = value - y
                var sectionsMoved = Math.floor(Math.abs(diff) / 15.0).intValue
                if (sectionsMoved != 0) {
                    if (diff < 0) {
                        sectionsMoved = -sectionsMoved
                    }
                    var positionToMove = currentPosition + sectionsMoved
                    while (positionToMove < 1) {
                        positionToMove += 26
                    }
                    rotateTo(positionToMove)
                    previousY = Some(y)
                }
            })
        }
    }

    onMouseDragReleased = _ => {
        if (!disableDrag()) {
            previousY = None
            onRotateEnded()
        }
    }

    var onRotated: Int => Unit = _ => ()
}
