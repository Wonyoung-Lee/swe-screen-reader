package edu.brown.cs.student.coordinates;

import edu.brown.cs.student.node.TreeNode;
import edu.brown.cs.student.searchAlgorithms.ListNaiveSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

/** Class to create a KdTree of specified type ID.
 @param <I> Any type for the ID of the Coordinates that is specified
 when being used to construct a KdTree.
 @param <T> And generic type that extends Coordinates.
 */

public class KdTree<I, T extends Coordinate<I>> {
  private final int dimensions;
  private TreeNode<I, T> root;
  private final List<T> coordinates;

  /** Set the KdTree to have the passed coordinates with the specified dimensions each.
   @param dimensions the dimension number, from 1 to n where n is a positive integer.
   @param coordinates a list of Coordinates of any identifier/id type
   */
  public KdTree(int dimensions, List<T> coordinates) {
    this.dimensions = dimensions;
    this.root = null;
    this.coordinates = new ArrayList<>(coordinates);
  }

  /** Creates a new Node in KDTree.
   @param element is the element to be stored in Node
   @return a Node storing the inputted element and null children
   */
  private TreeNode<I, T> newNode(T element) {
    return new TreeNode<>(element, null, null);
  }

  /** Adds a node to KDTree.
   @param depth determines the coordinate at which axis to compare with
   @param currentNode is the current Node
   @param coordinate is the element to be added as a Node to the KDTree
   @return a new root Node
   */
  public TreeNode<I, T> addNode(int depth, TreeNode<I, T> currentNode, T coordinate) {
    if (currentNode == null) {
      return newNode(coordinate);
    }
    int axis = depth % dimensions;
    int nextDepth = depth + 1;

    if (coordinate.getCoordinateVal(axis).compareTo(
        currentNode.getValue().getCoordinateVal(axis)) < 0) {
      TreeNode<I, T> currentLeft = currentNode.getLeft();
      currentNode.setLeftChild(addNode(nextDepth, currentLeft, coordinate));
    } else if (coordinate.getCoordinateVal(axis).compareTo(
        currentNode.getValue().getCoordinateVal(axis)) >= 0) {
      TreeNode<I, T> currentRight = currentNode.getRight();
      currentNode.setRightChild(addNode(nextDepth, currentRight, coordinate));
    }
    return currentNode;
  }

  /** Adds all the elements in the given Arraylist as Nodes to the KDTree
   by sorting the collection of elements by coordinate values,
   finding the median element, and creating a tree where the root is the median element,
   * where all elements to the left of the median are pushed to the left subtree,
   * and all of the elements to the right of the median are pushed to the right subtree.
   @param depth determines the coordinate at which axis to compare to
   @param currentNode is the current Node addAll is operating on
   @param coords is the elements to be stored as Nodes
   @return the current node that is the median element
   */
  protected TreeNode<I, T> addAll(int depth, TreeNode<I, T> currentNode,
                                  List<T> coords) {

    if (coords.isEmpty()) {
      return currentNode;
    }
    int axis = depth % dimensions;
    Comparator<T> byDimension
        = Comparator.comparingDouble(T -> T.getCoordinateVal(axis));
    coords.sort(byDimension);
    int median = coords.size() / 2;
    // elements to the left
    List<T> lesserHalf = new ArrayList<>(coords.subList(0, median));
    // elements to the right
    List<T> greaterHalf = new ArrayList<>(coords.subList(median + 1, coords.size()));

    currentNode = addNode(depth, currentNode, coords.get(median));
    currentNode = addAll(depth, currentNode, lesserHalf);
    currentNode = addAll(depth, currentNode, greaterHalf);

    return currentNode;
  }

  /** Builds the KDTree given the root, the median elements
   * and the list of elements to store.
   * @throws IllegalStateException if pointer is out of bound while building tree
   */
  public void buildTree() throws IllegalStateException {
    try {
      root = addAll(0, root, coordinates);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalStateException();
    }
  }

  /** Updates the Priority Queue storing the nearest neighbors as Nodes
   * depending on the distance between the target and current Node
   * and the distance between the target and the farthest nearest neighbors.
   @param target is a list of Doubles that represents the coordinates of the target location.
   @param current is the current Node updateQueue is operating on.
   @param nearestNeighborsSoFar is the Priority Queue storing the nearest neighbors.
   @param numNeighbors is the number of neighbors searching for.
   @return produces the new Priority Queue storing the nearest neighbors.
   */
  public PriorityQueue<TreeNode<I, T>> updateQueue(
      List<Double> target, TreeNode<I, T> current,
      PriorityQueue<TreeNode<I, T>> nearestNeighborsSoFar, int numNeighbors) {
    // if the queue of neighbors is not full, add the current Node
    if (nearestNeighborsSoFar.size() < numNeighbors) {
      current.distanceTo(target);
      nearestNeighborsSoFar.offer(current);
      return nearestNeighborsSoFar;
      // if the current node is closer to the target point
      // than the farthest k-nearest neighbor
    } else if (!(nearestNeighborsSoFar.isEmpty()) && current.distanceTo(target).compareTo(
        nearestNeighborsSoFar.peek().distanceTo(target)) < 0) {
      nearestNeighborsSoFar.poll();
      nearestNeighborsSoFar.offer(current);
      return nearestNeighborsSoFar;
      // if the current node is equidistant to the target
      // as the farther k-nearest neighbor
    } else if (!(nearestNeighborsSoFar.isEmpty()) && current.distanceTo(target).compareTo(
        nearestNeighborsSoFar.peek().distanceTo(target)) == 0) {
      nearestNeighborsSoFar.offer(current);
    }
    return nearestNeighborsSoFar;
  }

  /** Return the root of the KDTree.
   * @return the root Node
   */
  public TreeNode<I, T> getRoot() {
    return root;
  }

  /** Finds the k-nearest neighbors to the target point.
   @param depth determines the relevant axis for comparison
   @param target is a list of Doubles that represents the target point
   @param current is the current Node searchNN is operating on
   @param nearestNeighborsSoFar is the PriorityQueue of nearest neighbors
   @param numNeighbors is the number of neighbors searching for.
   @return returns an updated PriorityQueue with the nearest neighbors
   */
  public PriorityQueue<TreeNode<I, T>> searchNearestNeighbors(int depth, List<Double> target,
                                                              TreeNode<I, T> current,
                                                              PriorityQueue<TreeNode<I, T>>
                                                                nearestNeighborsSoFar,
                                                              int numNeighbors) {
    // find the relevant axis, according to the depth
    int axis = depth % dimensions;
    int nextDepth = depth + 1;
    if (current == null) {
      return nearestNeighborsSoFar;
    }
    nearestNeighborsSoFar = updateQueue(target, current, nearestNeighborsSoFar,
        numNeighbors);

    // the relevant axis distance between the current node and target point
    Double axisDistanceFromTargetToCurrent =
        target.get(axis) - (Math.abs(current.getValue().getCoordinateVal(axis)));
    if (!(nearestNeighborsSoFar.isEmpty())
        && nearestNeighborsSoFar.peek().distanceTo(target).compareTo(
        axisDistanceFromTargetToCurrent) >= 0) {
      // recur on both children
      searchNearestNeighbors(nextDepth, target, current.getLeft(), nearestNeighborsSoFar,
          numNeighbors);
      searchNearestNeighbors(nextDepth, target, current.getRight(), nearestNeighborsSoFar,
          numNeighbors);
      // if the current node's coordinate on the relevant axis is
      // less than target's coordinate, recur on the right child
    } else if (!(nearestNeighborsSoFar.isEmpty())
        && target.get(axis).compareTo(current.getValue()
        .getCoordinateVal(axis)) >= 0) {
      searchNearestNeighbors(nextDepth, target, current.getRight(), nearestNeighborsSoFar,
          numNeighbors);
      // if the current node's coordinate on the relevant axis is
      // greater than target's coordinate, recur on the left child
    } else if (!(nearestNeighborsSoFar.isEmpty())
        && target.get(axis).compareTo(current.getValue()
        .getCoordinateVal(axis)) < 0) {
      searchNearestNeighbors(nextDepth, target, current.getLeft(), nearestNeighborsSoFar,
          numNeighbors);
    }
    return nearestNeighborsSoFar;
  }

  /** Finds all the stars within the radius r to the target.
   @param depth determines the relevant axis for comparison
   @param target is a list of Doubles that represents the target point
   @param current is the current Node searchRadius is operating on
   @param r is the radius to search within
   @param nearest is the PriorityQueue storing all the stars within radius r to the target
   @return returns an update PriorityQueue
   */
  public PriorityQueue<TreeNode<I, T>> searchRadius(int depth, List<Double> target,
                                                    TreeNode<I, T> current, Double r,
                                                    PriorityQueue<TreeNode<I, T>> nearest) {
    // find the relevant axis, according to the depth
    int axis = depth % dimensions;
    int nextDepth = depth + 1;
    if (current == null) {
      return nearest;
    }
    // the relevant axis distance between the current node and target point
    Double distanceFromTargetToCurrent =
        target.get(axis) - (Math.abs(current.getValue().getCoordinateVal(axis)));
    // updates the Queue of stars if currentNode is within the radius to target
    if (current.distanceTo(target).compareTo(r) <= 0) {
      nearest.add(current);
    }

    if (r.compareTo(distanceFromTargetToCurrent) >= 0) {
      // recur on both children
      searchRadius(nextDepth, target, current.getLeft(), r, nearest);
      searchRadius(nextDepth, target, current.getRight(), r, nearest);
    } else if (target.get(axis).compareTo(current.getValue().getCoordinateVal(axis)) >= 0) {
      // if the current node's coordinate on the relevant axis is
      // less than target's coordinate, recur on the right child
      searchRadius(nextDepth, target, current.getRight(), r, nearest);
    } else if (target.get(axis).compareTo(current.getValue().getCoordinateVal(axis)) < 0) {
      // if the current node's coordinate on the relevant axis is
      // greater than target's coordinate, recur on the left child
      searchRadius(nextDepth, target, current.getLeft(), r, nearest);
    }
    return nearest;
  }

  /** Produces a list of sorted nearest neighbors and randomly selected
   * equidistant coordinates if necessary.
   @param n is the number of neighbors to search for.
   @param targetPoint is a Coordinate represents the target position.
   @param excludeTarget determines whether to exclude the
   given target point as one of the nearest neighbors output.
   @return the final list of k-nearest neighbors, may include
   equidistant stars that exceed the limit of k.
   */
  public List<T> getNearestNeighborsResult(int n,  Coordinate<I> targetPoint,
                                           boolean excludeTarget) {
    int numNeighbors = n;

    if (excludeTarget) {
      // this is to account for the fact that the neighbors will exclude the target star
      numNeighbors = n + 1;
    }

    Comparator<TreeNode<I, T>> sortByReversedDistance = Comparator
        .comparing(node -> -1 * node.distanceTo(targetPoint.getCoordinates()));
    // reverse sorting so that the farthest nearest neighbor
    // is easily accessible at the front of the queue
    PriorityQueue<TreeNode<I, T>> nearestNeighborsSoFar
        = new PriorityQueue<>(sortByReversedDistance);
    nearestNeighborsSoFar
        = searchNearestNeighbors(0, targetPoint.getCoordinates(),
        root, nearestNeighborsSoFar, numNeighbors);
    if (nearestNeighborsSoFar == null) {
      return null;
    }
    List<TreeNode<I, T>> neighbors = new ArrayList<>();
    while (!nearestNeighborsSoFar.isEmpty()) {
      TreeNode<I, T> neighbor = nearestNeighborsSoFar.remove();
      if (excludeTarget) {
        if (!(neighbor.getValue().getId().equals(targetPoint.getId()))) {
          neighbors.add(neighbor);
        }
      } else {
        neighbors.add(neighbor);
      }
    }
    Collections.reverse(neighbors);

    // begin conversion process of PriorityQueue into ListNaiveSearch to handle randomness
    // of tied distance star IDs
    Map<T, Double> keyDistance = new HashMap<>();
    for (TreeNode<I, T> neighbor : neighbors) {
      keyDistance.put(neighbor.getValue(), neighbor
          .distanceTo(targetPoint.getCoordinates()));
    }
    ListNaiveSearch<I, T> kNN = new ListNaiveSearch<>(keyDistance);
    return kNN.getNaiveNearestNeighbors(n);
  }

  /** Produces a list of sorted coordinates within radius.
   @param r is the radius to search within.
   @param targetPoint is the list of Doubles that represents the target position.
   @param excludeTarget determines whether to exclude the
   given target point as one of the nearest neighbors output.
   @return the final list of stars within the radius
   */
  public List<T> getRadiusSearchResult(Double r, Coordinate<I> targetPoint,
                                       boolean excludeTarget) {
    Comparator<TreeNode<I, T>> sortByDistance = Comparator
        .comparing(node -> node.distanceTo(targetPoint.getCoordinates()));
    PriorityQueue<TreeNode<I, T>> nearestWithinRSoFar
        = new PriorityQueue<>(sortByDistance);
    nearestWithinRSoFar = searchRadius(0, targetPoint.getCoordinates(),
        root, r, nearestWithinRSoFar);
    if (nearestWithinRSoFar == null) {
      return null;
    }
    List<T> neighbors = new ArrayList<>();
    while (!nearestWithinRSoFar.isEmpty() && (nearestWithinRSoFar.peek()
        .distanceTo(targetPoint.getCoordinates()).compareTo(r) <= 0)) {
      TreeNode<I, T> neighbor = nearestWithinRSoFar.remove();
      if (excludeTarget) {
        if (!(neighbor.getValue().getId().equals(targetPoint.getId()))) {
          neighbors.add(neighbor.getValue());
        }
      } else {
        neighbors.add(neighbor.getValue());
      }
    }
    return neighbors;
  }

  /** Represent the KdTree as a String.
   @return a String representation of a KdTree.
   */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("KdTree{");
    str.append("dimensions=" + dimensions);
    if (this.root == null) {
      str.append(", tree=NULL");
      str.append('}');
      return str.toString();
    } else {
      str.append(", tree=" + root.toString());
      str.append('}');
      return str.toString();
    }
  }

  /** Check if this KdTree is equal to the passed object.
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
    KdTree<?, ?> kdTree = (KdTree<?, ?>) o;
    return dimensions == kdTree.dimensions && Objects.equals(root, kdTree.root)
        && Objects.equals(coordinates, kdTree.coordinates);
  }

  /** Get a hashcode for a KdTree.
   @return an int representing the hash index.
   */
  @Override
  public int hashCode() {
    return Objects.hash(dimensions, root);
  }
}
