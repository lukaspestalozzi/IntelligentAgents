package template;

import java.util.HashMap;
import java.util.List;

import cern.colt.Arrays;
import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class ActionTableBuilder {
  private RewardTable mRewardTable;
  private TransitionTable mT;
  private List<City> mCities;
  
  public ActionTableBuilder(List<City> cities, TaskDistribution td) {
    mRewardTable = new RewardTable(td);
    mCities = cities;
    mT = new TransitionTable(td);
  }
  
  /**
   * Generates a optimal transition table on the given map (cities).
   * 
   * @param discount
   * @return
   */
  public ActionTable generateActionTable(double gamma, Vehicle vehicle) {
    return new ActionTable(Q_learning(gamma, vehicle));
  }
  
  private HashMap<State, DPAction> Q_learning(double gamma, Vehicle vehicle) {
    State[] states = State.generateAllStates(mCities);
    DPAction[] actions = DPAction.generateAllActions(mCities);
    
    System.out.println("States: "+Arrays.toString(states));
    System.out.println();
    System.out.println("Actions"+Arrays.toString(actions));
    
    // Value of a state
    HashMap<State, Double> V = new HashMap<State, Double>();
    // best action for a state
    HashMap<State, DPAction> actionTable = new HashMap<State, DPAction>();
    
    // init V
    for (State s : states) {
      V.put(s, 0.0); // each state has initially the value 1.
    }
    
    boolean goodEnough = false;
    boolean changeInV;
    
    while (!goodEnough) {
      changeInV = false;
      
      for (State state : states) {
        System.out.println("State: " + state.toString());
        int maxIndex = -1; // index of the best action for the state
        double maxValue = Double.NEGATIVE_INFINITY;
        DPAction[] possibleActions = state.getLegalActions(actions);
        
        // loop over all actions
        for (int a = 0; a < possibleActions.length; a++) {
          DPAction action = possibleActions[a];
          if (!state.isLegalAction(action)) {
            continue;
          }
          
          double sum = 0;
          for (State nextS : states) {
            double proba = mT.getProbability(state, action, nextS);
            if (proba == 0) {
              continue;
            }
            double valNextS = V.get(nextS);
            double toAdd = proba * valNextS;
            sum += toAdd;
          }
          
          double immediateReward = mRewardTable.reward(state, action, vehicle);
          double q = (sum * gamma) + immediateReward;
          
          System.out
              .println(String.format(
                  "sum: %f\n" + "gamma: %f\n" + "sum*gamma: %f\n"
                      + "immediate reward: %f\n" + "q: %f\n",
                  sum, gamma, sum * gamma, immediateReward, q));
                  
          // update maxValue & index if the action is better than any before.
          if (maxValue < q) {
            maxIndex = a;
            maxValue = q;
          }
        }
        
        System.out.println("State: " + state + " has new val: " + maxValue);
        System.out.println("==========\n");
        
        if (V.put(state, maxValue) != maxValue) {
          changeInV = true;
        }
        actionTable.put(state, possibleActions[maxIndex]);
      }
      
      goodEnough = !changeInV; 
      // if V never changed, then the strategy is good enough.
    }
    
    return actionTable;
  }
}
