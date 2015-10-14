package template;

import java.util.HashSet;
import java.util.List;

import logist.topology.Topology.City;

public abstract class DPAction {
  
  
  public static DPAction[] generateAllActions(List<City> cities) {
    HashSet<DPAction> actions = new HashSet<DPAction>();
    
    for (City orig : cities) {
      // Move from origin to all neighbors
      for (City dest : orig.neighbors()) {
        actions.add(new DPMove(orig, dest));
      }
    }
    actions.add(new DPDelivery());
    return actions.toArray(new DPAction[actions.size()]);
  }

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object obj);
  
  public abstract boolean isMove();

  public abstract boolean isDelivery();
  
  @Override
  public abstract String toString();
}
