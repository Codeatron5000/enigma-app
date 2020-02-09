package enigma.app

import scalafx.animation.TranslateTransition
import scalafx.beans.property.BooleanProperty
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Gray, Red, White}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, Text}
import scalafx.scene.{Cursor, Node}
import scalafx.util.Duration

object Rotor {
  val radius: Double = 20 / (2 * Math.sin(Math.PI / 26))
  val radAngle: Double = 2 * Math.PI / 26
  val degAngle: Double = 360.0 / 26.0
}

case class Rotor(position: Int, rotateCb: Int => Unit) extends HBox with Rotatable {rotor =>

  private val locked = BooleanProperty(true)
  private var draggingNumbers = false

  override def disableDrag: Boolean = draggingNumbers

  cursor = Cursor.Hand

  private val sections = new Cylinder {
    override def disableDrag: Boolean = locked()
    override def sectionFill: Color = White

    override def onRotated: Int => Unit = i => {
      super.onRotated(i)
      draggingNumbers = true
    }
    override def onRotateEnded: () => Unit = () => draggingNumbers = false
    override def section: (Int, Node) => Seq[Node] = (i, _) => {
      Seq(new Text("%02d".format(i + 1)) {
        font = Font(13)
      })
    }
  }

  private val spacerSections = new Cylinder {
    override def sectionWidth = 13
    override def sectionHeight = 21
    override def sectionFill: Color = Gray
    override def sectionStroke: Option[Color] = None

    override def disableDrag: Boolean = true

    override def section: (Int, Node) => Seq[Node] = (i, pane) => {
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
  }

  override def onRotated: Int => Unit = i => {
    sections.orderSections()
    spacerSections.orderSections()
    rotateCb(i)
  }

  children = Seq(
    sections,
    spacerSections,
    new Cylinder {
      override def sectionWidth = 5
      override def sectionHeight = 24
      override def sectionFill: Color = Gray
      override def disableDrag = true
    }
  )

  rotateTo(position, 0)
}
