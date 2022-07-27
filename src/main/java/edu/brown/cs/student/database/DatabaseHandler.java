package edu.brown.cs.student.database;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Class that establishes connections to valid DBs.
 */
public final class DatabaseHandler {
  private DatabaseHandler() {
  }

  private static List<String> validExtensionFormats = new ArrayList<>(List.of("sqlite3"));
  private static Connection conn = null;

  /** Sets the current valid extension formats to inputted list.
   * @param validExtensionFormats is a list of extension formats (ex. "sqlite3")
   */
  public static void setValidExtensionFormats(List<String> validExtensionFormats) {
    DatabaseHandler.validExtensionFormats = new ArrayList<>(validExtensionFormats);
  }

  /** Gets current Connection.
   * @return the Connection in DatabaseHandler
   */
  public static Connection getConn() {
    return conn;
  }

  /** Sets current Connection to an inputted one.
   * @param conn is the given Connection.
   */
  public static void setConn(Connection conn) {
    DatabaseHandler.conn = conn;
  }

  /** Instantiates the database, creating tables if necessary; automatically loads files.
   @param filename file name of SQLite3 database to open.
   @throws SQLException if an error occurs in any SQL query.
   @throws ClassNotFoundException if the file path is broken.
   @throws FileNotFoundException if the file with inputted file name does not exist.
   */
  public static void loadDB(String filename) throws SQLException, ClassNotFoundException,
      FileNotFoundException {

    /*
     * Initialize the database connection, turn foreign keys on,
     *  and then create the word and corpus tables if they do not exist.
     */
    File f = new File(filename);
    if (!f.isFile()) {
      throw new FileNotFoundException();
    } else if (!validExtensionFormats.contains(Files.getFileExtension(filename))) {
      throw new IllegalAccessError();
    }

    // this line loads the driver manager class, and must be
    // present for everything else to work properly
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    conn = DriverManager.getConnection(urlToDB);
    // these two lines tell the database to enforce foreign keys during operations,
    // and should be present
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
  }

  /** Queries Database according to an inputted String of SQL commands.
   * @param query is a String of the SQL Commands
   * @return ResultSet containing the queried objects
   * @throws SQLException if something goes wrong with a SQL query.
   * @throws NullPointerException if database has not been loaded yet.
   */
  public static ResultSet queryLoadedDB(String query) throws SQLException, NullPointerException {
    PreparedStatement prep;
    prep = conn.prepareStatement(query);
    return prep.executeQuery();
  }
}
