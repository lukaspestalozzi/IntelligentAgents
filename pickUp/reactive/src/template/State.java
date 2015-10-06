package template;

import logist.topology.Topology.City;

public class State {
  private final City mCity;
  private final boolean mHasTask;
  
  public State(City c, boolean hasTask) {
    mCity = c;
    mHasTask = hasTask;
  }
  
  public City getCity() {
    return mCity;
  }
  
  public boolean hasTask(){
    return mHasTask;
  }
}
