package template;

import java.util.ArrayList;
import java.util.List;

import logist.topology.Topology.City;

public class State {
  private final City mCity; // the city where the vehicle is right now
  private final City mTo;
  private final boolean mHasTask;
  
  public State(City from, City to, boolean hasTask) {
    if(from == null || (to == null && hasTask)){
      throw new IllegalArgumentException("'from' can not be null and 'to' can not be null if 'hasTask' is true");
    }
    mCity = from;
    mTo = to;
    mHasTask = hasTask;
  }
  
  public City getCity() {
    return mCity;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mCity == null) ? 0 : mCity.hashCode());
    result = prime * result + (mHasTask ? 1231 : 1237);
    result = prime * result + ((mTo == null) ? 0 : mTo.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (!(obj instanceof State)) { return false; }
    State other = (State) obj;
    return mHasTask == other.hasTask() && mCity.equals(other.getCity())
        && mTo.equals(other.getTo());
  }
  
  public City getTo() {
    return mTo;
  }
  
  public boolean hasTask() {
    return mHasTask;
  }
  
  /**
   * generates all states one for each pair of cities (the task is available)
   * and one where no task is available.
   * 
   * @param cities
   * @return
   */
  public static State[] generateAllStates(List<City> cities) {
    ArrayList<State> states = new ArrayList<State>(cities.size() * cities.size());
    for (City orig : cities) {
      for (City dest : cities) {
        if (!orig.equals(dest)) {
          states.add(new State(orig, dest, true)); // has the task from origin
                                                   // to destination
          states.add(new State(orig, null, false)); // has no task available
        }
      }
    }
    return states.toArray(new State[states.size()]);
  }
  
  /**
   * 
   * @param allActions
   * @return all actions that can be taken from the given state
   */
  public DPAction[] possibleActions(DPAction[] allActions) {
    ArrayList<DPAction> possible = new ArrayList<>();
    
    for (DPAction a : allActions) {
      if (mCity.equals(a.getFrom())) {
        if(mHasTask && a.isDelivery() && mTo.equals(a.getTo())){ // the only delivery action that can be taken
          possible.add(a);
        }else if(!mHasTask && a.isMove() && mCity.hasNeighbor(a.getTo())){ // the move actions
          possible.add(a);
        }
      }
    }
    
    return possible.toArray(new DPAction[possible.size()]);
  }
}
