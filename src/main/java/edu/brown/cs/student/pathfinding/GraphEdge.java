package edu.brown.cs.student.pathfinding;

/** Interface for a GraphEdge that connects two GraphNodes.
 * @param <T> is the ID type of the GraphEdge
 * @param <N> is the ID type of the GraphNode it connects
 * @param <P> is an Object that extends the GraphNode interface
 */
public interface GraphEdge<T, N, P extends GraphNode<N>> {
  /** Get the ID of GraphEdge that is of type T.
   * @return T that represents the ID.
   */
  T getId();

  /** Get the start of the GraphEdge that is a GraphNode.
   * @return type P that extends GraphNode
   */
  P getStartNode();

  /** Get the end of the GraphEdge that is a GraphNode.
   * @return type P that extends GraphNode
   */
  P getEndNode();

  /**
   * Method that returns the distance from the original start node to this current node's end node.
   * This total distance value is used as the priority in the Dijkstra's priority queue.
   *
   * @return the total distance instance variable for the graph edge.
   */
  Double getTotalDistance();

  /**
   * Mutator method for the edge's total distance.
   * This total distance value is used as the priority in the Dijkstra's priority queue.
   * @param distance the new total distance instance variable for the graph edge.
   */
  void setTotalDistance(Double distance);

}
