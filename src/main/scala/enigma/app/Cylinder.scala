package enigma.app

import javafx.geometry.Point3D
import scalafx.geometry.Pos
import scalafx.scene.{Cursor, Node}
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Gray}
import scalafx.scene.shape.Rectangle
import scalafx.scene.transform.Rotate

object Cylinder {
  val radAngle: Double = 2 * Math.PI / 26
  val degAngle: Double = 360.0 / 26.0
}

class Cylinder extends StackPane with Rotatable {
  rotationAxis = Rotate.XAxis

  def sectionWidth: Float = 20
  def sectionHeight: Float = 20
  def section: (Int, Node) => Seq[Node] = (_, _) => Seq()
  def sectionStroke: Option[Color] = Some(Black)
  def sectionFill: Color = Gray

  private val fullHeight = if (sectionStroke.isEmpty) sectionHeight else sectionHeight + 1
  val radius: Double = fullHeight / (2 * Math.sin(Math.PI / 26))

  private var fromAngle = 0.0
  private val sections = (0 until 26).map(i => {
    new StackPane {pane =>
      translateY = radius * Math.sin(fromAngle)
      translateZ = radius * Math.cos(fromAngle)
      rotationAxis = Rotate.XAxis
      rotate = -fromAngle * 180 / Math.PI
      maxHeight = sectionHeight
      maxWidth = sectionWidth
      fromAngle += Cylinder.radAngle
      alignment = Pos.Center

      children = Seq(
        new Rectangle {
          width = sectionWidth
          height = sectionHeight
          fill = sectionFill
          if (sectionStroke.nonEmpty) {
            strokeWidth = 1
            stroke = sectionStroke.get
          }
        }
      ) ++ section(i, pane)
    }
  })

  children = sections

  def orderSections(): Unit = {
    val sortedSections = sections.sortBy(section => {
      section.localToScene(new Point3D(0, 0,0)).getZ
    })
    sortedSections.foreach(_.toFront)
  }

  orderSections()

  override def onRotated: Int => Unit = _ => orderSections()
}
