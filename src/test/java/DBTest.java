import com.google.gson.Gson;
import edu.brown.cs.student.database.DatabaseHandler;
import edu.brown.cs.student.maps.DatabaseFetchHandler;
import edu.brown.cs.student.maps.Way;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DBTest {

  @Test
  public void understandingFunctionality() throws SQLException {
    String path = "./data/maps/maps.sqlite3";
    try {
      DatabaseHandler.loadDB(path);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    double maxLat = 41.828147;
    double minLat = 41.823142;
    double maxLon = -71.392231;
    double minLon = -72.407971;
    String fancyStmt = "SELECT way.*, sNode.latitude as startLat, sNode.longitude as startLon, "
        + "eNode.latitude as endLat, eNode.longitude as endLon FROM way "
        + "INNER JOIN (SELECT * FROM node WHERE latitude BETWEEN " + minLat + " AND " + maxLat + " AND "
        + "longitude BETWEEN " + minLon + " and " + maxLon + ") as sNode ON way.start=sNode.id INNER JOIN "
        + "(SELECT * FROM node WHERE latitude BETWEEN " + minLat + " AND " + maxLat + " AND longitude "
        + "BETWEEN " + minLon + " and " + maxLon + ") as eNode ON way.end=eNode.id WHERE way.id LIKE '%1';";
    ResultSet results = DatabaseHandler.queryLoadedDB(fancyStmt);
    ArrayList<Way> ways = new ArrayList<>();
    while (results.next()) {
      Way currWay = new Way(results.getString("id"),
          results.getDouble("startLat"),
          results.getDouble("startLon"),
          results.getDouble("endLat"),
          results.getDouble("endLon"),
          results.getString("name"),
          results.getString("type"));
//      System.out.println(currWay);
      ways.add(currWay);
    }
    Gson gson = new Gson();
    String waysJson = gson.toJson(ways);
    System.out.println(waysJson);
    double[] coords = {54.0, 42.0};
    Gson GSON = new Gson();
    String test = GSON.toJson(coords);
//    System.out.println("test" + test);
  }

  @Test
  public void testAPI() throws SQLException, FileNotFoundException, ClassNotFoundException {
    Gson gson = new Gson();
    double maxLat = 41.828147;
    double minLat = 41.823142;
    double maxLon = -71.392231;
    double minLon = -72.407971;
    double[] coords = {maxLat, minLat, maxLon, minLon};
//    List<Way> ways = dbFetch.fetchWays(coords[0], coords[1], coords[2], coords[3]);
    DatabaseFetchHandler dbFetch = new DatabaseFetchHandler();
    List<Way> ways = dbFetch.fetchWays(maxLat, minLat, maxLon, minLon);
//    System.out.println(ways.size());
    String waysJson = gson.toJson(ways);
//    System.out.println(waysJson);
  }
}

// SELECT way.*, sNode.latitude as startLat, sNode.longitude as startLon, eNode.latitude as endLat, eNode.longitude as endLon FROM way INNER JOIN node sNode ON way.start=sNode.id INNER JOIN node eNode ON way.end=eNode.id LIMIT 10;
// SELECT way.id FROM way INNER JOIN (SELECT id FROM node WHERE latitude BETWEEN 41 AND 42 AND  longitude BETWEEN -72 AND -71) as boxNodes ON (way.start=boxNodes.id OR way.end = boxNodes.id) ORDER BY way.id ASC LIMIT 30;