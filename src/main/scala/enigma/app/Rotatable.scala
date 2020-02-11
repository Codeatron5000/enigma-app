package enigma.app

import scalafx.animation.RotateTransition
import scalafx.scene.Node
import scalafx.scene.transform.Rotate
import scalafx.util.Duration

trait Rotatable extends Node {node =>
  def startFullDrag(): Unit
  def disableDrag: Boolean = false
  def onRotated: Int => Unit = _ => ()
  def onRotateEnded: () => Unit = () => ()

  rotationAxis = Rotate.XAxis

  var currentPosition = 1

  private var previousY: Option[Double] = None
  onDragDetected = e => {
    node.startFullDrag()
    previousY = Some(e.getSceneY)
  }

  onMouseDragged = e => {
    if (!disableDrag && previousY.nonEmpty) {
      val y = e.getSceneY
      val diff = previousY.get - y
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
    }
  }

  onMouseDragReleased = _ => {
    if (!disableDrag) {
      previousY = None
      onRotateEnded()
    }
  }

  def rotateTo(newPosition: Int, duration: Int = 200): Unit = {
    val relativeCurrentPosition = currentPosition % 26
    var diff = relativeCurrentPosition - newPosition
    if (diff > 13) {
      diff -= 26
    } else if (diff < -13) {
      diff += 26
    }
    val absolutePosition = currentPosition - diff
    val trans = new RotateTransition(new Duration(duration), node)
    trans.setFromAngle((currentPosition - 1) * Rotor.degAngle)
    trans.setToAngle((absolutePosition - 1) * Rotor.degAngle)
    trans.play()
    currentPosition = absolutePosition
    onRotated(newPosition)
  }
}
