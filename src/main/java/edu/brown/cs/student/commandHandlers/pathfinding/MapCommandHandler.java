package edu.brown.cs.student.commandHandlers.pathfinding;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.student.coordinates.KdTree;
import edu.brown.cs.student.pathfinding.GraticuleNode;
import edu.brown.cs.student.pathfinding.ProxiedEdgeFetcher;
import edu.brown.cs.student.database.DatabaseHandler;
import edu.brown.cs.student.main.ErrorMessages;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Class that checks and executes map command.
 */
public final class MapCommandHandler {
  private MapCommandHandler() {
  }

  private static String curDb = "";

  private static Function<String, String> parseFunc;
  private static KdTree<String, GraticuleNode> kdTree = null;
  private static ProxiedEdgeFetcher<String, String, GraticuleNode> dBProxiedReader;

  /**
   * Getter function for the name of the current database.
   *
   * @return the name of the current database
   */
  public static String getCurDb() {
    return curDb;
  }

  private static final Map<String, Function<String, String>> VALID_PARSERS
      = new HashMap<>() {{
            put("repl", MapCommandHandler::parseToRepl);
        }};

  /** returns the proxied reader for the database.
   * @return a ProxiedEdgeFetcher
   */
  public static ProxiedEdgeFetcher<String, String, GraticuleNode> getdBProxiedReader() {
    return dBProxiedReader;
  }

  /** returns built KDTree.
   * @return a KDTree made from GraticuleNodes with type String ids.
   */
  public static KdTree<String, GraticuleNode> getKdTree() {
    return kdTree;
  }

  /** resets the database handler connection and KDTree.
   */
  public static void reset() {
    DatabaseHandler.setConn(null);
    MapCommandHandler.kdTree = null;
  }

  /** Return a String error or computation outcome of the map command passed
   back to the REPL.
   @param command A String representing the full command entered.
   @param parseKey A String key representing the function which should parse
   any successful output into the desired format.
   @return 1 String, either an ERROR, or the result of a successful computation
   which may have newlines.
   */
  public static String mapCommand(String command, String parseKey) {
    parseFunc = VALID_PARSERS.get(parseKey);
    String[] splitCommand = command.split(" ");
    try {
      return checkMapArgs(splitCommand);
    } catch (IllegalArgumentException e) {
      return ErrorMessages.MAP_INVALID_NUMBER_ARGUMENTS;
    }
  }

  /** Checks whether the map command passed has valid arguments.
   @param splitCommand An Array of Strings representing each part of an entered
   command
   @return 1 String, either an ERROR, or the result of a successful computation
   which may have newlines.
   */
  static String checkMapArgs(String[] splitCommand) {
    if (splitCommand.length == 2) {
      String filename = splitCommand[1];
      return loadDB(filename);
    } else {
      throw new IllegalArgumentException();
    }
  }

  /** Handle all errors that may be thrown from calling DatabaseHandler.loadDB when
   extracting all rows from the database at the passed path.
   @param filename A String containing the path to the database to be read.
   @return 1 String, either an ERROR, or the result of a successful computation
   which may have newlines.
   */
  static String loadDB(String filename) {
    try {
      DatabaseHandler.loadDB(filename);
      dBProxiedReader
        = new ProxiedEdgeFetcher<>(NeighborWaySearchers::getWaysFromTargetNode);
      loadTree();
      if (kdTree.getRoot() == null) {
        MapCommandHandler.reset();
        throw new IllegalArgumentException();
      }
      curDb = "Currently Loaded: " + filename;
      return parseFunc.apply(filename);
    } catch (FileNotFoundException e) {
      return ErrorMessages.invalidDatabaseFilepath(filename);
    } catch (IllegalAccessError e) {
      return ErrorMessages.DATABASE_INVALID_EXTENSION;
    } catch (SQLException e) {
      return ErrorMessages.DATABASE_MISSING_COLUMNS_OR_EMPTY;
    } catch (ClassNotFoundException e) {
      return ErrorMessages.CLASS_NOT_FOUND;
    } catch (NumberFormatException e) {
      return ErrorMessages.NON_REAL_LAT_AND_LON;
    } catch (IllegalArgumentException e) {
      return ErrorMessages.MALFORMED_OR_MISSING_DATA;
    } catch (IllegalAccessException e) {
      return ErrorMessages.EMPTY_WAY_OR_NODE_ID;
    }
  }

  /** creates the KDTree by querying database for traversable nodes.
   * @throws SQLException if SQL fails to execute command
   */
  static void loadTree() throws SQLException, IllegalAccessException {
    kdTree = MapDBResultSetHandler.queryDBNodes(
        "SELECT N1.id, N1.latitude as lat1, N1.longitude as lon1, "
            + "N2.id, N2.latitude as lat2, N2.longitude as lon2, way.id "
            + "FROM way\n"
            + "INNER JOIN node as N1\n"
            + "INNER JOIN node as N2\n"
            + "ON (way.start=N1.id) AND (way.end=N2.id)"
            + "AND NOT(way.type=\"\" OR way.type=\"unclassified\")\n"
            + ";"
    );
  }

  /** returns a display String as load message.
   @param filename A String representing the path to the data file.
   @return A String concatenating the passed parameters into a meaningful load message.
   */
  static String parseToRepl(String filename) {
    return "map set to " + filename;
  }

  /**
   * Command Handler to handle requests from the front-end to load map
   * data.
   *
   * @param filename - path of the file whose data is to be loaded
   * @return an immutable map in the format required by the front-end
   * indicating if a map was successfully loaded, or an informative
   * error message if it was not.
   */
  public static ImmutableMap<String, Object> loadDBGui(String filename) {
    try {
      DatabaseHandler.loadDB(filename);
      dBProxiedReader
        = new ProxiedEdgeFetcher<>(NeighborWaySearchers::getWaysFromTargetNode);
      loadTree();
      if (kdTree.getRoot() == null) {
        MapCommandHandler.reset();
        throw new IllegalArgumentException();
      }
      curDb = "Currently Loaded: " + filename;
      return parseToGui(filename);
    } catch (FileNotFoundException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "error", "ERROR: DB not found");
    } catch (IllegalAccessError e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "error", "ERROR: DB must be of the provided extension types");
    } catch (SQLException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "error", "ERROR: DB missing columns/is empty");
    } catch (ClassNotFoundException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "error", "ERROR: Class missing");
    } catch (NumberFormatException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "error", "ERROR: Latitude and longitude must be real");
    } catch (IllegalArgumentException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "error", "ERROR: Malformed/missing data in DB");
    } catch (IllegalAccessException e) {
      return ImmutableMap.of(
        "map", "",
        "route", "",
        "ways", "",
        "error", "ERROR: Way and node ids must not be empty");
    }
  }

  /**
   * Returns an immutable map in the format required by the front-end
   * indicating that the given filename has been successfully loaded.
   *
   * @param filename - path of the file whose data has been loaded
   * @return an immutable map in the format required by the front-end
   * indicating the name of the map that was successfully loaded
   */
  public static ImmutableMap<String, Object> parseToGui(String filename) {
    return ImmutableMap.of(
      "map", "Currently loaded: " + filename,
      "route", "",
      "ways", "",
      "error", "");
  }
}
