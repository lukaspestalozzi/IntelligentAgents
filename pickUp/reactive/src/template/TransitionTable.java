package template;

import java.util.HashMap;


public class TransitionTable {
  private final HashMap<Triple<State, DPAction>, Double> mTable;
  
  
  public TransitionTable(HashMap<Triple<State, DPAction>, Double> t) {
    mTable = t;
  }
  
  /**
   * 
   * @param s
   * @param action
   * @param nextState
   * @return the probability that the 'action' in state 's' leads to state 'nextState'. 
   * Note that it can be 0 if the action is either illegal in s or there is no way it reaches the given next state.
   */
  public double getProbability(State s, DPAction action, State nextState){
    // TODO can be made more efficient. (without the contains condition), but then the mTable must be filled completely.
    Triple<State, DPAction> t = new Triple<State, DPAction>(s, action, nextState);
    return mTable.containsKey(t) ? mTable.get(t) : 0.0;
  }
}