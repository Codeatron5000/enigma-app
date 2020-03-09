package enigma.app

import enigma.machine.Alphabet
import scalafx.geometry.Pos
import scalafx.scene.Cursor
import scalafx.scene.layout.{ HBox, StackPane, VBox }
import scalafx.scene.paint.Color.{ White, gray }
import scalafx.scene.shape.Circle
import scalafx.scene.text.{ Font, Text }

object Keyboard extends VBox {
    // A map of all the letters to their corresponding UI key.
    val keys = Map(
        Alphabet.alphabet.map(letter => {
            (letter, new Circle { circle =>
                radius = 15
                fill = gray(0.2)
                strokeWidth = 2
                stroke = gray(0.4)
            })
        }): _*
    )
    // A list of callbacks to run when a key is being pressed.
    private var keyDownCallbacks: Seq[Char => Unit] = Seq.empty
    // A list of callbacks to run when a key is released.
    private var keyUpCallbacks: Seq[Char => Unit] = Seq.empty
    // The key currently being pressed.
    private var pressedKey: Option[Char] = None

    /**
     * A method for registering callbacks to be run when a key is pressed.
     */
    def onKeyDown(cb: Char => Unit): Unit = {
        keyDownCallbacks = keyDownCallbacks :+ cb
    }

    /**
     * A method for registering callbacks to be run when a key is released.
     */
    def onKeyUp(cb: Char => Unit): Unit = {
        keyUpCallbacks = keyUpCallbacks :+ cb
    }

    /**
     * Manually press one of the keys on the keyboard.
     */
    def pressKey(letter: Char): Unit = {
        // First check it is a valid key and if there isn't already a key being
        // pressed.
        if (keys.contains(letter) && pressedKey.isEmpty) {
            // Get the corresponding node and highlight it.
            val circle = keys(letter)
            circle.strokeWidth = 4
            circle.radius = 14
            // Trigger the registered callbacks.
            keyDownCallbacks.foreach(cb => cb(letter))
            pressedKey = Some(letter)
        }
    }

    /**
     * Manually release one of the keys on the keyboard.
     */
    def releaseKey(letter: Char): Unit = {
        // First check it is a valid key and if it has been pressed.
        if (
            keys.contains(letter) &&
                pressedKey.nonEmpty &&
                pressedKey.get == letter
        ) {
            // Get the corresponding node and remove the highlighting.
            val circle = keys(letter)
            circle.strokeWidth = 2
            circle.radius = 15
            // Trigger the registered callbacks.
            keyUpCallbacks.foreach(cb => cb(letter))
            pressedKey = None
        }
    }

    spacing = 15

    children = KeypadOrder().map(row => {
        new HBox {
            alignment = Pos.Center
            spacing = 15
            children = row.map(c => {
                new StackPane {
                    private val circle = keys(c)
                    children = Seq(
                        circle,
                        new Text {
                            text = c.toString
                            fill = White
                            font = Font("Arial", 15)
                        }
                    )
                    cursor = Cursor.Hand
                    onMousePressed = _ => pressKey(c)
                    onMouseReleased = _ => releaseKey(c)
                }
            })
        }
    })
}
