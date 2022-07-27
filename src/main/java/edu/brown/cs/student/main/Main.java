package edu.brown.cs.student.main;


import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.student.commandHandlers.pathfinding.MapCommandHandler;
import edu.brown.cs.student.commandHandlers.pathfinding.NearestCommandHandler;
import edu.brown.cs.student.commandHandlers.pathfinding.RouteCommandsHandler;
import edu.brown.cs.student.maps.DatabaseFetchHandler;
import edu.brown.cs.student.maps.Way;
import edu.brown.cs.student.repl.Repl;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import freemarker.template.Configuration;


/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    // create a map of valid commands and command handler methods
    Map<String, BiFunction<String, String, String>> validCommands = new HashMap<>() {
      {
        put("map", MapCommandHandler::mapCommand);
        put("nearest", NearestCommandHandler::nearestCommand);
        put("route", RouteCommandsHandler::routeCommand);
      }
    };

    // instantiate and activate the REPL
    Repl repl = new Repl(validCommands);
    repl.activate(new InputStreamReader(System.in, StandardCharsets.UTF_8));
  }

//  private static String loadedFileState = "*No file loaded*";

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {

    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");

    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // TODO: Setup Spark Routes
    // Example Spark Route where /route is the subpage and MapsGuiHandler is a class
    // containing the RouteHandler method
//     Spark.post("/route", new MapsGuiHandler.RouteHandler());
    Spark.post("/ways", ((request, response) -> {
      response.type("application/json");
      Gson gson = new Gson();
      double[] coords = gson.fromJson(request.body(), double[].class);
      DatabaseFetchHandler dbFetch = new DatabaseFetchHandler();
      List<Way> ways = dbFetch.fetchWays(coords[0], coords[1], coords[2], coords[3]);
      String waysJson = gson.toJson(ways);
      return waysJson;
    }));

//    Gson gson = new Gson();
//    double[] coords = {54.0, 42.0, 42.0, 55.0};
//    fetch('https://jsonplaceholder.typicode.com/posts', {
//        method: 'post',
//        body: gson.toJson(coords)
//	}),
//        headers: {
//          'Content-type': 'application/json; charset=UTF-8',
//        },
//    })
//    .then((response) => response.json())
//    .then((data) => console.log(data));

  }


  /**
   * Display an error page when an exception occurs in the server.
   *
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
