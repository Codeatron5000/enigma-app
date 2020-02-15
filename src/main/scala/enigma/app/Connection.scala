package enigma.app

import scalafx.geometry.Point2D
import scalafx.scene.Cursor
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color.{ Transparent, White }
import scalafx.scene.shape.{ Circle, CubicCurve, StrokeLineCap }

class Connection(
    firstConnector: Circle,
    secondConnector: Circle,
    linkPane: Pane,
) extends CubicCurve {
    def refreshCoords(): Unit = {
        val firstCenter = Utils.getRelativeCenter(firstConnector, linkPane)
        startX = firstCenter.x
        startY = firstCenter.y

        val secondCenter = Utils.getRelativeCenter(secondConnector, linkPane)
        endX = secondCenter.x
        endY = secondCenter.y

        val middle = middlePoint(startX(), startY(), endX(), endY())
        controlX1 = middle.x
        controlY1 = middle.y
        controlX2 = middle.x
        controlY2 = middle.y
    }

    def middlePoint(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double
    ): Point2D = {
        val xDiff = Math.abs((endX - startX) / 2)
        val yDiff = Math.abs((endY - startY) / 2)
        val length = Math.sqrt(xDiff * xDiff + yDiff * yDiff)
        val distance = length / 4
        val hyp = Math.sqrt(distance * distance + length * length)
        val smallAngle = Math.atan(distance / length)
        val bigAngle = Math.atan(yDiff / xDiff)
        val angle = bigAngle - smallAngle
        var xOffset = hyp * Math.cos(angle)
        val yOffset = hyp * Math.sin(angle)

        var relX = 0.0
        val relY = Math.max(startY, endY)
        if ((endX - startX) * (endY - startY) < 0) {
            xOffset *= -1
            relX = Math.min(startX, endX)
        } else {
            relX = Math.max(startX, endX)
        }

        new Point2D(relX - xOffset, relY - yOffset)
    }

    refreshCoords()

    firstConnector.layoutX.onChange((_, _, _) => refreshCoords())
    firstConnector.layoutY.onChange((_, _, _) => refreshCoords())
    secondConnector.layoutX.onChange((_, _, _) => refreshCoords())
    secondConnector.layoutY.onChange((_, _, _) => refreshCoords())

    fill = Transparent
    stroke = White
    strokeWidth = 5
    strokeLineCap = StrokeLineCap.Round
    cursor = Cursor.Crosshair
}
