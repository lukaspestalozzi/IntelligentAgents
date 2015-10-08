package template;

import java.util.HashMap;
import java.util.List;

import logist.plan.Action;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class ActionTableBuilder {
  private HashMap<State, Action> mTable;
  
  public ActionTableBuilder() {
    this.mTable = new HashMap<State, Action>();
  }
  
  /**
   * Generates a map mapping a state to the best action for the state.
   * @param discount
   * @return
   */
  public HashMap<State, Action> generateActionTable(List<City> cities, TaskDistribution td, double discount){
    TransitionTable tr = new TransitionTableBuilder().generateTable(cities, td);
    
    // TODO Q learning
    // TODO generate ActionTable
    throw new RuntimeException("Not yet implemented");
  }
  
  private void/* Q? */ Q_learning(/*...*/){
 // TODO
    throw new RuntimeException("Not yet implemented");
  }
}
