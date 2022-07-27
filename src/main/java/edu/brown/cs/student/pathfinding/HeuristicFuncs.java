package edu.brown.cs.student.pathfinding;

import java.util.function.BiFunction;

/** contains all methods that calculate heuristics for different pathfind algorithms.
 */
public final class HeuristicFuncs {
  private HeuristicFuncs() { }

  /** The heuristic for A*.
   * @param start is a GraticuleNode that represents the start.
   * @param end is a GraticuleNode that represents the end.
   * @param way is a GraphEdge that has a String ID and connects GraticuleNodes with String IDs.
   * @param distanceFunc is a function that takes in two GraticuleNode and returns the distance
   *                     between the two as a Double.
   * @return a double that represents the weight of the inputted path.
   */
  public static double aStarDist(GraticuleNode start, GraticuleNode end,
                                 GraphEdge<String, String, GraticuleNode> way,
                                 BiFunction<GraticuleNode, GraticuleNode, Double> distanceFunc) {
    return distanceFunc.apply(way.getEndNode(), end);
  }

  /** The heuristic for Dijkstra.
   * @param start is a GraticuleNode that represents the start.
   * @param end is a GraticuleNode that represents the end.
   * @param way is a PathEdge that has a String ID and connects GraticuleNodes with String IDs.
   * @param distanceFunc is a function that takes in two GraticuleNode and returns the distance
   *                     between the two as a Double.
   * @return a constant of 0
   */
  public static double dijkstraDist(GraticuleNode start, GraticuleNode end,
                                    GraphEdge<String, String, GraticuleNode> way,
                                    BiFunction<GraticuleNode, GraticuleNode, Double> distanceFunc) {
    return 0;
  }
}
