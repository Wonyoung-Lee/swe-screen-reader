package edu.brown.cs.student.pathfinding;

import edu.brown.cs.student.commandHandlers.pathfinding.MapCommandHandler;
import edu.brown.cs.student.commandHandlers.pathfinding.NodeDistanceCalculators;
import edu.brown.cs.student.commandHandlers.pathfinding.RouteCommandsHandler;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertTrue;

/** Class to perform Property Based Testing on the A* and Dijkstra implementations
 of the Maps algorithms.
 */
public class PathfinderPropertyBasedTesting {
  /** Check whether the Dijkstra and A* implementations give the same results
   when fed randomly generated commands as input.
   @param iterations An integer that represents the number of randomly generated
   inputs to test naive and kdTree implementations against.
   @param latLongDeviation is the +/- threshold within which we can generate the latitude
   and longitude of the second node from the first node's latitude and longitude.
   @param possibleDB is the file name of the possible database to be queried.
   @return true or false if all comparisons succeeded.
   */
  public boolean testModels(int iterations, double latLongDeviation, String possibleDB) {

    int i = 0;
    String mapLoadCommand = "pathfinding " + possibleDB;
    MapCommandHandler.mapCommand(mapLoadCommand, "repl");

    while (i < iterations) {
      // choice of 0 represents lat lon
      // choice of 1 represents street cross-street
      int argChoice = ThreadLocalRandom.current().nextInt(0, 1);

      final double minLatitude = -90;
      final double maxLatitude = 90;
      final double minLongitude = -180;
      final double maxLongitude = 180;

      GraticuleNode start = null;
      GraticuleNode end = null;

      if (argChoice == 0) {
        // lat lon command
        double lat1 = ThreadLocalRandom.current().nextDouble(minLatitude, maxLatitude + 1);
        double lon1 = ThreadLocalRandom.current().nextDouble(minLongitude, maxLongitude + 1);
        double lat2 = lat1 + ThreadLocalRandom.current()
            .nextDouble(-1 * latLongDeviation, latLongDeviation + 1);
        double lon2 = lon1 + ThreadLocalRandom.current()
            .nextDouble(-1 * latLongDeviation, latLongDeviation + 1);

        start = RouteCommandsHandler.getTargetPathNode(lat1, lon1);
        end = RouteCommandsHandler.getTargetPathNode(lat2, lon2);
      }
      System.out.println("[pathfinding/a*]: " + start + " " + end);

      DijkstraAStar<String, String, GraticuleNode> dijkstra
          = new DijkstraAStar<>(
          NodeDistanceCalculators::getHaversineDistance,
          MapCommandHandler.getdBProxiedReader()::get,
          HeuristicFuncs::dijkstraDist);
      List<GraphEdge<String, String, GraticuleNode>> dijkstraPath =
          dijkstra.runDijkstraAStar(start, end);

      DijkstraAStar<String, String, GraticuleNode> aStar
          = new DijkstraAStar<>(
          NodeDistanceCalculators::getHaversineDistance,
          MapCommandHandler.getdBProxiedReader()::get,
          HeuristicFuncs::aStarDist);
      List<GraphEdge<String, String, GraticuleNode>> aStarPath =
          aStar.runDijkstraAStar(start, end);

      try {
        assertTrue(oracle(dijkstraPath, aStarPath, start, end));
      } catch (AssertionError e) {
        return false;
      }
      i++;
    }
    return true;
  }

  /** Compares the dijkstra and A* outputs to check for correctness.
   * @param dijkstraOutput is the output List of PathEdges using dijkstra
   * @param aStarOutput is the output List of PathEdges using A*
   * @param start is the Graticule Node at the start of search
   * @param end is the Graticule Node at the end of search
   * @return true or false if all comparisons succeeded.
   */
  boolean oracle(List<GraphEdge<String, String, GraticuleNode>> dijkstraOutput,
                 List<GraphEdge<String, String, GraticuleNode>> aStarOutput,
                 GraticuleNode start, GraticuleNode end) {
    if (dijkstraOutput.size() == 0 && aStarOutput.size() == 0) {
      return true;
    } else {
      double accDijkDist = 0;
      double accAStarDist = 0;

      for (GraphEdge<String, String, GraticuleNode> way : dijkstraOutput) {
        accDijkDist +=
                NodeDistanceCalculators.getHaversineDistance(way.getStartNode(), way.getEndNode());
      }

      for (GraphEdge<String, String, GraticuleNode> way : aStarOutput) {
        accAStarDist +=
                NodeDistanceCalculators.getHaversineDistance(way.getStartNode(), way.getEndNode());
      }

      System.out.println("accDijkDist: " + accDijkDist);
      System.out.println("accAStarDist: " + accAStarDist);

      return dijkstraOutput.get(0).getStartNode().equals(start)
        && aStarOutput.get(0).getStartNode().equals(start)
        && dijkstraOutput.get(dijkstraOutput.size() - 1).getEndNode().equals(end)
        && aStarOutput.get(aStarOutput.size() - 1).getEndNode().equals(end)
        && Double.compare(accDijkDist, accAStarDist) == 0;
    }
  }
}
