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
    
    // Value of a state
    HashMap<State, Double> V = new HashMap<State, Double>();
    // best action for a state
    HashMap<State, DPAction> actionTable = new HashMap<State, DPAction>();
    
    // init V
    for (State s : states) {
      V.put(s, 1.0); // each state has initially the value 1. // TODO is there a
                     // better init value?
    }
    
    boolean goodEnough = false;
    boolean changeInV;
    while (!goodEnough) {
      
      changeInV = false;
      
      for (State state : states) {
        int maxIndex = -1; // index of the best move for the state
        double maxValue = Double.NEGATIVE_INFINITY;
        
        // loop over all actions
        for (int a = 0; a < actions.length; a++) {
          DPAction action = actions[a];
          if (state.isLegalAction(action)) {
            
            double sum = 0;
            for (State nextS : states) {
              sum += mT.getProbability(state, action, nextS) * V.get(nextS);
            }
            double immediateReward = mRewardTable.reward(state, action, vehicle);
            double q = (sum * gamma) + immediateReward;
            
            System.err.println(String.format("sum: %f\n"
                + "gamma: %f\n"
                + "sum*gamma: %f\n"
                + "immediate reward: %f\n"
                + "q: %f\n",
                sum, gamma, sum*gamma,immediateReward, q));
            
            
            // update maxValue & index if the action is better than any before.
            if (maxValue < q) {
              maxIndex = a;
              maxValue = q;
              System.out.println("updated max to: index:" + a + " val:" + q);
            }
          }
        }
        
        if (V.put(state, maxValue) != maxValue) {
          changeInV = true;
        }
        System.out.println();
        System.out.println("maxIndex: " + maxIndex + " maxVal: " + maxValue);
        actionTable.put(state, actions[maxIndex]);
      }
      goodEnough = !changeInV; // if V never changed, then the strategy is good
                               // enough.
    }
    
    return actionTable;
  }
}
