package enigma.app

import enigma.machine.{
  Alphabet,
  Enigma,
  PlugBoardConflictException,
  PlugBoardOverloadException,
  PlugBoard => MachinePlugBoard
}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Cursor, Scene}
import scalafx.scene.layout.{HBox, Pane, VBox}
import scalafx.scene.paint.Color.{Black, White, gray}
import scalafx.scene.shape.{Circle, Line, Rectangle, StrokeLineCap}
import scalafx.scene.text.{Font, Text}
import scalafx.Includes._

class PlugBoard(scene: Scene, enigma: EnigmaProperty) extends Pane {pane =>

  var newConnection: Option[(Line, Circle, Char)] = None

  private val linkPane: Pane = new Pane {linkPane =>
    pickOnBounds = false
  }

  /*
   * First build up a map of all the letters with their corresponding circle
   * node for easy access later on.
   */
  private val letterConnectors = Map(
    Alphabet.alphabet.map(letter => {
      (letter, new Circle {circle =>
        radius = 5
        fill = Black
        strokeWidth = 3
        stroke = gray(0.3)
        cursor = Cursor.Hand
        onMousePressed = e => {
          e.setDragDetect(true)
        }
        onDragDetected  = e => {
          val relativeStart = Utils.getRelativeCenter(circle, pane)
          circle.startFullDrag()

          val newLine = new Line {
            fill = White
            stroke = White
            strokeWidth = 5
            strokeLineCap = StrokeLineCap.Round
            startX = relativeStart.x
            startY = relativeStart.y
            endX = relativeStart.x + e.getX
            endY = relativeStart.y + e.getY
          }

          /*
           * When they start to drag from a connector we save the connector
           * node, the letter and the line to manipulate later.
           */
          newConnection = Some((newLine, circle, letter))

          linkPane.children.add(newLine)
        }
        onMouseDragged = e => {
          if (newConnection.nonEmpty) {
            /*
             * Update the line coordinates whenever they move the mouse
             * but don't let the line go beyond the bounds of the plug board.
             */
            val line = newConnection.get._1
            val x = line.startX() + e.getX
            val y = line.startY() + e.getY
            line.endX = if (x <= 0) {
              10
            } else if (x >= pane.width() - 10) {
              pane.width() - 10
            } else x
            line.endY = if (y <= 0) {
              10
            } else if (y >= pane.height() - 10) {
              pane.height() - 10
            } else y
          }
        }
      })
    }) : _*
  )

  private def removeNewConnection(): Unit = {
    if (newConnection.nonEmpty) {
      linkPane.children.remove(linkPane.children.length - 1)
      newConnection = None
    }
  }

  linkPane.onMouseDragReleased = e => {
    val closestCircle = letterConnectors.find(t => {
      val relativeCenter = Utils.getRelativeCenter(t._2, linkPane)
      Math.abs(relativeCenter.x - e.getX) < 10 &&
        Math.abs(relativeCenter.y - e.getY) < 10
    })

    if (closestCircle.nonEmpty) {
      /*
       * When the drag is released we find the closest circle (within 10 pixels)
       * create a new permanent connection line and add the substitution to the
       * enigma simulator, then remove the temporary line.
       */
      val circle = closestCircle.get._2
      val letter = closestCircle.get._1
      if (newConnection.nonEmpty && newConnection.get._2 != circle) {
        val (_, initialCircle, initialLetter) = newConnection.get
        try {
          val newSub = (initialLetter, letter)
          enigma.addConnection(newSub)

          removeNewConnection()

          linkPane.children.add(new Connection(
            initialCircle,
            circle,
            newSub,
            linkPane,
            enigma
          ))
        } catch {
          case _: PlugBoardConflictException =>
            println("Plug board conflict")
          case _: PlugBoardOverloadException =>
            println("Too many plug board connections")
        }
      }
    } else removeNewConnection()
  }

  linkPane.onMouseDragExited = _ => removeNewConnection()

  private val connections = enigma.connections().map(sub => {

    val firstConnector: Circle = letterConnectors(sub._1)
    val secondConnector: Circle = letterConnectors(sub._2)

    new Connection(firstConnector, secondConnector, sub, linkPane, enigma)
  })

  /*
   * Because all the connections are created before the scene is fully loaded we
   * need to put them all in the right place when the scene width changes.
   */
  scene.width.onChange((_, _, _) => connections.map(c => c.refreshCoords()))

  linkPane.children = connections

  private val sceneWidth = scene.width
  minWidth <== sceneWidth
  maxWidth <== sceneWidth
  children = Seq(
    new Rectangle {
      width <== pane.width
      height <== pane.height
      fill = Black
    },
    new VBox {
      minWidth <== sceneWidth
      spacing = 15
      padding = Insets(20)
      children = KeypadOrder().map(row => {
        new HBox {
          alignment = Pos.Center
          spacing = 25
          children = row.map(c => {
            new VBox {
              spacing = 10
              children = Seq(
                new Text {
                  text = c.toString
                  fill = White
                  font = Font(10)
                },
                letterConnectors(c)
              )
            }
          })
        }
      })
    },
    linkPane
  )
}
