package edu.brown.cs.student.pathfinding;

import java.util.Objects;

/** Class represents graticule edges that delineate geographic coordinates.
 */
public class GraticuleEdge implements GraphEdge<String, String, GraticuleNode> {
  private final String id;
  private final String name;
  private final String type;
  private final GraticuleNode start;
  private final GraticuleNode end;
  private Double totalDistance;

  /** Constructor for GraticuleEdge.
   * @param id is a String and must be unique.
   * @param name is a String that is the name of GraticuleEdge.
   * @param type is a String that is the type of the GraticuleEdge.
   * @param start is a GraticuleNode that is at the start of the GraticuleEdge.
   * @param end is a GraticuleNode that is at the end of the GraticuleEdge.
   */
  public GraticuleEdge(String id, String name, String type,
                       GraticuleNode start, GraticuleNode end) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.start = start;
    this.end = end;
  }

  /** Return name of the edge.
   * @return String
   */
  public String getName() {
    return name;
  }

  /** Return type of the edge.
   * @return String
   */
  public String getType() {
    return type;
  }

  /** Return a String that represents the edge.
   * @return String
   */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("GraticuleEdge{");
    str.append("id='" + id + '\'');
    str.append(", start=" + start.toString());
    str.append(", end=" + end.toString());
    str.append(", name='" + name + '\'');
    str.append(", type='" + type + '\'');
    str.append('}');
    return str.toString();
  }

  /** Check if this edge is equal to another.
   * @param o is an object that is supposedly another GraticuleEdge
   * @return boolean
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GraticuleEdge graticuleEdge = (GraticuleEdge) o;
    return id.equals(graticuleEdge.id) && Objects.equals(start, graticuleEdge.start)
        && Objects.equals(end, graticuleEdge.end) && Objects.equals(name, graticuleEdge.name)
        && Objects.equals(type, graticuleEdge.type);
  }

  /** Get a hashcode for a GraticuleEdge.
   @return an int representing the hash index.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, start, end, name, type);
  }

  /** Get the id of the edge.
   * @return String that is the id
   */
  @Override
  public String getId() {
    return id;
  }

  /** Get the start of the GraticuleEdge that is a GraticuleNode.
   * @return a GraticuleNode
   */
  @Override
  public GraticuleNode getStartNode() {
    return start;
  }

  /** Get the end of the GraticuleEdge that is a GraticuleNode.
   * @return a GraticuleNode
   */
  @Override
  public GraticuleNode getEndNode() {
    return end;
  }

  @Override
  public Double getTotalDistance() {
    return totalDistance;
  }

  @Override
  public void setTotalDistance(Double distance) {
    totalDistance = distance;
  }
}
