package template;

import logist.topology.Topology.City;

public class State {
  private City mCurrent;
  private City mDestination;
  private Boolean mIsFull;
  //TODO Determine exactly what's needed to represent a state
  
  public State (City current, Boolean isFull, City destination) {
   mCurrent = current;
   mDestination = destination;
  }
  
  public City getCurrentCity() {
    return mCurrent;
  }
  
  public City getDestination() {
    return mDestination;
  }
  
  public Boolean isFull() {
    return mIsFull;
  }
}
