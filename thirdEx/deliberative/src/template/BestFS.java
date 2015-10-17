package template;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class BestFS<S extends Comparable<S>> extends Astar<S> {

  public BestFS(S start) {
    super(start);
  }

  @Override
  public void insertOpen(SearchNode<S> k, LinkedList<SearchNode<S>> openList) {
    Iterator<SearchNode<S>> it = openList.iterator();
    S kstate = k.getState();
    int index = 0;
    S state;
    while(it.hasNext()){
      state = it.next().getState();
      if(kstate.compareTo(state) >= 0){
        break;
      }
      index++;
    }
    openList.add(index, k);
  }
}
