package enigma.app

import javafx.scene.Scene
import javafx.scene.input.MouseDragEvent
import scalafx.animation.RotateTransition
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Node
import scalafx.scene.transform.Rotate
import scalafx.util.Duration

/**
 * We want the node to be rotated even when the mouse is dragged off the node.
 * That means we need to listen to drag events on the scene as well as on the
 * node itself.
 * In this object we declare some callbacks that will be run when the mouse is
 * released anywhere on the scene so we can trigger the drag release logic.
 */
object Rotatable {
    private var _onDragReleasedCallbacks: Seq[MouseDragEvent => Unit] = Seq.empty
    private var booted = false

    def addOnDragReleasedCallback(cb: MouseDragEvent => Unit): Unit = {
        _onDragReleasedCallbacks = _onDragReleasedCallbacks :+ cb
    }

    def removeOnDragReleasedCallback(cb: MouseDragEvent => Unit): Unit = {
        _onDragReleasedCallbacks = _onDragReleasedCallbacks.filterNot(_ == cb)
    }

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

/**
 * A trait that allows a node to be rotated in increments of 26 by clicking and
 * dragging.
 */
trait Rotatable extends Node { node =>
    // Boot the scene callbacks if whenever the scene changes, if they haven't
    // already been booted.
    if (scene() != null) {
        Rotatable.bootIfNotBooted(scene())
    } else {
        scene.onChange((v, _, _) => Rotatable.bootIfNotBooted(v()))
    }

    // Allow the draggableness to be disabled.
    private val _disableDrag: BooleanProperty = BooleanProperty(false)
    var currentPosition = 1
    // A callback to trigger when the node has finished rotating.
    var onRotateEnded: () => Unit = () => ()
    // A callback to trigger when the node has is rotated to a new position.
    var onRotated: Int => Unit = _ => ()
    protected var dragging = false
    private var previousY: Option[Double] = None

    rotationAxis = Rotate.XAxis

    /**
     * Set and get the disable drag value.
     * @return
     */
    def disableDrag: BooleanProperty = _disableDrag

    def disableDrag_=(value: Boolean): Unit = _disableDrag() = value

    onDragDetected = e => {
        if (!disableDrag()) {
            dragging = true
            node.startFullDrag()
            previousY = Some(e.getSceneY)

            // When a drag is started we add the necessary callbacks to let us
            // know when the drag has ended, and then remove afterwards.
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

    /**
     * Rotate the node to a specific position.
     * @param newPosition The new position to move to.
     * @param duration How long the rotation should take.
     */
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
}
