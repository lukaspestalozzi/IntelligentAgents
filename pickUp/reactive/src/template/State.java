package template;

import java.util.ArrayList;
import java.util.List;

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
  
  public static State[] generateAllStates(List<City> cities){
    ArrayList<State> states = new ArrayList<State>(cities.size()*2);
    for(City c  : cities){
      states.add(new State(c, true));
      states.add(new State(c, false));
    }
    return states.toArray(new State[states.size()]);
  }
  
  public DPAction[] possibleActions(DPAction[] allActions){
    // TODO
    throw new RuntimeException("Not yet implemented");
  }
}
