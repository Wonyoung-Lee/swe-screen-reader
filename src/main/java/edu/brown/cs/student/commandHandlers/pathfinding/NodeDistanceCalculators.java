package edu.brown.cs.student.commandHandlers.pathfinding;

import edu.brown.cs.student.pathfinding.GraticuleNode;

/** Class that calculates the distance between nodes using Haversine.
 */
public final class NodeDistanceCalculators {
  private NodeDistanceCalculators() {
  }

  /** Get the Haversine distance between node1 and node2.
   * @param node1 is a GraticuleNode
   * @param node2 is a GraticuleNode
   * @return a double that is the distance between them.
   */
  public static double getHaversineDistance(GraticuleNode node1,
                                            GraticuleNode node2) {
    final double r = 6371.0088;
    final double phi1 = node1.getLatitude() * Math.PI / 180;
    final double phi2 = node2.getLatitude() * Math.PI / 180;
    final double lambda1 = node1.getLongitude() * Math.PI / 180;
    final double lambda2 = node2.getLongitude() * Math.PI / 180;

    return 2 * r * Math.asin(
        Math.sqrt(Math.pow(Math.sin((phi2 - phi1) / 2), 2)
            + Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin((lambda2 - lambda1) / 2), 2)));
  }
}
