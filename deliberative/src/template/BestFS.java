package template;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * Generic Best first search algorithm.
 */
public abstract class BestFS<S> extends Astar<S> {

  public BestFS(S start) {
    super(start);
  }

  @Override
  public void insertOpen(SearchNode<S> k, LinkedList<SearchNode<S>> openList) {
    Iterator<SearchNode<S>> it = openList.iterator();
    double f = k.getF();
    int index = 0;
    double otherF;
    while (it.hasNext()) {
      otherF = it.next().getF();
      if (f >= otherF) {
        break;
      }
      index++;
    }
    openList.add(index, k);
  }
}
