package enigma.app

import scalafx.animation.{ KeyFrame, KeyValue, Timeline }
import scalafx.geometry.Pos
import scalafx.scene.layout.{ HBox, StackPane }
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Black
import scalafx.scene.shape.{ Rectangle, Shape }
import scalafx.scene.transform.Rotate
import scalafx.scene.{ Cursor, Node }
import scalafx.util.Duration

/**
 * The case that holds the three rotors and the reflector.
 * It has three slots for each rotor that drop into place and one longer slot
 * on the left for the reflector. There is also a cover that has three holes in
 * the top to be able to see the position of the rotors.
 */
object RotorCase extends StackPane {
    // The three holes that will be subtracted from the case.
    private val holes = (0 until 3).map(i => {
        Rectangle(89 + 70 * i, 98, 22, 25)
    })
    // The grey borders that surround the holes.
    private val borders = (0 until 3).map(_ => {
        val border = Shape.subtract(
            Rectangle(30, 33),
            Rectangle(4, 4, 22, 25)
        )
        border.fill = Color.Gray
        border
    })
    // The node that contains the rotors and reflector.
    private val rotorBox: HBox = new HBox {
        alignment = Pos.Center
        spacing = 30
    }
    // A boolean indicating if the case is open or closed.
    private var open = false

    // The cover that will have the holes removed.
    private var sheet: Shape = new Rectangle {
        height = 220
        width = 300
        fill = Black
    }

    holes.foreach(hole => {
        sheet = Shape.subtract(sheet, hole)
    })

    // The placeholders for the rotors and reflector.
    private var cylinders: Seq[Node] = Seq(
        Rectangle(40, 100),
        Rectangle(40, 100),
        Rectangle(40, 100),
    )
    private var reflector: Node = Rectangle(30, 150)


    /**
     * Place the rotors or the placeholders in the rotor case based on the
     * cylinders / reflector variables.
     */
    def buildRotors(): Unit = {
        rotorBox.children = reflector +: cylinders
    }

    /**
     * Drop a rotor in position in the case if it is intersecting one of the
     * placeholders and the case is open.
     * @param r The rotor node to be dropped inside the rotor case.
     * @return
     */
    def dropRotor(r: Rotor): Option[Int] = {
        var placed: Option[Int] = None
        if (open) {
            cylinders = cylinders.indices.map(i => {
                val n = cylinders(i)
                if (
                    placed.isEmpty &&
                        n.isInstanceOf[Rectangle] &&
                        r.localToScene(r.boundsInLocal()).intersects(n.localToScene(n.boundsInLocal()))
                ) {
                    placed = Some(i)
                    r.disableDrag = false
                    r
                } else {
                    n
                }
            })
            // If the rotor was placed in the case, rebuild the case.
            placed.foreach(_ => {
                buildRotors()
            })
        }
        placed
    }

    buildRotors()

    /**
     * Drop a reflector into the rotor case if it is intersecting the rotor
     * placeholder and the case is open.
     * @param r The reflector node to be dropped inside the rotor case.
     * @return
     */
    def dropReflector(r: Cylinder): Boolean = {
        if (
            open &&
                reflector.isInstanceOf[Rectangle] &&
                r.localToScene(r.boundsInLocal()).intersects(reflector.localToScene(reflector.boundsInLocal()))
        ) {
            reflector = r
            buildRotors()
            true
        } else {
            false
        }
    }

    /**
     * Remove a rotor from the case.
     * @param r The rotor to be removed.
     */
    def removeRotor(r: Rotor): Unit = {
        cylinders = cylinders.map(c => {
            if (c == r) Rectangle(40, 100) else c
        })
        buildRotors()
    }

    /**
     * Remove the reflector from the case if it is there.
     */
    def removeReflector(): Unit = {
        reflector = Rectangle(30, 150)
        buildRotors()
    }

    maxWidth = 300

    children = Seq(
        new Rectangle {
            height = 220
            width = 300
            fill = Color.gray(0.2)
        },
        rotorBox,
        new StackPane { cover =>

            private val rotation = new Rotate {
                pivotX <== cover.translateX
                pivotY <== cover.translateY
                axis = Rotate.XAxis
            }

            transforms.add(rotation)

            cursor = Cursor.OpenHand

            // Open and close the cover when it is clicked.
            onMouseClicked = _ => {
                Timeline(
                    Seq(
                        KeyFrame(
                            Duration.Zero,
                            null, null,
                            Set(KeyValue(rotation.angle, if (open) 85 else 0)),
                        ),
                        KeyFrame(
                            Duration(1000),
                            null, null,
                            Set(KeyValue(rotation.angle, if (open) 0 else 85)),
                        ),
                    )
                ).play()

                open = !open
            }

            children = Seq(
                sheet,
                new HBox {
                    alignment = Pos.Center
                    spacing = 40
                    translateX = 20
                    children = borders
                }
            )
        },
    )
}
