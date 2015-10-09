package template;

import java.util.HashMap;

import logist.plan.Action;

public class TransitionTable {
  private final HashMap<Triple<State, DPAction>, Double> mTable;
  
  
  public TransitionTable(HashMap<Triple<State, DPAction>, Double> t) {
    mTable = t;
  }
  
  public double getProbability(State s, DPAction action, State nextState){
    return mTable.get(new Triple<State, DPAction>(s, action, nextState));
  }
}