package edu.brown.cs.student.maps;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.student.database.DatabaseHandler;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFetchHandler {
  public DatabaseFetchHandler() {
  }

  public List<Way> fetchWays(double maxLat, double minLat, double maxLon, double minLon)
      throws SQLException, FileNotFoundException, ClassNotFoundException {
    // Load DB
    if (maxLat <= minLat || maxLon <= minLon) {
      throw new IllegalArgumentException("Coordinates are input incorrectly");
    }
    DatabaseHandler.loadDB("./data/maps/maps.sqlite3");

    // Create Query String
    String fancyStmt = "SELECT way.*, sNode.latitude as startLat, sNode.longitude as startLon, "
        + "eNode.latitude as endLat, eNode.longitude as endLon FROM way "
        + "INNER JOIN (SELECT * FROM node WHERE latitude BETWEEN " + minLat + " AND " + maxLat + " AND "
        + "longitude BETWEEN " + minLon + " and " + maxLon + ") as sNode ON way.start=sNode.id INNER JOIN "
        + "(SELECT * FROM node WHERE latitude BETWEEN " + minLat + " AND " + maxLat + " AND longitude "
        + "BETWEEN " + minLon + " and " + maxLon + ") as eNode ON way.end=eNode.id;";
    // Query Results
    ResultSet results = DatabaseHandler.queryLoadedDB(fancyStmt);
    ArrayList<Way> ways = new ArrayList<>();
    // Add each Way to ways ArrayList
    while (results.next()) {
      Way currWay = new Way(results.getString("id"),
          results.getDouble("startLat"),
          results.getDouble("endLat"),
          results.getDouble("startLon"),
          results.getDouble("endLon"),
          results.getString("name"),
          results.getString("type"));
      ways.add(currWay);
    }
    System.out.println(maxLon > minLon && maxLat > minLat);
    System.out.println("ways size " + ways.size());
    return ways;
  }
}



//    String stmt = "SELECT * FROM way INNER JOIN "
//        + "(SELECT id FROM node WHERE latitude BETWEEN " + latNW + " AND " + latSE + " AND "
//        + " longitude BETWEEN " + lonSE + " AND " + lonNW + ") as boxNodes ON (way.start=boxNodes.id "
//        + "OR way.end = boxNodes.id) ORDER BY way.id ASC;";