package template;

import logist.plan.Action;
import logist.plan.Action.Move;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class RewardTable {
  private TaskDistribution td;
  private ActionGetter mActionGetter;
  
  public long reward(State s, Action a) {
    City from = s.getCity();
    City to = a.accept(mActionGetter);
    
    return ((a instanceof Move) ? 0 : td.reward(from, to)) - from.distanceUnitsTo(to);
  }
}
