package enigma.app

import enigma.machine.{ Rotor => MachineRotor }
import scalafx.animation.{ KeyFrame, KeyValue, Timeline }
import scalafx.beans.property.IntegerProperty
import scalafx.geometry.Pos
import scalafx.scene.effect.DropShadow
import scalafx.scene.{ Cursor, Group, Node }
import scalafx.scene.layout.{ HBox, StackPane, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Black
import scalafx.scene.shape.{ Rectangle, Shape }
import scalafx.scene.text.{ Font, Text }
import scalafx.scene.transform.Rotate
import scalafx.util.Duration

class RotorCase extends StackPane {
    private val holes = (0 until 3).map(i => {
        Rectangle(89 + 70 * i, 98, 22, 25)
    })

    private val borders = (0 until 3).map(_ => {
        val border = Shape.subtract(
            Rectangle(30, 33),
            Rectangle(4, 4, 22, 25)
        )
        border.fill = Color.Gray
        border
    })

    private var sheet: Shape = new Rectangle {
        height = 220
        width = 300
        fill = Black
    }

    holes.foreach(hole => {
        sheet = Shape.subtract(sheet, hole)
    })

    private var cylinders: Seq[Node] = Seq(
        Rectangle(40, 100),
        Rectangle(40, 100),
        Rectangle(40, 100),
    )

    private var reflector: Node = Rectangle(30, 150)

    private val rotorBox: HBox = new HBox {
        alignment = Pos.Center
        spacing = 30
    }

    def buildRotors(): Unit = {
        rotorBox.children = reflector +: cylinders
    }

    buildRotors()

    def dropRotor(r: Rotor): Boolean = {
        var placed = false
        cylinders = cylinders.map(n => {
            if (
                !placed &&
                n.isInstanceOf[Rectangle] &&
                r.localToScene(r.boundsInLocal()).intersects(n.localToScene(n.boundsInLocal()))
            ) {
                placed = true
                r.disableDrag = false
                r
            } else {
                n
            }
        })
        if (placed) {
            buildRotors()
        }
        placed
    }

    def dropReflector(r: Cylinder): Boolean = {
        if (
            reflector.isInstanceOf[Rectangle] &&
                r.localToScene(r.boundsInLocal()).intersects(reflector.localToScene(reflector.boundsInLocal()))
        ) {
            reflector = r
            buildRotors()
            true
        } else false
    }

    def removeRotor(r: Rotor): Unit = {
        cylinders = cylinders.map(c => {
            if (c == r) Rectangle(30, 150) else c
        })
        buildRotors()
    }

    def removeReflector(): Unit = {
        reflector = Rectangle(40, 100)
        buildRotors()
    }

    children = Seq(
        new Rectangle {
            height = 220
            width = 300
            fill = Color.gray(0.2)
        },
        rotorBox,
        new StackPane { cover =>
            private var open = false

            private val rotation = new Rotate {
                pivotX <== cover.translateX
                pivotY <== cover.translateY
                axis = Rotate.XAxis
            }

            transforms.add(rotation)

            cursor = Cursor.OpenHand

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
