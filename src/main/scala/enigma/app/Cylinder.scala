package enigma.app

import javafx.geometry.Point3D
import javafx.scene.paint.Paint
import scalafx.beans.property.{ DoubleProperty, FloatProperty, ObjectProperty }
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color.{ Black, Gray }
import scalafx.scene.shape.Rectangle
import scalafx.scene.transform.Rotate

object Cylinder {
    val radAngle: Double = 2 * Math.PI / 26
    val degAngle: Double = 360.0 / 26.0
}

class Cylinder(
    private val section: (Int, Node) => Seq[Node] = (_, _) => Seq()
) extends StackPane with Rotatable {
    rotationAxis = Rotate.XAxis

    private val _sectionWidth = FloatProperty(20)
    def sectionWidth: FloatProperty = _sectionWidth
    def sectionWidth_=(value: Float): Unit = _sectionWidth() = value

    private val _sectionHeight = FloatProperty(20)
    def sectionHeight: FloatProperty = _sectionHeight
    def sectionHeight_=(value: Float): Unit = _sectionHeight() = value

    private val _sectionStroke = ObjectProperty[Paint](Black)
    def sectionStroke: ObjectProperty[Paint] = _sectionStroke
    def sectionStroke_=(value: Paint): Unit = _sectionStroke() = value

    private val _sectionStrokeWidth = DoubleProperty(1)
    def sectionStrokeWidth: DoubleProperty = _sectionStrokeWidth
    def sectionStrokeWidth_=(value: Double): Unit = _sectionStrokeWidth() = value

    private val _sectionFill = ObjectProperty[Paint](Gray)
    def sectionFill: ObjectProperty[Paint] = _sectionFill
    def sectionFill_=(value: Paint): Unit = _sectionFill() = value

    private val fullHeight = DoubleProperty(sectionHeight() + sectionStrokeWidth())
    sectionHeight.onChange((v, _, _) => fullHeight() = v() + sectionStrokeWidth())
    sectionStrokeWidth.onChange((v, _, _) => fullHeight() = v() + sectionHeight())
    private val radius = DoubleProperty(
        fullHeight() / (2 * Math.sin(Math.PI / 26))
    )
    fullHeight.onChange((v, _, _) => radius() = v() / (2 * Math.sin(Math.PI / 26)))


    val sections: Seq[StackPane] = (0 until 26).map(i => {
        val fromAngle = i * Cylinder.radAngle
        new StackPane { pane =>
            translateY = radius() * Math.sin(fromAngle)
            translateZ = radius() * Math.cos(fromAngle)
            radius.onChange((v, _, _) => {
                translateY = v() * Math.sin(fromAngle)
                translateZ = v() * Math.cos(fromAngle)
            })
            rotationAxis = Rotate.XAxis
            rotate = -fromAngle * 180 / Math.PI
            maxHeight <== sectionHeight
            maxWidth <== sectionWidth
            alignment = Pos.Center

            children = Seq(
                new Rectangle {
                    width <== sectionWidth
                    height <== sectionHeight
                    strokeWidth <== sectionStrokeWidth
                    stroke <== sectionStroke
                    fill <== sectionFill
                }

            ) ++ section(i, pane)
        }
    })

    children = sections

    onRotated = _ => orderSections()

    orderSections()

    def orderSections(): Unit = {
        val sortedSections = sections.sortBy(section => {
            section.localToScene(new Point3D(0, 0, 0)).getZ
        })
        sortedSections.foreach(_.toFront)
    }
}
