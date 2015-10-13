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
    mT = new TransitionTableBuilder(cities, td).generateTable();
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
    
    HashMap<State, Double> V = new HashMap<State, Double>(); // 'value' of a state
    HashMap<State, DPAction> actionTable = new HashMap<State, DPAction>();
    
    // init V
    for (State s : states) {
      V.put(s, 1.0); // each state has initially the value 1.
    }
    
    boolean goodEnough = false;
    boolean changeInV;
    while (!goodEnough) {
       changeInV = false;
      for (State state : states) {
        System.out.println("state: "+state.toString());
        DPAction[] possibleActions = state.possibleActions(actions);
        System.out.println("possibleActions: "+Arrays.toString(possibleActions));
        
        if(possibleActions.length == 0){
          throw new RuntimeException("No action possible"); // TODO remove
        }
        
        int maxIndex = -1; // index of the best move for the state
        double maxValue = Double.NEGATIVE_INFINITY; // value of taking the best move
        
        for (int a = 0; a < possibleActions.length; a++) {
          DPAction action = possibleActions[a];
          System.out.println("actions: "+action.toString());
          
          double sum = mRewardTable.reward(state, action, vehicle); // sum = immediate reward of taking the action
          System.out.println("reward: "+sum);
          for(State nextS : states){
            sum += mT.getProbability(state, action, nextS)*V.get(nextS); // sum of all the possible actions in this state
          
          }
          double q = sum * gamma;
          
          System.out.println("q: "+q+" val:"+maxValue+"->"+(maxValue < q));
          
          // update maxValue & index if the action is better than any before.
          if(maxValue < q){
            //System.out.println(String.format("q: %f, a: %d, maxindex: %d, maxVal: %f", q, a, maxIndex, maxValue));
            maxIndex = a;
            maxValue = q;
            System.out.println("updated max to: index:"+a+" val:"+q);
          }
        }
        if(V.put(state, maxValue) != maxValue){
          changeInV = true;
        }
        System.out.println();
        System.out.println("maxIndex: "+maxIndex+" maxVal: "+maxValue);
        System.out.println("possible: "+possibleActions.length);
        actionTable.put(state, possibleActions[maxIndex]);
      }
      goodEnough = !changeInV; // if V never changed, then the strategy is good enough.
    }
    
    return actionTable;
  }
}
