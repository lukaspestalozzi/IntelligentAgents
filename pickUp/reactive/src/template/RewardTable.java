package template;

import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class RewardTable {
  private TaskDistribution td;
  // private ActionGetter mActionGetter =  new ActionGetter();
  
  public RewardTable(TaskDistribution td) {
    this.td = td;
  }
  
  /**
   * 
   * @param s
   * @param a
   * @return
   */
  public long reward(State s, DPAction a/*, Vehicle v*/) { //TODO do without vehicle?
    City from = s.getCity();
    City to = a.getTo();
    assert(from.equals(a.getFrom()));
    // City to = a.accept(mActionGetter);
    
    return ((a.isMove()) ? 0 : td.reward(from, to)) - from.distanceUnitsTo(to)/**v.costPerKm()*/;
  }
}
