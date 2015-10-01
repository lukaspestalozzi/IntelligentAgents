package template;

import java.util.HashMap;

public class TransitionTable {
  private State[] mStates;
  private Action[] mActions;
  private HashMap<Tuple<State, Action>, Double> mTable;
  
  public TransitionTable(State[] s, Action[] a, HashMap<Tuple<State, Action>, Double> t) {
    mStates = s;
    mActions = a;
    mTable = t;
  }
  
  public double get(State s, Action a){
    return mTable.get(new Tuple<State, Action>(s, a));
  }

}


