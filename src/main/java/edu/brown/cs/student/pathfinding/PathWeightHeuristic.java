package edu.brown.cs.student.pathfinding;


import java.util.Objects;

/** Class associates each path with its accumulated distance and last node's heuristic.
 @param <T> Any type for the path
 */
public class PathWeightHeuristic<T> {
  private final T path;
  private final Double distance;
  private final Double heuristic;

  /** Constructor for PathWeightHeuristic.
   * @param path is of type T (usually a list of PathEdges)
   * @param distance is a Double that is the accumulated distance
   *                 of path from a starting node
   * @param heuristic is a Double that is the heuristic of a certain node along path
   */
  public PathWeightHeuristic(T path, Double distance, Double heuristic) {
    this.path = path;
    this.distance = distance;
    this.heuristic = heuristic;
  }

  /** Get the accumulated distance of the path.
   @return a double number, i.e., a real number.
   */
  public Double getDistance() {
    return distance;
  }

  /** Get the stored path.
   @return T that is the type of the path.
   */
  public T getPath() {
    return path;
  }

  /** Get the heuristic of the last node in the path.
   * @return a double that represents the heuristic.
   */
  public Double getHeuristic() {
    return heuristic;
  }

  /** Get the total distance of the stored path.
   * @return a double that is the sum of distance and heuristic
   */
  public double getTotalDistance() {
    return this.distance + this.heuristic;
  }

  /** Represent the KeyDistance as a String.
   @return a String representation of a KeyDistance.
   */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("PathWeightHeuristic{");
    str.append("path=" + path);
    str.append(", distance=" + distance);
    str.append(", heuristic=" + heuristic);
    str.append('}');
    return str.toString();
  }

  /** Check if this KeyDistance is equal to the passed object.
   @param o Another object
   @return a Boolean ture/false if the objects are equal.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PathWeightHeuristic<?> that = (PathWeightHeuristic<?>) o;
    return Objects.equals(path, that.path) && Objects.equals(distance, that.distance);
  }

  /** Get a hashcode for a KeyDistance.
   @return an int representing the hash index.
   */
  @Override
  public int hashCode() {
    return Objects.hash(path, distance);
  }
}
