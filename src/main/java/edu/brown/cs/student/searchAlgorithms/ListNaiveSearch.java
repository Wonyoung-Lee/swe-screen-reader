package edu.brown.cs.student.searchAlgorithms;

import edu.brown.cs.student.coordinates.Coordinate;
import edu.brown.cs.student.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Wrapper class - uses composition in place of inheritance
/** Class to perform NaiveSearch over coordinates of specified type ID.
 @param <I> Any type for the ID of the Coordinate that is specified
 when constructing a ListNaiveSearch.
 @param <T> Any type that extends the interface Coordinate.
 */
public class ListNaiveSearch<I, T extends Coordinate<I>> {
  private Map<T, Double> keysDist;

  /** Construct a ListNaiveSearch using the passed values by storing in a List
   of KeyDistance.
   @param kNearestNeighbors A List of KeyDistance of the type specified by the KeyDistance ID.
   */
  public ListNaiveSearch(Map<T, Double> kNearestNeighbors) {
    keysDist = kNearestNeighbors.entrySet()
             .stream()
             .sorted(Map.Entry.comparingByValue())
             .collect(Collectors.toMap(
                     Map.Entry::getKey,
                     Map.Entry::getValue,
               (oldValue, newValue) -> oldValue, LinkedHashMap::new));
  }

  /** Get the first few stars within the passed threshold with the least distances,
   randomizing the order of any with tied distances.
   @param k An int threshold representing the maximum number of stars that may be
   printed to console in the event of a successful command execution.
   @return A List of specified type of ID, of size threshold k.
   */
  public List<T> getNaiveNearestNeighbors(int k) {

    List<T> nearestStars = new ArrayList<>();
    List<T> commonDistStars = new ArrayList<>();
    Iterator<Map.Entry<T, Double>> mapIterator = keysDist.entrySet().iterator();
    Double prevDist = 0.0;

    // filledPos represents how many filled positions out of k
    int filledPos = 0;
    try {
      while (filledPos < k && mapIterator.hasNext()) {
        Map.Entry<T, Double> keyDistPair = mapIterator.next();
        // any common dist stars get added to commonDistStars
        if (Double.compare(keyDistPair.getValue(), prevDist) == 0) {
          commonDistStars.add(keyDistPair.getKey());
        } else {
          // if the star does not have common dist, print out everything from
          // commonDistStars after shuffling, then add this star to commonDistStars
          if (commonDistStars.size() == 1) {
            nearestStars.add(commonDistStars.get(0));
            filledPos++;
          } else {
            filledPos = addCommonDistStars(k, commonDistStars, filledPos, nearestStars);
          }
          commonDistStars = new ArrayList<>();
          commonDistStars.add(keyDistPair.getKey());
          prevDist = keyDistPair.getValue();
        }
        mapIterator.remove();
        if (commonDistStars.size() > 0) {
          filledPos = addCommonDistStars(k, commonDistStars, filledPos, nearestStars);
        }
      }
      return nearestStars;
    } catch (IndexOutOfBoundsException e) {
      // occurs when k > no.of stars
      return nearestStars;
    }
  }

  /** Add all star IDs with common distances to the passed array of stars to be
   consoled after calling a method that shuffled aforementioned stars.
   @param k An int threshold representing the maximum number of stars that may be
   printed to console in the event of a successful command execution.
   @param commonDistStars An ArrayList of StarDistance with the same distance,
   of which some IDs will be printed depending on the threshold and filled spots.
   @param filledPos An int representing the number of stars that have been
   added to the passed ArrayList for printing to console.
   @param nearestStarIndices An ArrayList of String containing all star IDs
   that are to be printed to console, in the exact order in which they were added.
   @return An int, the updated number of slots filled for stars within the
   threshold to be printed to console.
   */
  int addCommonDistStars(int k, List<T> commonDistStars, int filledPos,
                         List<T> nearestStarIndices) {
    List<T> shuffledDistStars = Utils.shuffleList(commonDistStars);
    int shuffleIndex = 0;
    while (shuffleIndex < shuffledDistStars.size() && filledPos < k) {
      nearestStarIndices.add(shuffledDistStars.get(shuffleIndex));
      filledPos++;

      shuffleIndex++;
    }
    return filledPos;
  }

  /** Get all possible stars close lying within or on the passed threshold.
   @param r An N threshold representing the maximum radial distance, inclusive,
   of stars that may be printed to console.
   @return A List of specified type of ID, of size threshold k.
   */
  public List<T> getNaiveRadiusSearchResult(double r) {
    List<T> nearestStars = new ArrayList<>();
    Iterator<Map.Entry<T, Double>> mapIterator = keysDist.entrySet().iterator();
    int index = 0;
    while (index < keysDist.size() && mapIterator.hasNext()) {
      Map.Entry<T, Double> keyDistPair = mapIterator.next();
      if (keyDistPair.getValue() <= r) {
        nearestStars.add(keyDistPair.getKey());
        index++;
      } else {
        break;
      }
    }
    return nearestStars;
  }
}
