package enigma.app

import enigma.machine.Alphabet
import scalafx.animation.RotateTransition
import scalafx.scene.Cursor
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color.{Black, White}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.scene.transform.Rotate
import scalafx.util.Duration

object Rotor {
  val radius: Double = 25 / (2 * Math.sin(Math.PI / 26))
  val radAngle: Double = 2 * Math.PI / 26
  val degAngle: Double = 360.0 / 26.0
}

case class Rotor(position: Int, onRotated: Int => Unit) extends StackPane {rotor =>
  private var fromAngle = 0.0

  private var currentPosition = 0

  rotationAxis = Rotate.XAxis

  def rotateTo(newPosition: Int, duration: Int = 200): Unit = {
    val relativeCurrentPosition = currentPosition % 26
    var diff = relativeCurrentPosition - newPosition + 1
    if (diff > 13) {
      diff -= 26
    } else if (diff < -13) {
      diff += 26
    }
    val absolutePosition = currentPosition - diff
    val trans = new RotateTransition(new Duration(duration), rotor)
    trans.setFromAngle(currentPosition * Rotor.degAngle)
    trans.setToAngle(absolutePosition * Rotor.degAngle)
    trans.play()
    currentPosition = absolutePosition
    orderSections()
  }

  private var previousY: Option[Double] = None

  onDragDetected = e => {
    rotor.startFullDrag()
    previousY = Some(e.getSceneY)
  }

  onMouseDragged = e => {
    if (previousY.nonEmpty) {
      val y = e.getSceneY
      val diff = previousY.get - y
      var sectionsMoved = Math.floor(Math.abs(diff) / 20.0).intValue
      if (sectionsMoved != 0) {
        if (diff < 0) {
          sectionsMoved = -sectionsMoved
        }
        var positionToMove = currentPosition + sectionsMoved
        while (positionToMove < 0) {
          positionToMove += 26
        }
        positionToMove += 1
        rotateTo(positionToMove)
        onRotated(positionToMove)
        previousY = Some(y)
      }
    }
  }

  onMouseDragReleased = e => {
    previousY = None
  }

  cursor = Cursor.Hand
  private val sections = (0 until 26).map(i => {
    new StackPane {pane =>
      translateY = Rotor.radius * Math.sin(fromAngle)
      translateZ = Rotor.radius * Math.cos(fromAngle)
      rotationAxis = Rotate.XAxis
      rotate = -fromAngle * 180 / Math.PI
      fromAngle += Rotor.radAngle
      children = Set(
        new Rectangle {
          width = 25
          height = 25
          fill = White
          strokeWidth = 1
          stroke = Black
        },
        new Text(Alphabet(i).toString)
      )
    }
  })

  children = sections

  def orderSections(): Unit = {
    val absoluteAngle = (currentPosition % 26) * Rotor.degAngle
    sections.foreach(section => {
      var angle = -section.getRotate - absoluteAngle
      if (angle < 0) {
        angle += 360
      }
      if (angle > 360) {
        angle -= 360
      }
      if (angle > 90.0 && angle < 270.0) {
        section.toBack()
      }
    })
  }

  rotateTo(position, 0)
}
