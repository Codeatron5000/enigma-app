package enigma.app

import javafx.scene.Scene
import javafx.scene.input.MouseDragEvent
import scalafx.animation.RotateTransition
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Node
import scalafx.scene.transform.Rotate
import scalafx.util.Duration

object Rotatable {
    private var _onDragReleasedCallbacks: Seq[MouseDragEvent => Unit] = Seq.empty
    def addOnDragReleasedCallback(cb: MouseDragEvent => Unit): Unit = {
        _onDragReleasedCallbacks = _onDragReleasedCallbacks :+ cb
    }
    def removeOnDragReleasedCallback(cb: MouseDragEvent => Unit): Unit = {
        _onDragReleasedCallbacks = _onDragReleasedCallbacks.filterNot(_ == cb)
    }

    private var booted = false

    def bootIfNotBooted(scene: Scene): Unit = {
        if (!booted) {
            booted = true
            val originalHandler = scene.getOnMouseDragReleased
            scene.setOnMouseDragReleased(e => {
                if (originalHandler != null) {
                    originalHandler.handle(e)
                }
                _onDragReleasedCallbacks.foreach(cb => cb(e))
            })
        }
    }
}

trait Rotatable extends Node { node =>
    if (scene() != null) {
        Rotatable.bootIfNotBooted(scene())
    } else {
        scene.onChange((v, _, _) => Rotatable.bootIfNotBooted(v()))
    }

    var currentPosition = 1
    private var previousY: Option[Double] = None

    private val _disableDrag: BooleanProperty = BooleanProperty(false)
    def disableDrag: BooleanProperty = _disableDrag
    def disableDrag_=(value: Boolean): Unit = _disableDrag() = value

    rotationAxis = Rotate.XAxis

    var onRotateEnded: () => Unit = () => println("default ended")

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
        if (!disableDrag()) {
            node.startFullDrag()
            previousY = Some(e.getSceneY)

            lazy val cb: MouseDragEvent => Unit = (_: MouseDragEvent) => {
                previousY = None
                onRotateEnded()
                Rotatable.removeOnDragReleasedCallback(cb)
            }
            Rotatable.addOnDragReleasedCallback(cb)
        }
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

    var onRotated: Int => Unit = _ => ()
}
