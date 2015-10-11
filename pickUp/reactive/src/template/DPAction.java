package template;

import java.util.HashSet;
import java.util.List;

import logist.topology.Topology.City;

public class DPAction {
  private final City mFrom;
  private final City mTo;
  private final boolean mIsDelivery;
  
  

  public DPAction(City from, City to, boolean isDelivery) {
    if(from == null || to == null){
      throw new IllegalArgumentException("'from' and 'to' can not be null.");
    }
    this.mFrom = from;
    this.mTo = to;
    this.mIsDelivery = isDelivery;
  }
  
  public static DPAction[] generateAllActions(List<City> cities) {
    HashSet<DPAction> actions = new HashSet<DPAction>();
    
    for (City orig : cities) {
      // Move from origin to all neighbors
      for (City dest : orig.neighbors()) {
        actions.add(new DPAction(orig, dest, false));
      }
      
      // Delivery to all other cities
      for (City dest : cities) {
        if(! orig.equals(dest)){
          actions.add(new DPAction(orig, dest, true));
        }
      }
    }
    
    return actions.toArray(new DPAction[actions.size()]);
    
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mFrom == null) ? 0 : mFrom.hashCode());
    result = prime * result + (mIsDelivery ? 1231 : 1237);
    result = prime * result + ((mTo == null) ? 0 : mTo.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    
    if (!(obj instanceof DPAction)) { return false; }
    DPAction other = (DPAction) obj;
    
    return this.mFrom.equals(other.getFrom()) 
        && this.mTo.equals(other.getTo()) 
        && mIsDelivery == other.mIsDelivery;
  }
  
  public boolean isMove(){
    return ! mIsDelivery;
  }
  
  public City getFrom() {
    return mFrom;
  }

  public City getTo() {
    return mTo;
  }

  public boolean isDelivery() {
    return mIsDelivery;
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("A(")
        .append(mFrom.name)
        .append(", ")
        .append(mTo.name)
        .append(", ")
        .append(mIsDelivery ? "Delivery" : "Move")
        .append(")")
        .toString();
  }
}
