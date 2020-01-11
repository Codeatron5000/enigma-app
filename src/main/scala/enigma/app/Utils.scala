package enigma.app

import scalafx.geometry.Point2D
import scalafx.scene.Node

object Utils {
  /**
   * Calculate the x and y coordinates of a node relative to another node
   *
   * @param node The inner node
   * @param to The parent node
   * @return The coordinates of the node relative to the specified parent
   */
  def getRelativeCenter(node: Node, to: Node): Point2D = {
    // Get the bounding rectangle relative to the whole scene.
    val nodeBoundsInScene = node.localToScene(node.getBoundsInLocal)
    // Convert the bounding rectangle local to the parent node.
    val bounds = to.sceneToLocal(nodeBoundsInScene)
    // For some reason the x coordinates do not take into account the margins of the parent
    // so in this case we are adding on the margins to the coordinates.
    new Point2D(
      bounds.getMinX + bounds.getWidth / 2,
      bounds.getMinY + bounds.getHeight / 2
    )
  }
}
