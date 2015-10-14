package template;

import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class TransitionTable {
  private final TaskDistribution mTd;

  public TransitionTable(TaskDistribution td) {
    mTd = td;
  }

  /**
   * 
   * @param s
   * @param action
   * @param nextState
   * @return the probability that the 'action' in state 's' leads to state
   *         'nextState'. Note that it can be 0 if the action is either illegal
   *         in s or there is no way it reaches the given next state.
   */
  public double getProbability(State s, DPAction action, State nextState) {
    // TODO can be made more efficient. (without the contains condition), but
    // then the mTable must be filled completely.
    if(s.isLegalAction(action)){
      
      if(nextState.hasTask()){
        return mTd.probability(nextState.getCity(),
            nextState.getTo());
      }else{
        return mTd.probability(nextState.getCity(), null);
      }
    }else{
      return 0.0;
    }
    
//    if (action.getTo().equals(nextState.getCity()) && action.isDelivery() && s.hasTask()) {
//      return nextState.hasTask() ? mTd.probability(nextState.getCity(),
//          nextState.getTo()) : mTd.probability(nextState.getCity(), null);
//    } else {
//      return 0.0;
//    }
  }
}