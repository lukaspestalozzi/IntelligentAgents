package template;

import java.util.HashMap;
import java.util.List;

import logist.plan.Action;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class TransitionTableBuilder {
  
  private HashMap<Triple<State, Action>, Double> mTable;
  
  public TransitionTableBuilder() {
  }
  
  public TransitionTable generateTable(List<City> cities, TaskDistribution td) {
    // TODO
    
    throw new RuntimeException("Not yet implemented");
  }
  
  private void addEntry(State s, Action a, State nextState, double proba) {
    mTable.put(new Triple<State, Action>(s, a, nextState), proba);
  }
  
  private Triple<State, Action>[] generateKeys() {
    // TODO
    throw new RuntimeException("Not yet implemented");
  }
  
  public Action[] generateActions(){
    // TODO
    throw new RuntimeException("Not yet implemented");
  }
  
  public State[] generateStates(List<City> cities) {
    // TODO
    throw new RuntimeException("Not yet implemented");
  }
}
