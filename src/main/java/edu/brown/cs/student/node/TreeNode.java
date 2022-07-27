package edu.brown.cs.student.node;

import edu.brown.cs.student.coordinates.Coordinate;

import java.util.List;
import java.util.Objects;

/** Class to create a Node of specified type value.
 @param <I> Any type for the ID of the Coordinate stored in
 the Tree Node.
 @param <T> Any generic type that extends Coordinate
 for the value of the Node that is specified
 when constructing a Node.
 */
public class TreeNode<I, T extends Coordinate<I>> {
  private final T value;
  private TreeNode<I, T> left;
  private TreeNode<I, T> right;

  /** Create an instance of a Node of specified type with the passed arguments.
   @param value Represents the value of the Node of the type specified.
   @param left A Node of the same type as the value representing the left child of
   this Node instantiation.
   @param right A Node of the same type as the value representing the right child of
   this Node instantiation.
   */
  public TreeNode(T value, TreeNode<I, T> left, TreeNode<I, T> right) {
    this.value = value;
    this.left = left;
    this.right = right;
  }

  /** Get the value of the Node.
   @return Returns the value of the Node of type specified when originally constructing the Node.
   */
  public T getValue() {
    return value;
  }

  /** Get the left Node.
   @return Returns the left Node child of this Node of the same type.
   */
  public TreeNode<I, T> getLeft() {
    return left;
  }

  /** Get the right Node.
   @return Returns the right Node child of this Node of the same type.
   */
  public TreeNode<I, T> getRight() {
    return right;
  }

  /**
   * sets left child Node to a given Node.
   * @param l is the given Node.
   */
  public void setLeftChild(final TreeNode<I, T> l) {
    this.left = l;
  }

  /** Sets right child Node to a given Node.
   * @param r is the given Node
   */
  public void setRightChild(final TreeNode<I, T> r) {
    this.right = r;
  }


  /** Represent the Node as a String.
   @return a String representation of a Node.
   */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("TreeNode{");
    str.append("value=" + value);
    if (this.getLeft() == null && this.getRight() == null) {
      str.append(", left=NULL");
      str.append(", right=NULL");
      str.append('}');
    } else if (this.getLeft() == null) {
      str.append(", left=NULL");
      str.append(", right=" + right.toString());
      str.append('}');
    } else if (this.getRight() == null) {
      str.append(", left=" + left.toString());
      str.append(", right=NULL");
      str.append('}');
    } else {
      str.append(", left=" + left.toString());
      str.append(", right=" + right.toString());
      str.append('}');
    }
    return str.toString();
  }

  /** Check if this Node is equal to the passed object.
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
    TreeNode<?, ?> node = (TreeNode<?, ?>) o;
    return Objects.equals(value, node.value) && Objects.equals(left, node.left)
        && Objects.equals(right, node.right);
  }

  /** Get a hashcode for a Node.
   @return an int representing the hash index.
   */
  @Override
  public int hashCode() {
    return Objects.hash(value, left, right);
  }

  /** Calculate the Euclidean distance between
   * the object and the given target location
   * and returns distanceAway.
   * @param targetCoors is the coordinates of the
   *                    target location
   * @return Double
   */
  public Double distanceTo(final List<Double> targetCoors) {
    double sumOfSqr = 0.0;
    for (int i = 0; i < targetCoors.size(); i++) {
      double difference = value.getCoordinateVal(i) - (targetCoors.get(i));
      double squared = difference * difference;
      sumOfSqr += squared;
    }
    return Math.sqrt(sumOfSqr);
  }
}


