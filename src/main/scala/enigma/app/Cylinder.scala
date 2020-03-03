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

/**
 * A rotatable cylinder of 26 sections. The size and contents of the sections
 * can be customised.
 * This class is used to build the different parts of the rotors and the
 * reflectors.
 * @param section A callback that returns a node which will be injected in each
 *                section.
 */
class Cylinder(
    private val section: (Int, Node) => Seq[Node] = (_, _) => Seq()
) extends StackPane with Rotatable {
    rotationAxis = Rotate.XAxis

    // The configurable properties.
    private val _sectionWidth = FloatProperty(20)
    private val _sectionHeight = FloatProperty(20)
    private val _sectionStroke = ObjectProperty[Paint](Black)
    private val _sectionStrokeWidth = DoubleProperty(1)
    private val _sectionFill = ObjectProperty[Paint](Gray)
    private val fullHeight = DoubleProperty(sectionHeight() + sectionStrokeWidth())
    private val radius = DoubleProperty(
        fullHeight() / (2 * Math.sin(Math.PI / 26))
    )

    // Create 26 sections and build 26 rectangles of the specified height and
    // width positioned and angled on all the faces of a 26 sided cylinder.
    val sections: Seq[StackPane] = (0 until 26).map(i => {
        // The angle of the current section.
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

            ) ++ section(i, pane) // Add any children returned by the callback.
        }
    })

    /**
     * The width of each section.
     * @return
     */
    def sectionWidth: FloatProperty = _sectionWidth

    def sectionWidth_=(value: Float): Unit = _sectionWidth() = value

    /**
     * The height of each section.
     * @return
     */
    def sectionHeight: FloatProperty = _sectionHeight

    def sectionHeight_=(value: Float): Unit = _sectionHeight() = value

    /**
     * The color of each section border.
     * @return
     */
    def sectionStroke: ObjectProperty[Paint] = _sectionStroke

    def sectionStroke_=(value: Paint): Unit = _sectionStroke() = value

    /**
     * The width of each section border.
     * @return
     */
    def sectionStrokeWidth: DoubleProperty = _sectionStrokeWidth

    def sectionStrokeWidth_=(value: Double): Unit = _sectionStrokeWidth() = value

    sectionHeight.onChange((v, _, _) => fullHeight() = v() + sectionStrokeWidth())
    sectionStrokeWidth.onChange((v, _, _) => fullHeight() = v() + sectionHeight())
    fullHeight.onChange((v, _, _) => radius() = v() / (2 * Math.sin(Math.PI / 26)))

    /**
     * The color of each section.
     * @return
     */
    def sectionFill: ObjectProperty[Paint] = _sectionFill

    def sectionFill_=(value: Paint): Unit = _sectionFill() = value

    children = sections

    onRotated = _ => orderSections()

    orderSections()

    /**
     * Loop through all the sections and make sure they are in the correct order
     * on the screen.
     */
    def orderSections(): Unit = {
        // Order each section by their Z position relative to the scene.
        val sortedSections = sections.sortBy(section => {
            section.localToScene(new Point3D(0, 0, 0)).getZ
        })
        // Put them all to the front in order from back to front.
        sortedSections.foreach(_.toFront)
    }
}
