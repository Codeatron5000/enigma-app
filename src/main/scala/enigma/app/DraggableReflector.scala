package enigma.app

import enigma.machine.{ Reflector => MachineReflector }
import scalafx.geometry.{ Point2D, Pos }
import scalafx.scene.Group
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.{ Font, Text }

/**
 * A draggable group around the reflector that drops the reflector in the
 * rotor case if it's dragged there.
 * @param reflector The cylinder representing a reflector.
 * @param letter The letter classification of the reflector.
 */
case class DraggableReflector(reflector: Cylinder, letter: Char) extends VBox {
    var onPlaced: () => Unit = () => ()


    alignment = Pos.Center
    spacing = 20
    children = Seq(
        // The label of the reflector.
        new Text(letter.toString) {
            font = Font("Arial", 30)
            fill = Color.White
        },
        new Group {
            children = Seq(reflector)

            private var previousLocation: Option[Point2D] = None

            onDragDetected = e => {
                startFullDrag()
                previousLocation = Some(new Point2D(e.getSceneX, e.getSceneY))
            }

            onMouseDragged = e => {
                previousLocation.foreach(v => {
                    translateX = e.getSceneX - v.x
                    translateY = e.getSceneY - v.y
                })
            }

            onMouseDragReleased = _ => {
                // Placing the reflector in the rotor case and updating the
                // enigma property.
                val placed = RotorCase.dropReflector(reflector)
                if (placed) {
                    letter match {
                        case 'B' => EnigmaProperty.reflector = MachineReflector.B
                        case 'C' => EnigmaProperty.reflector = MachineReflector.C
                    }
                    onPlaced()
                }
                translateX = 0
                translateY = 0
                previousLocation = None
            }
        }
    )
}
