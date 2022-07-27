package edu.brown.cs.student.pathfinding;

import java.util.List;

/** Interface for a GraphNode that has an ID and coordinates.
 * @param <T> is the ID type of the GraphNode
 */
public interface GraphNode<T> {
  /** Get the value of GraphNode's coordinate at given dimension.
   * @param dim is an int that represents a given dimension
   * @return a Double that is the coordinate value
   */
  Double getCoordinateVal(int dim);

  /** Get the ID of GraphNode that is of type T.
   * @return T that represents the ID.
   */
  T getId();

  /** Get the coordinates of the GraphNode.
   * @return a list of Double that represents coordinates.
   */
  List<Double> getCoordinates();
}
