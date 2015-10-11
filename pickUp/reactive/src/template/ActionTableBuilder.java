package template;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import logist.plan.Action;
import logist.plan.Action.Delivery;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class ActionTableBuilder {
  private RewardTable mRewardTable;
  private TransitionTable mT;
  private List<City> mCities;
  
  public ActionTableBuilder(List<City> cities, TaskDistribution td) {
    mRewardTable = new RewardTable(td);
    mCities = cities;
    mT = new TransitionTableBuilder(cities, td).generateTable();
  }
  
  /**
   * Generates a optimal transition table on the given map (cities).
   * 
   * @param discount
   * @return
   */
  public ActionTable generateActionTable(double gamma) {
    
    return new ActionTable(Q_learning(gamma)); 
  }
  
  private HashMap<State, DPAction> Q_learning(double gamma) {
    State[] states = State.generateAllStates(mCities);
    DPAction[] actions = DPAction.generateAllActions(mCities);
    HashMap<State, Double> V = new HashMap<State, Double>(); // 'value' of a state
    HashMap<State, DPAction> actionTable = new HashMap<State, DPAction>();
    
    // init V
    for (State s : states) {
      V.put(s, 1.0); // each state has initially the value 1.
    }
    
    boolean goodEnough = false;
    
    while (!goodEnough) {
      for (State state : states) {
        DPAction[] possibleActions = state.possibleActions(actions);
        
        int maxIndex = -1; // index of the best move for the state
        double maxValue = Double.MIN_VALUE; // value of taking the best move
        
        for (int a = 0; a < possibleActions.length; a++) {
          DPAction action = actions[a];
          
          double sum = mRewardTable.reward(state, action); // sum = immediate reward of taking the action
          for(State nextS : states){
            sum += mT.getProbability(state, action, nextS)*V.get(nextS); // sum of all the possible actions in this state
          }
          double q = sum * gamma;
          
          // update maxValue & index if the action is better than any before.
          if(maxValue < q){
            maxIndex = a;
            maxValue = q;
          }
        }
        V.put(state, maxValue);
        actionTable.put(state, possibleActions[maxIndex]);
      }
    }
    
    return actionTable;
  }
}
