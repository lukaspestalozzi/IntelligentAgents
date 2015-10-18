package template;

import java.util.LinkedList;

/**
 * Generic Breath First Search algorithm
 *
 * @param <S>
 */
public abstract class BFS<S> extends Astar<S>{

  public BFS(S start) {
    super(start);
  }

  @Override
  public void insertOpen(SearchNode<S> k, LinkedList<SearchNode<S>> openList) {
    // FIFO queue
    openList.addLast(k);
  }
  
  @Override
  public double heuristic(SearchNode<S> s) {
    return 0.0;
  }
}
