package template;

import java.util.HashMap;
import java.util.List;

import logist.plan.Action;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class TransitionTableBuilder {
  
  private HashMap<Triple<State, DPAction>, Double> mTable;
  private List<City> mCities;
  private TaskDistribution mTd;
  
  public TransitionTableBuilder(List<City> cities, TaskDistribution td) {
    mCities = cities;
    mTd = td;
    mTable = new HashMap<Triple<State, DPAction>, Double>();
  }
  
  public TransitionTable generateTable() {
    State[] states = State.generateAllStates(mCities);
    DPAction[] actions = DPAction.generateAllActions(mCities);
    
    for (State state : states) { // for all states
      // and all possible(legal) actions in the state
      for (DPAction action : state.possibleActions(actions)) {
        for (State nextState : states) { // and all (goal) states
          
          // if the next state is a potential goal state
          if (nextState.getCity().equals(action.getTo())) {
            // if the action is delivery then the nextState has to match the 'to' city of the state
            if ((action.isDelivery() && state.hasTask()
                && state.getTo().equals(nextState.getCity()))) {
                
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
                p = mTd.probability(nextState.getCity(), nextState.getTo());
              } else {
                p = mTd.probability(nextState.getCity(), null);
              } // TODO could just write p = td.probability(nextState.getCity(),
                // nextState.getTo()); since if a state has no task then 'to' is
                // null; But first check if it works like this!
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
  
  /**
   * throws an AssertionError if the boolean is false
   * 
   * @param b
   */
  private void assertT(boolean b) {
    if (!b) { throw new AssertionError(); }
  }
}
