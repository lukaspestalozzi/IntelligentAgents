package template;

import java.util.HashMap;

import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.topology.Topology.City;

public class ActionTable {
  private HashMap<State, DPAction> best;
  
  public ActionTable(HashMap<State, DPAction> m){
    this.best = m;
  }
  
  /**
   * 
   * @param c the city the vehicle is in.
   * @param availableTask, may be null if no task is available
   * @return the best action if the vehicle is in city c with the given task,
   */
  public Action bestAction(City c, Task availableTask){
    if(availableTask == null){
      State s = new State(c, null, false);
      return new Move(best.get(s).getTo());
    }else{
      State s = new State(c, availableTask.deliveryCity, true);
      DPAction bestAction = best.get(s);
      if(bestAction.isMove()){
        return new Move(bestAction.getTo());
      }else{
        return new Pickup(availableTask);
      }
    }
  }
}
