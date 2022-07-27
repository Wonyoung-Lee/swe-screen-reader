package edu.brown.cs.student.commandHandlers.pathfinding;

import edu.brown.cs.student.coordinates.KdTree;
import edu.brown.cs.student.database.DatabaseHandler;
import edu.brown.cs.student.pathfinding.GraticuleEdge;
import edu.brown.cs.student.pathfinding.GraticuleNode;
import edu.brown.cs.student.pathfinding.GraphEdge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Class that handles pathfinding database queries.
 */
public final class MapDBResultSetHandler {
  private MapDBResultSetHandler() {
  }

  /** Queries database for ways and converts data into a list of ways.
   * @param query is a series of SQL commands to access data
   * @return a list of GraticuleEdge
   * @throws SQLException if SQL fails to execute commands
   * @throws NullPointerException if no database has been loaded.
   * @throws IllegalArgumentException if the column names and length are incorrect
   */
  public static List<GraticuleEdge> queryDBWays(String query)
    throws SQLException, NullPointerException, IllegalArgumentException {
    List<GraticuleEdge> queriedGraticuleEdges = new ArrayList<>();
    ResultSet rs = DatabaseHandler.queryLoadedDB(query);

    final int wayIDIndex = 1;
    final int wayNameIndex = 2;
    final int typeIndex = 3;
    final int startNodeIDIndex = 4;
    final int endNodeIDIndex = 5;
    final int lat1Index = 6;
    final int lon1Index = 7;
    final int lat2Index = 8;
    final int lon2Index = 9;

    if (!isValid()) {
      throw new IllegalArgumentException();
    }
    if (!rs.isClosed()) {
      while (rs.next()) {
        String wayID = rs.getString(wayIDIndex);
        String wayName = rs.getString(wayNameIndex);
        String type = rs.getString(typeIndex);
        String startNodeID = rs.getString(startNodeIDIndex);
        String endNodeID = rs.getString(endNodeIDIndex);

        double lat1 = Double.parseDouble(rs.getString(lat1Index));
        double lon1 = Double.parseDouble(rs.getString(lon1Index));
        GraticuleNode startNode = new GraticuleNode(startNodeID, lat1, lon1);

        double lat2 = Double.parseDouble(rs.getString(lat2Index));
        double lon2 = Double.parseDouble(rs.getString(lon2Index));
        GraticuleNode endNode = new GraticuleNode(endNodeID, lat2, lon2);

        queriedGraticuleEdges.add(new GraticuleEdge(wayID, wayName, type, startNode, endNode));
      }
    }
    rs.close();

    return queriedGraticuleEdges;
  }

  /**
   * Given a Node finds the first two streets intersections at that node.
   *
   * @param query - a list of GraticuleEdge
   * @return a list a streets intersecting at the given node
   * @throws SQLException if SQL fails to execute commands
   * @throws NullPointerException if no database has been loaded.
   * @throws IllegalArgumentException if the column names and length are incorrect
   */
  public static List<String> queryDBStreetIntersections(String query)
    throws SQLException, NullPointerException, IllegalArgumentException {
    List<String> potentialStreetIntersections = new ArrayList<>();
    ResultSet rs = DatabaseHandler.queryLoadedDB(query);

    if (!isValid()) {
      throw new IllegalArgumentException();
    }
    if (!rs.isClosed()) {
      while (rs.next()) {
        potentialStreetIntersections.add(rs.getString(1));
      }
    }
    rs.close();

    return potentialStreetIntersections;
  }

  /** Queries the column names of tables way and node to check if they're valid.
   * @return true or false depending on their validity
   */
  public static boolean isValid() {
    try {
      ResultSet rs = DatabaseHandler.queryLoadedDB(
          "SELECT c.name FROM pragma_table_info('node') c;");
      List<String> nodeCols = new ArrayList<>();
      while (rs.next()) {
        nodeCols.add(rs.getString(1));
      }
      boolean isNodeValid = nodeCols.size() == 3
          && nodeCols.get(0).equals("id")
          && nodeCols.get(1).equals("latitude")
          && nodeCols.get(2).equals("longitude");

      rs = DatabaseHandler.queryLoadedDB("SELECT c.name FROM pragma_table_info('way') c;");
      List<String> wayCols = new ArrayList<>();
      while (rs.next()) {
        wayCols.add(rs.getString(1));
      }
      boolean isWayValid = wayCols.size() == 5
          && wayCols.get(0).equals("id")
          && wayCols.get(1).equals("name")
          && wayCols.get(2).equals("type")
          && wayCols.get(3).equals("start")
          && wayCols.get(4).equals("end");
      return isNodeValid && isWayValid;
    } catch (NullPointerException | SQLException e) {
      return false;
    }
  }

  /** Queries database for traversable nodes and converts node data into a KDTree.
   * @param query is a series of SQL commands
   * @return a KDTree built from queried nodes
   * @throws SQLException if SQL fails to query
   * @throws NullPointerException if no database has been loaded.
   * @throws IllegalArgumentException if the column names and length are incorrect
   * @throws IllegalAccessException if way and node ids are empty
   */
  public static KdTree<String, GraticuleNode> queryDBNodes(String query)
      throws SQLException, NullPointerException, IllegalArgumentException, IllegalAccessException {
    HashMap<String, GraticuleNode> queriedNodes = new HashMap<>();
    ResultSet rs = DatabaseHandler.queryLoadedDB(query);

    final int startNodeIDIndex = 1;
    final int startNodeLatIndex = 2;
    final int startNodeLonIndex = 3;
    final int endNodeIDIndex = 4;
    final int endNodeLatIndex = 5;
    final int endNodeLonIndex = 6;
    final int wayIndex = 7;

    if (!isValid()) {
      throw new IllegalArgumentException();
    }
    if (!rs.isClosed()) {
      while (rs.next()) {
        String startNodeID = rs.getString(startNodeIDIndex);
        double startNodeLat = Double.parseDouble(rs.getString(startNodeLatIndex));
        double startNodeLon = Double.parseDouble(rs.getString(startNodeLonIndex));
        String endNodeID = rs.getString(endNodeIDIndex);
        double endNodeLat = Double.parseDouble(rs.getString(endNodeLatIndex));
        double endNodeLon = Double.parseDouble(rs.getString(endNodeLonIndex));
        String wayID = rs.getString(wayIndex);
        //if ids are empty
        if (startNodeID.equals("") || endNodeID.equals("") || wayID.equals("")) {
          throw new IllegalAccessException();
        }
        //if queriedNodes doesn't have start node
        if (!(queriedNodes.containsKey(startNodeID))) {
          queriedNodes.put(startNodeID,
              new GraticuleNode(startNodeID, startNodeLat, startNodeLon));
        }
        //if queriedNodes doesn't have end node
        if (!(queriedNodes.containsKey(endNodeID))) {
          queriedNodes.put(endNodeID,
              new GraticuleNode(endNodeID, endNodeLat, endNodeLon));
        }
      }
    }
    rs.close();
    KdTree<String, GraticuleNode> kdTree = new KdTree<>(2,
        new ArrayList<>(queriedNodes.values()));
    kdTree.buildTree();
    return kdTree;
  }

  /** Queries for ways that start from a specific target node.
   * @param query is a series of SQL commands
   * @return a set of path edges of string ids made from Graticule nodes of string ids
   * @throws SQLException if SQL fails to execute command
   * @throws NullPointerException if no database has been loaded.
   * @throws IllegalArgumentException if the column names and length of the table are incorrect
    */
  public static Set<GraphEdge<String, String, GraticuleNode>>
              queryDBGetWaysAroundTarget(String query)
      throws SQLException, NullPointerException, IllegalArgumentException {
    Set<GraphEdge<String, String, GraticuleNode>> queriedWays = new HashSet<>();
    ResultSet rs = DatabaseHandler.queryLoadedDB(query);

    final int wayIDIndex = 1;
    final int wayNameIndex = 2;
    final int typeIndex = 3;
    final int startNodeIDIndex = 4;
    final int endNodeIDIndex = 5;
    final int lat1Index = 6;
    final int lon1Index = 7;
    final int lat2Index = 8;
    final int lon2Index = 9;

    if (!isValid()) {
      throw new IllegalArgumentException();
    }
    if (!rs.isClosed()) {
      while (rs.next()) {
        String wayID = rs.getString(wayIDIndex);
        String wayName = rs.getString(wayNameIndex);
        String type = rs.getString(typeIndex);
        String startNodeID = rs.getString(startNodeIDIndex);
        String endNodeID = rs.getString(endNodeIDIndex);

        double lat1 = Double.parseDouble(rs.getString(lat1Index));
        double lon1 = Double.parseDouble(rs.getString(lon1Index));
        GraticuleNode startNode = new GraticuleNode(startNodeID, lat1, lon1);

        double lat2 = Double.parseDouble(rs.getString(lat2Index));
        double lon2 = Double.parseDouble(rs.getString(lon2Index));
        GraticuleNode endNode = new GraticuleNode(endNodeID, lat2, lon2);

        queriedWays.add(new GraticuleEdge(wayID, wayName, type, startNode, endNode));
      }
    }
    rs.close();
    return queriedWays;
  }
}
