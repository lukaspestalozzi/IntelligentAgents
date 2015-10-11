package template;

import java.util.HashMap;
import java.util.List;

import logist.plan.Action;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class TransitionTableBuilder {
  
  private HashMap<Triple<State, DPAction>, Double> mTable;
  private List<City> cities;
  private TaskDistribution td;
  
  public TransitionTableBuilder(List<City> cities, TaskDistribution td) {
    this.cities = cities;
    this.td = td;
  }
  
  private void assertT(boolean b) {
    if (!b) { throw new AssertionError(); }
  }
  
  public TransitionTable generateTable() {
    State[] states = State.generateAllStates(cities);
    DPAction[] actions = DPAction.generateAllActions(cities);
    
    for (State state : states) { // for all states
      for (DPAction action : state.possibleActions(actions)) { // and all
                                                               // actions
                                                               // possible in
                                                               // the state
        for (State nextState : states) { // and all (goal) states
          if (nextState.getCity().equals(action.getTo())) { // if the next state is a potential goal state
            if ((action.isDelivery() && state.hasTask()
                    && state.getTo().equals(nextState.getCity()))) { // if the action is delivery then the next state has to match the 'to' state of the state
                    
              // sanity checks
              assertT(state.getCity().equals(action.getFrom()));
              assertT(
                  state.getTo() != null || (state.getTo() == null && action.isMove()));
              assertT(nextState.getCity().equals(action.getTo()));
              assertT(action.isDelivery() || (action.isMove()
                  && state.getCity().hasNeighbor(nextState.getCity())));
              assertT(action.getTo().equals(nextState.getCity()));
              
              Triple<State, DPAction> t = new Triple<State, DPAction>(state, action,
                  nextState);
              
              // add the probability
              double p;
              if (nextState.hasTask()) {
                p = td.probability(nextState.getCity(), nextState.getTo());
              } else {
                p = td.probability(nextState.getCity(), null);
              } // TODO could just write p = td.probability(nextState.getCity(),
                // nextState.getTo()); since if a state has no task then 'to' is
                // null;
              mTable.put(t, p);
            }
          }
        }
      }
    }
    return new TransitionTable(mTable);
  }
  
  private void addEntry(State s, DPAction a, State nextState, double proba) {
    mTable.put(new Triple<State, DPAction>(s, a, nextState), proba);
  }
}
