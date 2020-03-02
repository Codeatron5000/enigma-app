package enigma.app

import enigma.machine.{ Alphabet, PlugBoardConflictException, PlugBoardOverloadException }
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.layout.{ HBox, Pane, VBox }
import scalafx.scene.paint.Color.{ Black, White, gray }
import scalafx.scene.shape.{ Circle, Line, Rectangle, StrokeLineCap }
import scalafx.scene.text.{ Font, Text }
import scalafx.scene.Scene

class PlugBoard extends Pane { pane =>

    /**
     * The node that contains all the connections.
     */
    private val connectionPane: Pane = new Pane { linkPane =>
        pickOnBounds = false
    }
    private val letterConnectors: Map[Char, Circle] = Map(
        Alphabet.alphabet.map(letter => {
            (letter, new Circle { circle =>
                radius = 5
                fill = Black
                strokeWidth = 3
                stroke = gray(0.3)
                cursor = Cursor.Hand
                onMousePressed = e => {
                    e.setDragDetect(true)
                }
                onDragDetected = e => {
                    // Calculate the coordinates of the circle relative to the
                    // plug board node.
                    val relativeStart = Utils.getRelativeCenter(circle, pane)
                    startFullDrag()

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

                    // When they start to drag from a connector we save the
                    // the letter and the line to manipulate later.
                    newConnection = Some((newLine, letter))

                    // We add the new temporary line to the connection pane so
                    // they know where they are dragging.
                    connectionPane.children.add(newLine)
                }
                onMouseDragged = e => {
                    newConnection.foreach(value => {
                        // Update the line coordinates whenever they move the
                        // mouse but don't let the line go beyond the bounds of
                        // the plug board.
                        val line = value._1
                        val x = line.startX() + e.getX
                        val y = line.startY() + e.getY
                        line.endX = x match {
                            case x if x <= 0 => 10
                            case x if x >= pane.width() - 10 => pane.width() - 10
                            case x => x
                        }
                        line.endY = y match {
                            case y if y <= 0 => 10
                            case y if y >= pane.height() - 10 => pane.height() - 10
                            case y => y
                        }
                    })
                }
            })
        }): _*
    )
    var newConnection: Option[(Line, Char)] = None
    // A list of callbacks to run when a key is being pressed.
    private var connectionAddedCallbacks: Seq[((Char, Char)) => Unit] = Seq.empty
    // A list of callbacks to run when a key is released.
    private var connectionRemovedCallbacks: Seq[((Char, Char)) => Unit] = Seq.empty
    private val connections = Seq.empty[Connection]

    /**
     * A method for registering callbacks to be run when a connection is added.
     */
    def onConnectionAdded(cb: ((Char, Char)) => Unit): Unit = {
        connectionAddedCallbacks = connectionAddedCallbacks :+ cb
    }

    /**
     * A method for registering callbacks to be run when a connection is removed.
     */
    def onConnectionRemoved(cb: ((Char, Char)) => Unit): Unit = {
        connectionRemovedCallbacks = connectionRemovedCallbacks :+ cb
    }

    /**
     * Create a new connection line and add it to the connection pane.
     */
    private def addConnection(sub: (Char, Char)) = {
        val firstCircle = letterConnectors(sub._1)
        val secondCircle = letterConnectors(sub._2)
        val connection = new Connection(
            firstCircle,
            secondCircle,
            connectionPane
        ) {
            onMouseClicked = _ => {
                connectionRemovedCallbacks.foreach(cb => cb(sub))
                removeConnection(this)
            }
        }
        connectionPane.children.add(connection)
        connection
    }

//    connectionsProperty.onChange((c, _, _) => {
//        connections.foreach(removeConnection)
//        connections = c().map(addConnection)
//    })

    /**
     * Remove a connection from the connection pane.
     */
    private def removeConnection(connection: Connection) = {
        connectionPane.children.remove(connection)
    }

    // When the drag is over remove the temporary connection and add the new
    // permanent connection to the closest circle.
    connectionPane.onMouseDragReleased = e => {
        val closestCircle = letterConnectors.find(t => {
            val relativeCenter = Utils.getRelativeCenter(t._2, connectionPane)
            Math.abs(relativeCenter.x - e.getX) < 10 &&
                Math.abs(relativeCenter.y - e.getY) < 10
        })

        closestCircle.foreach(value => {
            // When the drag is released we find the closest circle (within 10
            // pixels) create a new permanent connection line and add the
            // substitution to the enigma simulator, then remove the temporary
            // line.
            val letter = value._1
            if (newConnection.nonEmpty && newConnection.get._2 != letter) {
                val (_, initialLetter) = newConnection.get
                try {
                    val newSub = (initialLetter, letter)
                    connectionAddedCallbacks.foreach(cb => cb(newSub))

                    addConnection(newSub)
                } catch {
                    case _: PlugBoardConflictException =>
                        println("Plug board conflict")
                    case _: PlugBoardOverloadException =>
                        println("Too many plug board connections")
                }
            }
        })

        removeNewConnection()
    }

    connectionPane.onMouseDragExited = _ => removeNewConnection()

    /*
     * Because all the connections are created before the scene is fully loaded we
     * need to put them all in the right place when the scene width changes.
     */
    scene.onChange((v, _, _) => {
        if (v != null) {
            minWidth <== v().widthProperty()
            maxWidth <== v().widthProperty()
            connections.foreach(c => c.refreshCoords())
            v().widthProperty().addListener((_, _, _) => connections.map(c => c.refreshCoords()))
        }
    })

    connectionPane.children = connections

    private def removeNewConnection(): Unit = {
        newConnection.foreach(value => {
            connectionPane.children.remove(value._1)
            newConnection = None
        })
    }

    minWidth = 600
    maxWidth = 600
    children = Seq(
        new Rectangle {
            width <== pane.width
            height <== pane.height
            fill = Black
        },
        new VBox {
            minWidth <== pane.width
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
        connectionPane
    )
}
