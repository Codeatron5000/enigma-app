package enigma.app

import scalafx.beans.property.{ DoubleProperty, StringProperty }
import scalafx.scene.Cursor
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color.{ White, gray }
import scalafx.scene.shape.{ Circle, Rectangle }
import scalafx.scene.text.{ Font, Text }

/**
 * The little bar at the top that shows the encoded/decoded cipher stream.
 */
object CipherLine extends StackPane {

    private val sceneWidth = DoubleProperty(0)
    private var cipherStream = new StringProperty

    /**
     * Bind the cipher stream to the local property.
     * @param stream The string property that will be displayed on the line.
     * @return
     */
    def apply(stream: StringProperty): CipherLine.type = {
        cipherStream <== stream
        this
    }

    // Binding the line's width to the scene's width.
    scene.onChange((_, _, v) => sceneWidth <== v.widthProperty())

    children = Seq(
        new Rectangle {
            width <== sceneWidth
            height = 30
            fill = gray(0.5, 0.5)
        },
        new Text {
            text <== cipherStream
            fill = White
            font = Font("Noto Mono", 15)
        },
        // A button to clear the cipher stream.
        new StackPane {
            onMouseClicked = _ => cipherStream() = ""
            managed = false
            layoutX <== sceneWidth
            layoutY = 15
            translateX = -20
            cursor = Cursor.Hand
            children = Seq(
                Circle(10, gray(0.9, 0.8)),
                new Text("X") {
                    font = Font(15)
                }
            )
        }
    )
}
