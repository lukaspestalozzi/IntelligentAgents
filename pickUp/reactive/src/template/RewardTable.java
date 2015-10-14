package template;

import logist.Measures;
import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;

public class RewardTable {
  private TaskDistribution mTd;
  
  public RewardTable(TaskDistribution td) {
    this.mTd = td;
  }
  
  /**
   * 
   * @param s
   * @param a
   * @return
   */
  public double reward(State s, DPAction a, Vehicle v) {
    City from = s.getCity();
    City to = a.isDelivery() ? s.getTo() : ((DPMove)a).getTo();
    
    return ((a.isMove()) ? 0 : mTd.reward(from, to)) - (Measures.unitsToKM(from.distanceUnitsTo(to))*v.costPerKm());
  }
}
