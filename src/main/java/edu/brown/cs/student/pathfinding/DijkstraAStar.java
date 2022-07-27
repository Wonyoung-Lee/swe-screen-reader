package edu.brown.cs.student.pathfinding;

import edu.brown.cs.student.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Class that performs the A* or Dijkstra pathfind algorithm.
 * @param <E> is the ID type of the GraphEdge
 * @param <N> is the ID type of the GraphNode
 * @param <P> is an object type that extends GraphNode
 */
public class DijkstraAStar<E, N, P extends GraphNode<N>> {
  private final BiFunction<P, P, Double> distanceFunc;
  private final Function<P, Set<GraphEdge<E, N, P>>>
          queryNeighborEdgesFunc;
  private final Utils.Function4To1<P, P, GraphEdge<E, N, P>,
          BiFunction<P, P, Double>, Double> heuristicFunc;
  private Map<N, GraphEdge<E, N, P>> visited;
  private PriorityQueue<GraphEdge<E, N, P>> routes;

  /**
   * Constructor for DijkstraAStar.
   *
   * @param distanceFunc           is a function that takes two GraphNodes and returns a double that
   *                               is the distance between the two nodes
   * @param queryNeighborEdgesFunc is a function that queries the
   *                               adjacent edges starting from a target node
   * @param heuristicFunc          is a function that calculates the heuristic of a node
   */
  public DijkstraAStar(BiFunction<P, P, Double> distanceFunc,
                       Function<P, Set<GraphEdge<E, N, P>>> queryNeighborEdgesFunc,
                       Utils.Function4To1<P,
                               P,
                               GraphEdge<E, N, P>,
                               BiFunction<P, P, Double>,
                               Double> heuristicFunc) {
    this.queryNeighborEdgesFunc = queryNeighborEdgesFunc;
    this.heuristicFunc = heuristicFunc;
    this.distanceFunc = distanceFunc;
    this.visited = new HashMap<>();
    this.routes = new PriorityQueue<GraphEdge<E, N, P>>(new Comparator<GraphEdge<E, N, P>>() {
      @Override
      public int compare(GraphEdge<E, N, P> e1, GraphEdge<E, N, P> e2) {
        Double e1Distance = e1.getTotalDistance();
        Double e2Distance = e2.getTotalDistance();
        return e1Distance.compareTo(e2Distance);
      }
    });
  }

    /**
   * Method responsible for the Dijkstra/AStar search. Given the starting and ending
   * GraphNodes, this method finds the shortest path between them. Because graphs can
   * be really big, this method relies on querying the database every time it looks for
   * outgoing edges. This way we don't need to store and build the whole graph in memory.
   * Querying for outgoing edges is optimized further via caching.
   *
   * @param startNode the start node of the path
   * @param endNode   the end node of the path
   * @return a list of GraphEdges that represent the shortest path between startNode and
   * endNode
   */
  public List<GraphEdge<E, N, P>> runDijkstraAStar(P startNode, P endNode) {
    Set<GraphEdge<E, N, P>> initOutwardEdges = queryNeighborEdgesFunc.apply(startNode);

    for (GraphEdge<E, N, P> edge : initOutwardEdges) {
      Double edgeDistance = distanceFunc.apply(edge.getStartNode(), edge.getEndNode());
      Double heuristicDistance = heuristicFunc.apply(startNode, endNode, edge, distanceFunc);

      edge.setTotalDistance(edgeDistance + heuristicDistance);
    }

    routes.addAll(initOutwardEdges);
    while (!routes.isEmpty()) {
      while (visited.containsKey(routes.peek().getEndNode().getId())) {
        routes.poll();
        if (routes.isEmpty()) {
          return new ArrayList<>();
        }
      }

      GraphEdge<E, N, P> nextWay = routes.poll();

      visited.put(nextWay.getEndNode().getId(), nextWay);

      if (nextWay.getEndNode().getId().equals(endNode.getId())) {
        break;
      }

      Set<GraphEdge<E, N, P>> outWardEdges = queryNeighborEdgesFunc.apply(nextWay.getEndNode());

      double curTotalDistance = nextWay.getTotalDistance();

      for (GraphEdge<E, N, P> edge : outWardEdges) {
        Double edgeDistance = distanceFunc.apply(edge.getStartNode(), edge.getEndNode());
        Double heuristicDistance = heuristicFunc.apply(startNode, endNode, edge, distanceFunc);

        edge.setTotalDistance(curTotalDistance + edgeDistance + heuristicDistance);

        routes.add(edge);
      }
    }

    return findRoute(startNode, endNode);
  }

    /**
   * This method takes the visited hashmap built by runDijkstra and
   * uses it to backtrack from the endNode all the way to the startNode.
   * As it backtracks, it builds the list of GraphEdges that represent the
   * shortest path.
   *
   * @param startNode the start node of the path
   * @param endNode   the end node of the path
   * @return a list of GraphEdges that represent the shortest path between startNode and
   * endNode
   */
  private List<GraphEdge<E, N, P>> findRoute(P startNode, P endNode) {
    List<GraphEdge<E, N, P>> edgesList = new ArrayList<>();

    if (!visited.containsKey(endNode.getId())) {
      return new ArrayList<>();
    }

    N startNodeID = startNode.getId();
    P curNode = endNode;
    while (!curNode.getId().equals(startNodeID)) {
      GraphEdge<E, N, P> curEdge = visited.get(curNode.getId());
      edgesList.add(curEdge);
      curNode = curEdge.getStartNode();
    }

    Collections.reverse(edgesList);

    return edgesList;
  }

}

//
//
//  /** Return a list of PathEdges that connect start Node to end Node in the quickest way possible.
//   * @param start is type P that extends PathNode that is the start of the search
//   * @param end type P that extends PathNode that is the end of the search
//   * @return a list of PathEdges of type E, N, and P
//   * @throws NullPointerException if no database has been loaded
//   */
//  public List<PathEdge<E, N, P>> pathFromStartToEnd(P start,
//                                                    P end)
//    throws NullPointerException {
//    return recurseAdjacentsBreadthwise(start, end);
//  }
//
//  /** Finds the next shortest path for the next node from start until end is reached.
//   * @param start is of type P that is the start of the pathfind
//   * @param end is of type P that is the end of the pathfind
//   * @return a list of PathEdges that make the shortest path from start to end or an empty list
//   * if no such path exists
//   * @throws NullPointerException if no database has been loaded
//   */
//  private List<PathEdge<E, N, P>> recurseAdjacentsBreadthwise(
//      P start, P end)
//    throws NullPointerException {
//    List<PathEdge<E, N, P>> accumulatedPath = new ArrayList<>();
//
//    double accumulatedDistance = 0;
//    Set<N> seenDijkstraNodeIDs = new HashSet<>();
//    seenDijkstraNodeIDs.add(start.getId());
//    Comparator<PathWeightHeuristic<List<PathEdge<E, N, P>>>> sortByDistance = Comparator
//        .comparing(PathWeightHeuristic::getTotalDistance);
//    PriorityQueue<PathWeightHeuristic<List<PathEdge<E, N, P>>>> candidatePaths
//        = new PriorityQueue<>(sortByDistance);
//
//    // because there are two terminating conditions, we decided to use a while loop.
//    // An if statement would be inappropriate since we don't know when it will terminate.
//    while (true) {
//      Set<PathEdge<E, N, P>> neighborPaths;
//
//      // if the current list of accumulated pathEdges is empty,
//      // then find adjacent edges from start
//      if (accumulatedPath.size() == 0) {
//        neighborPaths = getEdgesFromTargetNode(start);
//      } else {
//        // otherwise, get the adjacent edges from the end node of the last pathEdge
//        // in the accumulated list
//        neighborPaths
//          = getEdgesFromTargetNode(
//          accumulatedPath.get(accumulatedPath.size() - 1).getEnd());
//      }
//
//      // filter out the adjacent pathEdges from neighborPaths that
//      // have already been traversed
//      Set<PathEdge<E, N, P>> pathsToNewNodes
//          = getNeverSeenNeighbors(neighborPaths, seenDijkstraNodeIDs);
//
//      // update the candidate paths priority queue, so that the shortest path is
//      // at the front of the queue.
//      updateCandidatePaths(candidatePaths, pathsToNewNodes, accumulatedPath,
//          accumulatedDistance, start, end);
//
//      if (candidatePaths.size() == 0 || start.equals(end)) {
//        return new ArrayList<>();
//      }
//
//      // get the shortest candidate path at the front of the queue.
//      PathWeightHeuristic<List<PathEdge<E, N, P>>> wrappedMinCandPath =
//          getMinCandPath(seenDijkstraNodeIDs, candidatePaths);
//
//      List<PathEdge<E, N, P>> minCandPath = wrappedMinCandPath.getPath();
//
//      // if the shorted candidate path reaches the end, return it and terminate loop
//      if (minCandPath.get(minCandPath.size() - 1).getEnd().getId().equals(end.getId())) {
//        return minCandPath;
//      }
//      // otherwise, add it to the list of seen node ids to which shortest
//      // paths have already been found and update accumulate path and distance.
//      seenDijkstraNodeIDs.add(minCandPath.get(minCandPath.size() - 1).getEnd().getId());
//      accumulatedPath = minCandPath;
//      accumulatedDistance = wrappedMinCandPath.getDistance();
//    }
//  }
//
//  /** Get the minimum path to a node that has not already been seen.
//   * @param seenDijkstraNodeIDs is a list of N that are the seen node ids
//   *                            to which shortest paths have already been found.
//   * @param candidatePaths is a priority queue of PathWeightHeuristics of candidate
//   *                      shortest paths to different nodes
//   * @return a PathWeightHeuristic that stores the list of
//   pathEdges that make of the shortest path.
//   */
//  PathWeightHeuristic<List<PathEdge<E, N, P>>> getMinCandPath(
//      Set<N> seenDijkstraNodeIDs,
//      PriorityQueue<PathWeightHeuristic<List<PathEdge<E, N, P>>>> candidatePaths) {
//    PathWeightHeuristic<List<PathEdge<E, N, P>>> minCandPath = candidatePaths.poll();
//    if (seenDijkstraNodeIDs.contains(minCandPath.getPath()
//        .get(minCandPath.getPath().size() - 1).getEnd().getId())) {
//      return getMinCandPath(seenDijkstraNodeIDs, candidatePaths);
//    } else {
//      return minCandPath;
//    }
//  }
//
//  /** Get a Set of PathEdges that start at the given node.
//   * @param node is the given node that extends PathNode
//   * @return a Set of PathEdges.
//   * @throws NullPointerException if no database has been loaded.
//   */
//  Set<PathEdge<E, N, P>> getEdgesFromTargetNode(P node)
//    throws NullPointerException {
//    return queryNeighborEdgesFunc.apply(node);
//  }
//
//  /** Update the priority queue of candidate paths by adding PathWeightHeuritstics with
//   * each neighborPath, their heuristic, and distance.
//   * @param candidatePaths is a priority queue of PathWeightHeuristics of candidate
//   *                       shortest paths to different nodes
//   * @param neighborPaths is the set of PathEdges that are adjacent to a given node.
//   * @param accumulatedPath is the a list of PathEdges that
//   *                        is the accummulated paths to a certain node.
//   * @param accumulatedDistance is a double that is the accumulated distance of the paths.
//   * @param start is the PathNode at which pathfind starts.
//   * @param end is the PathNode at which pathfind ends.
//   */
//  private void updateCandidatePaths(
//      PriorityQueue<PathWeightHeuristic<List<PathEdge<E, N, P>>>> candidatePaths,
//      Set<PathEdge<E, N, P>> neighborPaths,
//      List<PathEdge<E, N, P>> accumulatedPath,
//      double accumulatedDistance,
//      P start, P end) {
//    for (PathEdge<E, N, P> way : neighborPaths) {
//      List<PathEdge<E, N, P>> currentPath = new ArrayList<>(accumulatedPath);
//      currentPath.add(way);
//
//      double currentDistance = accumulatedDistance
//          + distanceFunc.apply(way.getStart(), way.getEnd());
//
//      double heuristic = currentDistance
//          + heuristicFunc.apply(start, end, way, distanceFunc);
//
//      candidatePaths.add(new PathWeightHeuristic<>(currentPath, currentDistance, heuristic));
//    }
//  }
//
//  /** Get a set of PathEdges adjacent to a certain node that have not yet been traversed.
//   * @param neighborPaths is a set of PathEdges that are all the paths
//   *                      adjacent to a certain node
//   * @param seenDijkstraNodeIDs is a set of N that are the seen node ids
//   *    *                            to which shortest paths have already been found.
//   * @return a set of PathEdges that are the neighboring PathEdges yet to be seen.
//   */
//  Set<PathEdge<E, N, P>> getNeverSeenNeighbors(Set<PathEdge<E, N, P>> neighborPaths,
//                                               Set<N> seenDijkstraNodeIDs) {
//    Set<PathEdge<E, N, P>> neverSeenNeighbors = new HashSet<>();
//    for (PathEdge<E, N, P> candidatePath : neighborPaths) {
//      if (!seenDijkstraNodeIDs.contains(candidatePath.getEnd().getId())) {
//        neverSeenNeighbors.add(candidatePath);
//      }
//    }
//    return neverSeenNeighbors;
//  }
//}
//
///**
// * Responsible for generically performing an Dijkstra or AStar search.
// *
// * @param <V> A type variable that extends GraphNode
// * @param <E> A type variable that extends GraphEdge
// */
//public class DijkstraAStar<V extends GraphNode<E>, E extends GraphEdge<V>> {
//
//  private Map<String, E> visited;
//  private PriorityQueue<E> routes;
//  private boolean isAStar;
//
//  /**
//   * Constructor for DijkstraAStar class. It initializes the visited
//   * hashmap which is responsible for mapping visited nodes to the edges
//   * that lead to their shortest path back to the origin. It also
//   * initializes the PriorityQueue of GraphEdges responsible for the greedy
//   * mechanism of popping off next edge to traverse.
//   *
//   * @param isAStar a boolean that should be true for AStar search and false
//   *                for traditional AStar search.
//   */
//  public DijkstraAStar(boolean isAStar) {
//    this.visited = new HashMap<>();
//    this.routes = new PriorityQueue<E>(new Comparator<E>() {
//      @Override
//      public int compare(E e1, E e2) {
//        Double e1Distance = e1.getTotalDistance();
//        Double e2Distance = e2.getTotalDistance();
//        if (isAStar) {
//          return e1.getAStarDistance().compareTo(e2.getAStarDistance());
//        }
//        return e1Distance.compareTo(e2Distance);
//      }
//    });
//    this.isAStar = isAStar;
//  }
//
//  /**
//   * Method responsible for the Dijkstra/AStar search. Given the starting and ending
//   * GraphNodes, this method finds the shortest path between them. Because graphs can
//   * be really big, this method relies on querying the database every time it looks for
//   * outgoing edges. This way we don't need to store and build the whole graph in memory.
//   * Querying for outgoing edges is optimized further via caching.
//   *
//   * @param startNode the start node of the path
//   * @param endNode   the end node of the path
//   * @return a list of GraphEdges that represent the shortest path between startNode and
//   * endNode
//   */
//  public List<E> runDijkstra(V startNode, V endNode) {
//    List<E> initOutwardEdges = startNode.getOutgoingEdges();
//    if (isAStar) {
//      for (E edge : initOutwardEdges) {
//        edge.setAStarDistance(edge.getTotalDistance() +
//        endNode.getDistanceFrom(edge.getEndNode()));
//      }
//    }
//    routes.addAll(initOutwardEdges);
//    while (!routes.isEmpty()) {
//      while (visited.containsKey(routes.peek().getEndNode().getID())) {
//        routes.poll();
//        if (routes.isEmpty()) {
//          return new ArrayList<>();
//        }
//      }
//
//      E nextWay = routes.poll();
//
//      visited.put(nextWay.getEndNode().getID(), nextWay);
//
//      if (nextWay.getEndNode().getID().equals(endNode.getID())) {
//        break;
//      }
//
//      List<E> outWardEdges = nextWay.getEndNode().getOutgoingEdges();
//
//      double curTotalDistance = nextWay.getTotalDistance();
//
//      for (E edge : outWardEdges) {
//        edge.setTotalDistance(curTotalDistance + edge.getTotalDistance());
//
//        if (isAStar) {
//          edge.setAStarDistance(edge.getTotalDistance()
//                  + endNode.getDistanceFrom(edge.getEndNode()));
//        }
//
//        routes.add(edge);
//      }
//    }
//
//    return findRoute(startNode, endNode);
//  }
//
//  /**
//   * This method takes the visited hashmap built by runDijkstra and
//   * uses it to backtrack from the endNode all the way to the startNode.
//   * As it backtracks, it builds the list of GraphEdges that represent the
//   * shortest path.
//   *
//   * @param startNode the start node of the path
//   * @param endNode   the end node of the path
//   * @return a list of GraphEdges that represent the shortest path between startNode and
//   * endNode
//   */
//  private List<E> findRoute(V startNode, V endNode) {
//    List<E> edgesList = new ArrayList<>();
//
//    if (!visited.containsKey(endNode.getID())) {
//      return new ArrayList<>();
//    }
//
//    String startNodeID = startNode.getID();
//    V curNode = endNode;
//    while (!curNode.getID().equals(startNodeID)) {
//      E curEdge = visited.get(curNode.getID());
//      edgesList.add(curEdge);
//      curNode = curEdge.getStartNode();
//    }
//
//    Collections.reverse(edgesList);
//
//    return edgesList;
//  }
//
//}
//
