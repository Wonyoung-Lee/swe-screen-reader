package edu.brown.cs.student.pathfinding;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/** Class caches the neighboring edges for nodes while performing A* search.
 * @param <N> is the ID type of the PathNode
 * @param <E> is the ID type of the cached PathEdge
 * @param <P> is the PathNode that the cached PathEdges connects
 */
public class ProxiedEdgeFetcher<N, E, P extends GraphNode<N>> {
  private LoadingCache<P, Set<GraphEdge<E, N, P>>> edgeMapCache;

  /** Constructor for ProxiedEdgeFetcher.
   * @param queryNeighborEdgesFunc is a function that queries adjacent PathEdges
   *                              starting from a certain node and returns them.
   */
  public ProxiedEdgeFetcher(Function<P, Set<GraphEdge<E, N, P>>>
                                queryNeighborEdgesFunc) {
    final int cacheSize = 100;
    edgeMapCache = CacheBuilder.newBuilder()
        .maximumSize(cacheSize)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build(new CacheLoader<>() {
          @Override
          public Set<GraphEdge<E, N, P>> load(P node) {
            return queryNeighborEdgesFunc.apply(node);
          }
        });
  }

  /** If a given node exists in caches, retrieves the adjacent edges starting from node from cache;
   * otherwise runs queryNeighborEdgesFunc to find those edges.
   * @param node is of type P that is the given PathNode
   * @return a Set of PathEdge
   */
  public Set<GraphEdge<E, N, P>> get(P node) {
    return edgeMapCache.getUnchecked(node);
  }
}
