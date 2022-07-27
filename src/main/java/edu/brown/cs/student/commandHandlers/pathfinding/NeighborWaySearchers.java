package edu.brown.cs.student.commandHandlers.pathfinding;

import edu.brown.cs.student.pathfinding.GraphEdge;
import edu.brown.cs.student.pathfinding.GraphNode;
import edu.brown.cs.student.pathfinding.GraticuleNode;

import java.sql.SQLException;
import java.util.Set;

/** class that searches for ways that connect a specific node in database.
 */
public final class NeighborWaySearchers {
  private NeighborWaySearchers() {
  }

  /** queries ways that start from given node.
   * @param node is the given node
   * @return a set of pathEdges of String id that are made of GraticuleNodes with also String ids
   * @throws NullPointerException if no database has been loaded.
   */
  public static Set<GraphEdge<String, String, GraticuleNode>> getWaysFromTargetNode(
      GraphNode<String> node)
    throws NullPointerException {
    Set<GraphEdge<String, String, GraticuleNode>> results;
    try {
      results = MapDBResultSetHandler.queryDBGetWaysAroundTarget(
          "SELECT way.id AS wayID, way.name, way.type, way.start, way.end,\n"
              + "N1.latitude as lat1, N1.longitude as lon1,\n"
              + "N2.latitude as lat2, N2.longitude as lon2\n"
              + "FROM way\n"
              + "INNER JOIN node as N1\n"
              + "INNER JOIN node as N2\n"
              + "ON (way.start=N1.id) AND (way.end=N2.id)" + "\n"
              + "WHERE way.start='" + node.getId() + "'\n"
              + "AND way.type!='unclassified' AND way.type!=''"
              + ";"
      );
    } catch (SQLException e) {
      throw new IllegalAccessError();
    }
    return results;
  }
}
