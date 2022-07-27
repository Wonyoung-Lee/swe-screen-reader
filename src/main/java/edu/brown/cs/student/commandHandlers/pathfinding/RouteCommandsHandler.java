package edu.brown.cs.student.commandHandlers.pathfinding;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.student.database.DatabaseHandler;
import edu.brown.cs.student.pathfinding.DijkstraAStar;
import edu.brown.cs.student.pathfinding.GraphEdge;
import edu.brown.cs.student.pathfinding.GraphNode;
import edu.brown.cs.student.pathfinding.GraticuleEdge;
import edu.brown.cs.student.pathfinding.GraticuleNode;
import edu.brown.cs.student.pathfinding.HeuristicFuncs;
import edu.brown.cs.student.main.ErrorMessages;
import edu.brown.cs.student.utils.Utils;

import java.awt.geom.IllegalPathStateException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Class that checks and executes the route command
 by loading a database database and extracting necessary data.
 */
public final class RouteCommandsHandler {
  private RouteCommandsHandler() {
  }

  private static Utils.Function3To1<List<GraphEdge<String, String, GraticuleNode>>,
          GraphNode<String>, GraphNode<String>, String> parseFunc;

  private static final Map<String,
      Utils.Function3To1<List<GraphEdge<String, String, GraticuleNode>>,
              GraphNode<String>, GraphNode<String>, String>> VALID_PARSERS
      = new HashMap<>() {{
            put("repl", RouteCommandsHandler::parseToRepl);
        }};

  /** Return a String error or computation outcome of the route command passed
   back to the REPL.
   @param command A String representing the full command entered.
   @param parseKey A String key representing the function which should parse
   any successful output into the desired format.
   @return 1 String, either an ERROR, or the result of a successful computation
   which may have newlines.
   */
  public static String routeCommand(String command, String parseKey) {
    parseFunc = VALID_PARSERS.get(parseKey);
    List<String> splitCommand = new LinkedList<>(Arrays.asList(splitCommandComponents(command)));
    try {
      return checkRouteArgs(splitCommand);
    } catch (IllegalPathStateException e) {
      return ErrorMessages.INVALID_STREET_NAMES;
    } catch (NumberFormatException e) {
      return ErrorMessages.NON_REAL_LAT_AND_LON;
    } catch (IllegalArgumentException e) {
      return ErrorMessages.ROUTE_INVALID_NUMBER_ARGUMENTS;
    } catch (SQLException e) {
      return ErrorMessages.INVALID_SQL_QUERY;
    }
  }

  /** Checks whether the route command passed has valid arguments.
   @param splitCommand An Array of Strings representing each part of an entered
   command
   @return 1 String, either an ERROR, or the result of a successful computation
   which may have newlines.
   @throws SQLException if SQL fails to execute command
   */
  static String checkRouteArgs(List<String> splitCommand) throws SQLException {
    if (splitCommand.size() == 5) {
      if (!String.join("", splitCommand).contains("\"")) {
        double lat1 = Double.parseDouble(splitCommand.get(1));
        double lon1 = Double.parseDouble(splitCommand.get(2));
        double lat2 = Double.parseDouble(splitCommand.get(3));
        double lon2 = Double.parseDouble(splitCommand.get(4));
        return getPath(lat1, lon1, lat2, lon2);
      } else {
        List<String> streetsOutsideQuotes = new ArrayList<>();

        splitCommand.remove(0);
        for (String street : splitCommand) {
          if (street.startsWith("\"") && street.endsWith("\"")) {
            streetsOutsideQuotes.add(street.substring(1, street.length() - 1));
          } else {
            throw new IllegalPathStateException();
          }
        }
        return getPath(streetsOutsideQuotes.get(0), streetsOutsideQuotes.get(1),
            streetsOutsideQuotes.get(2), streetsOutsideQuotes.get(3));
      }
    } else {
      throw new IllegalArgumentException();
    }
  }

  /** Split the passed command at any spaces not included within a pair of quotes.
   @param command A String representing the full command entered.
   @return An ArrayList of Strings representing each part of the command.
   */
  static String[] splitCommandComponents(String command) {
    List<String> matchList = new ArrayList<>();
    Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
    Matcher regexMatcher = regex.matcher(command);
    while (regexMatcher.find()) {
      matchList.add(regexMatcher.group());
    }
    return matchList.toArray(new String[0]);
  }

  /** Find the node at the intersection of two streets with given names.
   * @param street is a String that is the name of a street
   * @param crossStreet is a String that is the name of a street
   * @return a GraticuleNode that is at their intersection
   * @throws SQLException if SQL fails to execute command
   * @throws NullPointerException if no database has been loaded.
   * @throws NoSuchFieldError if queried field does not exist
   */
  static GraticuleNode getTargetPathNode(String street, String crossStreet)
      throws SQLException, NullPointerException, NoSuchFieldError {
    if (DatabaseHandler.getConn() == null) {
      throw new NullPointerException();
    }

    if (street.equals(crossStreet)) {
      throw new IllegalAccessError();
    }

    List<GraticuleEdge> streetEdges = MapDBResultSetHandler.queryDBWays(
        "SELECT way.id AS wayID, way.name, way.type, way.start, way.end,\n"
            + "N1.latitude as lat1, N1.longitude as lon1,\n"
            + "N2.latitude as lat2, N2.longitude as lon2\n"
            + "FROM way\n"
            + "INNER JOIN node as N1\n"
            + "INNER JOIN node as N2\n"
            + "ON (way.start=N1.id) AND (way.end=N2.id)" + "\n"
            + "WHERE (way.name='" + street + "')\n"
            + "AND way.type!='unclassified' AND way.type!=''"
            + ";"
    );

    List<GraticuleEdge> crossStreetEdges = MapDBResultSetHandler.queryDBWays(
        "SELECT way.id AS wayID, way.name, way.type, way.start, way.end,\n"
            + "N1.latitude as lat1, N1.longitude as lon1,\n"
            + "N2.latitude as lat2, N2.longitude as lon2\n"
            + "FROM way\n"
            + "INNER JOIN node as N1\n"
            + "INNER JOIN node as N2\n"
            + "ON (way.start=N1.id) AND (way.end=N2.id)" + "\n"
            + "WHERE (way.name='" + crossStreet + "')\n"
            + "AND way.type!='unclassified' AND way.type!=''"
            + ";"
    );

    if (streetEdges.size() == 0 || crossStreetEdges.size() == 0) {
      throw new IllegalPathStateException();
    }

    // if multiple streets intersect, the first two where the start nodes equated were chosen
    for (GraticuleEdge w1 : streetEdges) {
      for (GraticuleEdge w2 : crossStreetEdges) {
        if (w1.getStartNode().getId().equals(w2.getStartNode().getId())) {
          return w1.getStartNode();
        } else if (w1.getEndNode().getId().equals(w2.getEndNode().getId())) {
          return w1.getEndNode();
        } else if (w1.getStartNode().getId().equals(w2.getEndNode().getId())) {
          return w1.getStartNode();
        } else if (w1.getEndNode().getId().equals(w2.getStartNode().getId())) {
          return w1.getEndNode();
        }
      }
    }
    throw new IllegalPathStateException();
  }

  /** Finds the nearest Node to given latitude and longitude.
   * @param lat is a Double that is the given latitude
   * @param lon is a Double that is the given longitude
   * @return a GraticuleNode that is the nearest to the provided location
   */
  public static GraticuleNode getTargetPathNode(double lat, double lon) {
    return NearestCommandHandler.getNearestNode(lat, lon);
  }

  /** Returns a String that represents the path from the intersection of
   * street1 and crossStreet1 to the intersection of street2 and crossStreet2.
   * @param street1 is a String that is the name of a street in the database
   * @param crossStreet1 is a String that is the name of a street in the database
   * @param street2 is a String that is the name of a street in the database
   * @param crossStreet2 is a String that is the name of a street in the database
   * @return a String that represents the path, connecting nodes and ways
   * @throws SQLException if SQL fails to execute command
   */
  static String getPath(String street1, String crossStreet1,
                        String street2, String crossStreet2) throws SQLException {
    try {
      GraticuleNode start = getTargetPathNode(street1, crossStreet1);
      GraticuleNode end = getTargetPathNode(street2, crossStreet2);

      DijkstraAStar<String, String, GraticuleNode> aStar
          = new DijkstraAStar<>(
          NodeDistanceCalculators::getHaversineDistance,
          MapCommandHandler.getdBProxiedReader()::get,
          HeuristicFuncs::aStarDist);
      return parseFunc.apply(aStar.runDijkstraAStar(start, end), start, end);

    } catch (IllegalPathStateException e) {
      return ErrorMessages.PATHS_NON_INTERSECTION;
    } catch (NullPointerException e) {
      return ErrorMessages.NO_DATABASE_LOADED;
    } catch (IllegalAccessError e) {
      return ErrorMessages.STREET_SELF_INTERSECTION;
    }
  }

  /** Finds tha path from (lat1, lon1) to (lat2, lon2) using A* search and Haversine distance.
   * @param lat1 is a double that represents a latitude
   * @param lon1 is a double that represents a longitude
   * @param lat2 is a double that represents a latitude
   * @param lon2 is a double that represents a longitude
   * @return a String that represents the path
   */
  static String getPath(double lat1, double lon1, double lat2, double lon2) {
    try {
      GraticuleNode start = getTargetPathNode(lat1, lon1);
      GraticuleNode end = getTargetPathNode(lat2, lon2);

      DijkstraAStar<String, String, GraticuleNode> aStar
          = new DijkstraAStar<>(
              NodeDistanceCalculators::getHaversineDistance,
              MapCommandHandler.getdBProxiedReader()::get,
              HeuristicFuncs::aStarDist);
      return parseFunc.apply(aStar.runDijkstraAStar(start, end), start, end);

    } catch (NullPointerException e) {
      return ErrorMessages.NO_DATABASE_LOADED;
    }
  }

  /** Parse ways, with startNode and endNode, to REPL.
   * @param ways is a list of PathEdges with String ids that connect GraticuleNodes with String ids,
   *             with startNode and endNode as the start and end
   * @param startNode is a PathNode with String id that represents the start
   * @param endNode is a PathNode with String id that represents the end
   * @return a String that represents the ways from startNode to endNode
   */
  public static String parseToRepl(List<GraphEdge<String, String, GraticuleNode>> ways,
                                   GraphNode<String> startNode, GraphNode<String> endNode) {
    List<String> wayIDs = new ArrayList<>();

    if (ways.size() == 0) {
      wayIDs.add(startNode.getId() + " -/- " + endNode.getId());
    } else {
      for (GraphEdge<String, String, GraticuleNode> w : ways) {
        wayIDs.add(w.getStartNode().getId() + " -> " + w.getEndNode().getId() + " : " + w.getId());
      }
    }

    return String.join("\n", wayIDs);
  }

  /**
   * Returns the path from the intersection of street1 and crossStreet1
   * to the intersection of street2 and crossStreet2 or an informative error message
   * if this could not be found, in a format compatible with the front-end.
   *
   * @param street1 - a String that is the name of a street in the database
   * @param crossStreet1 - a String that is the name of a street in the database
   * @param street2 - a String that is the name of a street in the database
   * @param crossStreet2 - a String that is the name of a street in the database
   * @return a map in the format compatible with the front-end representing
   * either the path between the given intersections or an informative error
   * message if this could not be done
   * @throws SQLException if database is not set/ cannot be queried
   */
  public static Map<String, Object> getPathStreetGui(String street1, String crossStreet1,
                        String street2, String crossStreet2) throws SQLException {
    try {
      GraticuleNode start = getTargetPathNode(street1, crossStreet1);
      GraticuleNode end = getTargetPathNode(street2, crossStreet2);

      DijkstraAStar<String, String, GraticuleNode> lazyDijk
          = new DijkstraAStar<>(
          NodeDistanceCalculators::getHaversineDistance,
          MapCommandHandler.getdBProxiedReader()::get,
          HeuristicFuncs::aStarDist);
      return parseToGui(lazyDijk.runDijkstraAStar(start, end), start, end);
    } catch (IllegalPathStateException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "nearest", "",
        "error", "ERROR: Paths do not intersect / street not found");
    } catch (NullPointerException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "nearest", "",
        "error", "ERROR: No DB loaded");
    } catch (IllegalAccessError e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "nearest", "",
        "error", "ERROR: Street self-intersection");
    }
  }

  /**
   * Returns the path from (lat1, lon1) to (lat2, lon2) or an informative error message
   * if this could not be found, in a format compatible with the front-end.
   *
   * @param lat1 - a double that represents a latitude
   * @param lon1 - a double that represents a longitude
   * @param lat2 - a double that represents a latitude
   * @param lon2 - a double that represents a longitude
   *
   * @return a map in the format compatible with the front-end representing
   * either the path between the given positions or an informative error
   * message if this could not be done
   * @throws SQLException if database is not set/ cannot be queried
   */
  public static Map<String, Object> getPathLatLonGui(double lat1, double lon1,
                                               double lat2, double lon2) throws SQLException {
    try {
      GraticuleNode start = getTargetPathNode(lat1, lon1);
      GraticuleNode end = getTargetPathNode(lat2, lon2);

      DijkstraAStar<String, String, GraticuleNode> lazyDijk
          = new DijkstraAStar<>(
          NodeDistanceCalculators::getHaversineDistance,
          MapCommandHandler.getdBProxiedReader()::get,
          HeuristicFuncs::aStarDist);
      return parseToGui(lazyDijk.runDijkstraAStar(start, end), start, end);
    } catch (NullPointerException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "nearest", "",
        "error", "ERROR: No DB loaded");
    }
  }

  /**
   * Returns the path from (lat1, lon1) to (lat2, lon2) or an informative error message
   * if this could not be found, in a format compatible with the front-end.
   *
   * @param lat1 - a double that represents a latitude
   * @param lon1 - a double that represents a longitude
   * @param lat2 - a double that represents a latitude
   * @param lon2 - a double that represents a longitude
   *
   * @return a map in the format compatible with the front-end representing
   * either the path between the given positions or an informative error
   * message if this could not be done
   * @throws SQLException if database is not set/ cannot be queried
   */

  /**
   * Returns the path between the given start and end nodes through the given ways
   * in a format compatible with the front-end.
   *
   * @param ways - a list of GraphEdges representing the Ways of the route
   * @param startNode - the starting GraphNode of the route
   * @param endNode - the ending GraphNode of the route
   * @return a map in a format compatible with the front-end representing
   * the path between the given start and end nodes.
   */
  public static Map<String, Object> parseToGui(List<GraphEdge<String, String, GraticuleNode>> ways,
                                   GraphNode<String> startNode, GraphNode<String> endNode) {
    List<String[]> edgesAndNodes = new ArrayList<>();

    if (ways.size() == 0) {
      edgesAndNodes.add(new String[]{
          startNode.getId(), startNode.getCoordinates().get(0) + "",
              startNode.getCoordinates().get(1) + "",
          endNode.getId(), endNode.getCoordinates().get(0) + "",
              endNode.getCoordinates().get(1) + ""
      });
    } else {
      for (GraphEdge<String, String, GraticuleNode> w : ways) {
        String[] currentWayData = {
          w.getId(),
          w.getStartNode().getId(), w.getStartNode().getLatitude() + "",
                w.getStartNode().getLongitude() + "",
          w.getEndNode().getId(), w.getEndNode().getLatitude() + "",
                w.getEndNode().getLongitude() + "",
        };
        edgesAndNodes.add(currentWayData);
      }
    }

    return ImmutableMap.of(
      "map", "",
      "route", edgesAndNodes,
      "ways", "",
      "nearest", "",
      "error", "");
  }
}
