package template;

import java.util.ArrayList;
import java.util.List;

import logist.topology.Topology.City;

/**
 * 
 * A vehicle in the City 'city' is in following state:</br>
 * if a task from the 'city' to the 'to' city exists, then the vehicle is in the
 * state (city, to, true).</br>
 * if no task exists then the vehicle is in the state (city, null, false).</br>
 *
 */
public class State {
  private final City mCity; // the city where the vehicle is right now
  private final City mTo; // the city where the task goes (if it exists)
  private final boolean mHasTask;
  private DPAction[] legalActions = null;
  private State[] possibleNextStates = null;
  
  public State(City c, City to, boolean hasTask) {
    if (c == null || (to == null && hasTask)) { throw new IllegalArgumentException(
        "'from' can not be null and 'to' can not be null if 'hasTask' is true"); }
    mCity = c;
    mTo = to;
    mHasTask = hasTask;
  }
  
  public boolean isLegalAction(DPAction a) {
    if (mHasTask && a.isDelivery()) { return true; }
    if (!mHasTask && a.isDelivery()) { return false; }
    DPMove ma = (DPMove) a;
    return ma.getFrom().equals(mCity) && mCity.hasNeighbor(ma.getTo());
  }
  
  public DPAction[] getLegalActions(DPAction[] allActions) {
    
    if (legalActions == null) {
      ArrayList<DPAction> all = new ArrayList<>();
      for (DPAction a : allActions) {
        if (isLegalAction(a)) {
          all.add(a);
        }
      }
      legalActions = all.toArray(new DPAction[all.size()]);
    }
    
    return legalActions;
    
  }
  
  public State[] getPossibleNextStates(State[] allStates){
    if (possibleNextStates == null) {
      ArrayList<State> all = new ArrayList<>();
      for (State s : allStates) {
        if (isPossibleNextState(s)) {
          all.add(s);
        }
      }
      possibleNextStates = all.toArray(new State[all.size()]);
    }
    
    return possibleNextStates;
  }
  
  private boolean isPossibleNextState(State s){
    
    return mHasTask ? mTo.equals(s.getCity()) : mCity.hasNeighbor(s.getCity());
    
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
    if (mHasTask != other.hasTask()) { return false; }
    if (!mCity.equals(other.getCity())) { return false; }
    if (mTo == null) {
      return other.getTo() == null;
    } else {
      return mTo.equals(other.getTo());
    }
  }
  
  public City getCity() {
    return mCity;
  }
  
  public City getTo() {
    return mTo;
  }
  
  public boolean hasTask() {
    return mHasTask;
  }
  
  @Override
  public String toString() {
    return new StringBuilder().append("S(").append(mCity.name)
        .append(mTo == null ? "" : "," + mTo.name).append(", ")
        .append(hasTask() ? "HasTask" : "NoTask").append(")").toString();
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
      states.add(new State(orig, null, false)); // has no task available
      
      for (City dest : cities) {
        if (!orig.equals(dest)) {
          states.add(new State(orig, dest, true)); // has the task from origin
                                                   // to destination
        }
      }
    }
    return states.toArray(new State[states.size()]);
  }
}
