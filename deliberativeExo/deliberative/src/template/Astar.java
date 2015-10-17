package template;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public abstract class Astar<S> {
  
  private final S mInitialState;
  private final SearchNode<S> mRoot;
  /**
   * The set containing all discovered and finished nodes/states
   */
  private final HashMap<SearchNode<S>, SearchNode<S>> mClosed;
  private final LinkedList<SearchNode<S>> mOpenList;
  private final HashSet<SearchNode<S>> mOpenSet;
  
  public Astar(S start) {
    mInitialState = start;
    mClosed = new HashMap<SearchNode<S>, SearchNode<S>>();
    mOpenList = new LinkedList<SearchNode<S>>();
    mOpenSet = new HashSet<SearchNode<S>>();
    mRoot = new SearchNode<S>(mInitialState, "ROOT");
  }
  /**
   * 
   * @param s
   * @return true iff s is a goal state.
   */
  public abstract boolean isGoal(SearchNode<S> s);
  /**
   * 
   * @param s
   * @return a list of all child nodes of the state s.
   */
  public abstract List<SearchNode<S>> children(SearchNode<S> s);
  /**
   * 
   * @param s
   * @return the heuristic value of the state s
   */
  public abstract double heuristic(SearchNode<S> s);
  
  /**
   * insert the Node at the correct position in the openList.
   * @param k
   * @param openList
   */
  public abstract void insertOpen(SearchNode<S> k, LinkedList<SearchNode<S>> openList);
  
  /**
   * 
   * @param from
   * @param to
   * @return the cost to go from the 'from' node to 'to' node.
   */
  public abstract double cost(SearchNode<S> from, SearchNode<S> to);
  
  /**
   * searches from the root node until it finds a goal or all reachable nodes have been reached. 
   * @return A list of actions leading from the initial state to a goal state. null if none was found.
   */
  public final List<SearchNode<S>> search(){
    mRoot.setG(0.0);
    mRoot.setH(heuristic(mRoot));
    mOpenList.push(mRoot);
    mOpenSet.add(mRoot);
    
    boolean continueSearch = true;
    while(continueSearch){
      if(mOpenList.isEmpty()){
        return null;
      }
      SearchNode<S> x = mOpenList.pop();
      mClosed.put(x, x);
      if(isGoal(x)){
        continueSearch = false;
        return generatePath(x);
      }
      
      List<SearchNode<S>> kids = children(x);
      for(SearchNode<S> k : kids){
        if(mClosed.containsKey(k)){
          // prevents that multiple versions of the same node.
          k = mClosed.get(k);
        }
        x.addKid(k);
        if(!mOpenSet.contains(k) && !mClosed.containsKey(k)){
          // k is a newly discovered node.
          attachAndEval(k, x);
          insertOpen(k, mOpenList);
        }else if(x.getG() + cost(x, k) < k.getG()){
          // better path is found
          attachAndEval(k, x);
          if(mClosed.containsKey(k)){
            propagatePathImprovements(k);
          }
        }
      }
    }
    return null;
  }
  
  public final void attachAndEval(SearchNode<S> kid, SearchNode<S> parent){
    kid.setBestParent(parent);
    kid.setG(parent.getG() + cost(parent, kid));
    kid.setH(heuristic(kid));
  }
  
  public final void propagatePathImprovements(SearchNode<S> node){
    for(SearchNode<S> kid : node.getKids()){
      if(node.getG() + cost(node, kid) < kid.getG()){
        kid.setBestParent(node);
        kid.setG(node.getG() + cost(node, kid));
        propagatePathImprovements(kid);
      }
    }
  }
  
  public final List<SearchNode<S>> generatePath(SearchNode<S> node){
    LinkedList<SearchNode<S>> path = new LinkedList<SearchNode<S>>();
    
    path.add(node);
    SearchNode<S> parent = node.getBestParent();
    while(!parent.equals(mRoot)){
      path.push(parent);
      parent = parent.getBestParent();
    }
    path.push(mRoot);
    return path;
  }
  
  
}
