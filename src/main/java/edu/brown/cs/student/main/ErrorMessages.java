package edu.brown.cs.student.main;

/**
 * Final class used for to store and access error messages.
 */
public final class ErrorMessages {

  /**
   * Constructs an ErrorMessages object.
   * Private to prevent objects being instantiated outside the class.
   */
  private ErrorMessages() {

  }

  public static final String MAP_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for map";

  /**
   * Static method used to return an error string for when the database was not
   * found at the given file path.
   *
   * @param databaseFilePath the database's file path
   * @return error message telling user that the database they were looking for was
   * not found
   */
  public static String invalidDatabaseFilepath(String databaseFilePath) {
    return "ERROR: Database at " + databaseFilePath + " not found";
  }

  public static final String DATABASE_INVALID_EXTENSION =
          "ERROR: Database must be of the provided extension types";

  public static final String DATABASE_MISSING_COLUMNS_OR_EMPTY =
          "ERROR: Database missing columns/is empty";

  public static final String CLASS_NOT_FOUND =
          "ERROR: Class missing";

  public static final String NON_REAL_LAT_AND_LON =
          "ERROR: Latitude and longitude must be real numbers";

  public static final String MALFORMED_OR_MISSING_DATA =
          "ERROR: Malformed/missing data in DB";

  public static final String EMPTY_WAY_OR_NODE_ID =
          "ERROR: Way and node ids must not be empty";

  public static final String NON_NUMBER_LAT_AND_LON =
          "ERROR: Latitudes and longitudes must be numbers";

  public static final String NEAREST_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for nearest";

  public static final String NO_DATABASE_LOADED =
          "ERROR: No Database loaded";

  public static final String INVALID_STREET_NAMES =
          "ERROR: street names must be within \"\"";

  public static final String ROUTE_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for route / streets within incomplete quotes";

  public static final String INVALID_SQL_QUERY =
          "ERROR: SQL Invalid Query";

  public static final String PATHS_NON_INTERSECTION =
          "ERROR: Paths do not intersect / street not found";

  public static final String STREET_SELF_INTERSECTION =
          "ERROR: Street self-intersection";

  public static final String WAYS_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for ways";

  public static final String INVALID_BOUNDING_BOX =
          "ERROR: Coordinates not northwest & southeast";

  public static final String STAR_NAME_NOT_IN_QUOTES =
          "ERROR: Star name must be within \"\"";

  public static final String INVALID_K =
          "ERROR: k must be a non-negative integer";

  public static final String NEIGHBORS_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for neighbors";

  public static final String INVALID_R =
          "ERROR: r must be a non-negative real number";

  public static final String RADIUS_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for radius";

  public static final String STAR_NAME_NOT_FOUND =
          "ERROR: Star name not found in loaded csv";

  public static final String EMPTY_STAR_NAME =
          "ERROR: Star name cannot be empty";

  public static final String NO_CSV_LOADED =
          "ERROR: No csv loaded";

  public static final String NAIVE_NEIGHBORS_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for naive_neighbors";

  public static final String NAIVE_RADIUS_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for naive_radius";

  public static final String STARS_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for stars";

  public static final String CSV_MISSING_COLUMNS =
          "ERROR: Missing columns in csv";

  public static final String INVALID_STAR_ID =
          "ERROR: Star ID must be a non-negative integer";

  public static final String INVALID_HEADER =
          "ERROR: File does not contain header/invalid header found";

  /**
   * Static method used to return an error string for when a csv file was not
   * found at the given file path.
   *
   * @param filePath the csv file's file path
   * @return error message telling user that the database they were looking for was
   * not found
   */
  public static String invalidFilepath(String filePath) {
    return "ERROR: File at " + filePath + " not found";
  }

  public static final String NO_CSV_SELECTED =
          "ERROR: No CSV selected";

  public static final String STARS_INSUFFICIENT_DIMENSIONS =
          "ERROR: Stars cannot have insufficient dimensions";

  public static final String REPL_INVALID_COMMAND =
          "ERROR: Invalid command entered";

  public static final String INVALID_STREAM_INPUT =
          "ERROR: Unable to get system input";

  public static final String DELETE_INVALID_NUMBER_ARGUMENTS =
          "ERROR: Invalid no.of arguments passed for delete";

  public static final String DELETE_NON_INT_ARGUMENT =
          "ERROR: User IDs must be integers";

  public static final String DELETE_NO_DB_LOADED =
          "ERROR: No Maps Database has been Loaded";

  public static final String COULD_NOT_QUERY_SERVER =
          "ERROR: Could not add data from server into database";

  public static final String CHECKIN_TABLE_NOT_CREATED =
          "ERROR: Checkins table could not be created";
}
