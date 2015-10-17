package template;

import java.util.LinkedList;

public abstract class BFS<S> extends Astar<S>{

  public BFS(S start) {
    super(start);
  }

  @Override
  public void insertOpen(SearchNode<S> k, LinkedList<SearchNode<S>> openList) {
    // FIFO queue
    openList.addLast(k);
  }
}
