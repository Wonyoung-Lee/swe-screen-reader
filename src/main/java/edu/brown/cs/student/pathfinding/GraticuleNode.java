package edu.brown.cs.student.pathfinding;

import edu.brown.cs.student.coordinates.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Class represents graticule nodes that have an id and latitude and longitude as coordinates.
 */
public class GraticuleNode implements Coordinate<String>, GraphNode<String> {
  private final String id;
  private final List<Double> coordinates;

  /** Constructor for GraticuleNode.
   * @param id is a String that represents the id of GraticuleNode.
   * @param latitude is a double that represents the latitude.
   * @param longitude is a double that represents the longitude.
   */
  public GraticuleNode(String id, double latitude, double longitude) {
    this.id = id;
    List<Double> coor = new ArrayList<>();
    coor.add(latitude);
    coor.add(longitude);

    this.coordinates = coor;
  }

  /** Get the latitude of the graticule node.
   * @return the first element in the stored coordinates.
   */
  public double getLatitude() {
    return getCoordinateVal(0);
  }

  /** Get the longitude of the graticule node.
   * @return the second element in the stored coordinates.
   */
  public double getLongitude() {
    return getCoordinateVal(1);
  }

  /** Get the value of a coordinate given the dimension.
   * @param dim is an int that represents a dimension.
   * @return a double that is the value of a coordinate at the given dimension
   */
  @Override
  public Double getCoordinateVal(int dim) {
    return coordinates.get(dim);
  }

  /** Get the ID of the GraticuleNode.
   * @return a String that represents its id.
   */
  @Override
  public String getId() {
    return id;
  }

  /** Get the coordinates of the GraticuleNode.
   * @return a list of Doubles that represents the coordinates.
   */
  @Override
  public List<Double> getCoordinates() {
    return coordinates;
  }

  /** Check if this GraticuleNode is equal to another.
   * @param o is an object that is supposedly another GraticuleNod
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
    GraticuleNode graticuleNode = (GraticuleNode) o;
    return Objects.equals(id, graticuleNode.id)
        && Objects.equals(coordinates, graticuleNode.coordinates);
  }

  /** Return a String that represents a GraticuleNode.
   * @return String
   */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("GraticuleNode{");
    str.append("id='" + id + '\'');
    str.append(", coordinates=" + coordinates);
    str.append('}');
    return str.toString();
  }

  /** Get a hashcode for a GraticuleNode.
   @return an int representing the hash index.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, coordinates);
  }
}
