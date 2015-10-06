package template;

import java.util.HashMap;

import logist.plan.Action;

public class TransitionTable {
  private final HashMap<Triple<State, Action>, Double> mTable;
  
  
  public TransitionTable(HashMap<Triple<State, Action>, Double> t) {
    mTable = t;
  }
  
  public double getProbability(State s, Action a, State nextState){
    return mTable.get(new Triple<State, Action>(s, a, nextState));
  }
}