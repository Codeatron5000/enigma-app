package enigma.app

import enigma.machine.{ Rotor => MachineRotor }
import scalafx.geometry.{ Point2D, Pos }
import scalafx.scene.Group
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.{ Font, Text }

/**
 * A draggable group around the rotor that drops the rotor in the rotor case if
 * it's dragged there.
 * @param rotor The rotor node.
 */
case class DraggableRotor(rotor: Rotor) extends VBox {
    var onPlaced: Int => Unit = _ => ()

    alignment = Pos.Center
    spacing = 20
    children = Seq(
        // The label of the rotor.
        new Text(rotor.rotor() match {
            case MachineRotor.I => "I"
            case MachineRotor.II => "II"
            case MachineRotor.III => "III"
            case MachineRotor.IV => "IV"
            case MachineRotor.V => "V"
        }) {
            font = Font("Arial", 30)
            fill = Color.White
        },
        new Group {
            children = Seq(rotor)
            rotor.disableDrag = true

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
                // Placing the rotor in the rotor case and updating the enigma
                // property.
                val placed = RotorCase.dropRotor(rotor)
                placed.foreach(v => {
                    v match {
                        case 0 => EnigmaProperty.slowRotor = rotor.rotor
                        case 1 => EnigmaProperty.mediumRotor = rotor.rotor
                        case 2 => EnigmaProperty.fastRotor = rotor.rotor
                    }
                    onPlaced(v)
                })
                translateX = 0
                translateY = 0
                previousLocation = None
            }
        }
    )
}
