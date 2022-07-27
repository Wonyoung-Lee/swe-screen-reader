package edu.brown.cs.student.commandHandlers.pathfinding;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.student.pathfinding.GraphNode;
import edu.brown.cs.student.pathfinding.GraticuleNode;
import edu.brown.cs.student.main.ErrorMessages;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Class that checks and executes nearest commands
 * by extracting necessary data.
 */
public final class NearestCommandHandler {
  private static Function<GraticuleNode, String> parseFunc;

  private NearestCommandHandler() {
  }

  private static final Map<String, Function<GraticuleNode, String>>
      VALID_PARSERS = new HashMap<>() {
        { put("repl", NearestCommandHandler::parseToRepl); }
      };

  /** Return a String error or computation outcome of the nearest
   * command passed back to the REPL.
   @param command A String representing the full command entered.
   @param parseKey A String key representing the function which should parse
   any successful output into the desired format.
   @return 1 String, either an ERROR, or the result of a successful computation
   which may have newlines.
   */
  public static String nearestCommand(String command, String parseKey) {
    parseFunc = VALID_PARSERS.get(parseKey);
    String[] splitCommand = command.split(" ");
    try {
      return checkNearestArgs(splitCommand);
    } catch (NumberFormatException e) {
      return ErrorMessages.NON_NUMBER_LAT_AND_LON;
    } catch (IllegalArgumentException e) {
      return ErrorMessages.NEAREST_INVALID_NUMBER_ARGUMENTS;
    } catch (NullPointerException e) {
      return ErrorMessages.NO_DATABASE_LOADED;
    } catch (IndexOutOfBoundsException e) {
      return "";
    }
  }

  /** Checks whether the nearest command passed has valid arguments.
   @param splitCommand An Array of Strings representing each part of an entered
   command
   @return 1 String, either an ERROR, or the result of a successful computation
   which may have newlines.
   */
  static String checkNearestArgs(String[] splitCommand) {
    if (splitCommand.length == 3) {
      double lat = Double.parseDouble(splitCommand[1]);
      double lon = Double.parseDouble(splitCommand[2]);
      return handleNearest(lat, lon);
    } else {
      throw new IllegalArgumentException();
    }
  }

  /** Finds the nearest Node to the given latitude and longitude.
   * @param lat is a given double that represents latitude
   * @param lon is a given double that represents longitude
   * @return a GraticuleNode that is the found nearest node
   * @throws IndexOutOfBoundsException if the object at index does not exist
   */
  public static GraticuleNode getNearestNode(double lat, double lon) throws IndexOutOfBoundsException {
    GraticuleNode target = new GraticuleNode("", lat, lon);
    List<GraticuleNode> nearestNode = MapCommandHandler.getKdTree()
        .getNearestNeighborsResult(1, target, false);
    // if there are multiple nearest nodes that are equidistant, the return is selected based on the
    // kdTree build
    return nearestNode.get(0);
  }

  /** Wrapper for getNearestNode; parses the nearest Node as a String to REPL.
   * @param lat is the given latitude
   * @param lon is the given longitude
   * @return a String that represents the nearest Node found
   */
  static String handleNearest(double lat, double lon) {
    try {
      GraticuleNode nearestNode = getNearestNode(lat, lon);
      return parseFunc.apply(nearestNode);
    } catch (IndexOutOfBoundsException e) {
      return "";
    }
  }

  /** Parses the nearest GraticuleNode to REPL.
   * @param node is the found nearest node
   * @return a String that represents node
   */
  static String parseToRepl(GraticuleNode node) {
    return node.getId();
  }

  /**
   * Returns a map in the format required by the front-end
   * with the information of the nearest Node to the given
   * lat and lon.
   *
   * @param lat - latitude of point from which to find nearest
   * @param lon - longitude of point from which to find nearest
   * @return a map in the format required by the front-end with the information
   * of the nearest Node to the given lat and lon.
   * @throws SQLException if database is not set/ cannot be queried
   */
  public static Map<String, Object> handleNearestGui(double lat, double lon) throws SQLException {
    GraticuleNode nearestNode = getNearestNode(lat, lon);
    return parseToGui(nearestNode);
  }

  /**
   * Queries the map database and gets the information of the streets
   * intersecting at the target node.
   *
   * @param targetNode - GraphNode from which to search for streets.
   * @return a String array representing information required by the
   * front-end of the streets intersecting at the given targetNode.
   * @throws SQLException if database is not set/ cannot be queried
   */
  public static String[] getIntersectingStreets(GraphNode<String> targetNode) throws SQLException {
    List<String> streetEdges = MapDBResultSetHandler.queryDBStreetIntersections(
        "SELECT name from way WHERE "
        + "(start='" + targetNode.getId() + "' or end='" + targetNode.getId() + "')"

    );

    if (streetEdges.size() == 0) {
      return null;
    } else {
      String[] streets = new String[4];
      streets[0] = streetEdges.get(0);

      for (String street : streetEdges) {
        if (!street.equals(streets[0])) {
          streets[1] = street;
          streets[2] = targetNode.getCoordinates().get(0) + "";
          streets[3] = targetNode.getCoordinates().get(1) + "";
          return streets;
        }
      }
      return null;
    }
  }

  /**
   * Returns an immutable map in the format required by the front-end
   * with the data of the traversable intersection at the given
   * GraticuleNode, or an informative error message if no traversable
   * intersection is found.
   *
   * @param node - node from which to find a traversable intersection
   * @return the data representing the traversable intersection or an error
   * message in a format compatible with the front-end
   * @throws SQLException - if database is not set/ cannot be queried
   */
  public static Map<String, Object> parseToGui(GraticuleNode node) throws SQLException {
    String[] result = getIntersectingStreets(node);
    if (result == null) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "nearest", "",
        "error", "No traversable intersection found");
    } else {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "nearest", result,
        "error", "");
    }
  }
}

