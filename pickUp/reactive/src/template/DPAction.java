package template;

import java.util.HashSet;
import java.util.List;

import logist.topology.Topology.City;

public class DPAction {
  private final City from;
  private final City to;
  private final boolean isDelivery;
  
  

  public DPAction(City from, City to, boolean isDelivery) {
    this.from = from;
    this.to = to;
    this.isDelivery = isDelivery;
  }
  
  public static DPAction[] generateAllActions(List<City> cities) {
    HashSet<DPAction> actions = new HashSet<DPAction>();
    
    
    for (City c : cities) {
      // Move
      for (City n : c.neighbors()) {
        actions.add(new DPAction(c, n, false));
      }
      
      // Delivery
      for (City cs : cities) {
        if(! c.equals(cs)){
          actions.add(new DPAction(c, cs, true));
        }
      }
    }
    
    return actions.toArray(new DPAction[actions.size()]);
    
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + (isDelivery ? 1231 : 1237);
    result = prime * result + ((to == null) ? 0 : to.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    
    if (!(obj instanceof DPAction)) { return false; }
    DPAction other = (DPAction) obj;
    
    return this.from.equals(other.getFrom()) 
        && this.to.equals(other.getTo()) 
        && isDelivery == other.isDelivery;
  }
  
  public boolean isMove(){
    return ! this.isDelivery;
  }
  
  public City getFrom() {
    return from;
  }

  public City getTo() {
    return to;
  }

  public boolean isDelivery() {
    return isDelivery;
  }
}
