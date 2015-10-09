package template;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import logist.plan.Action;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class ActionTableBuilder {
  private RewardTable mRewardTable;
  private TransitionTable T;
  private List<City> mCities;
  
  public ActionTableBuilder(List<City> cities, TaskDistribution td) {
    mRewardTable = new RewardTable(td);
    this.mCities = cities;
    T = new TransitionTableBuilder(cities, td).generateTable();
  }
  
  /**
   * Generates a map mapping a state to the best DPAction for the state.
   * 
   * @param discount
   * @return
   */
  public HashMap<State, Action> generateActionTable(List<City> cities,
      TaskDistribution td, double discount) {
    TransitionTableBuilder trBuilder = new TransitionTableBuilder(cities, td);
    TransitionTable tr = trBuilder.generateTable();
    
    HashMap<State, Action> best = mapDpToAction(Q_learning(0.8)); // TODO put gamma into the xml file.
    
    
    return best;
  }
  
  private HashMap<State, Action> mapDpToAction(HashMap<State, DPAction> dpMap){
    HashMap<State, Action> map = new HashMap<State, Action>();
    for(Entry<State, DPAction> e : dpMap.entrySet()){
      // TODO
    }
    
  }
  
  private HashMap<State, DPAction> Q_learning(double gamma) {
    State[] states = State.generateAllStates(mCities);
    DPAction[] actions = new DPAction[0]; // TODO
    HashMap<State, Double> V = new HashMap<State, Double>(); // value of a state
    HashMap<State, DPAction> actionTable = new HashMap<State, DPAction>();
    
    // init V
    for (State s : states) {
      V.put(s, 0.0);
    }
    
    boolean goodEnough = false;
    while (!goodEnough) {
      for (int s = 0; s < states.length; s++) {
        State state = states[s];
        DPAction[] possibleActions = states[s].possibleActions(actions);
        
        int maxIndex = -1; // index of the best move for the state
        double maxValue = Integer.MIN_VALUE; // value of taking the best move
        for (int a = 0; a < possibleActions.length; a++) {
          DPAction action = actions[a];
          
          double sum = mRewardTable.reward(state, action); // sum = immediate reward of taking the DPAction
          for(State nextS : states){
            sum += T.getProbability(state, action, nextS)*V.get(nextS); // sum of all the possible DPAction in this state
          }
          double q = sum * gamma;
          
          // update maxValue & index if the DPAction is better
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
