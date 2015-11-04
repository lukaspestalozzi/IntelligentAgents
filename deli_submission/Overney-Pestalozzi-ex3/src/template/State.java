package template;

import java.util.HashMap;
import java.util.Map.Entry;

import logist.plan.Action;
import logist.plan.ActionHandler;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.topology.Topology.City;

public class State{
  
  private final City mVehiclePosition;
  private final double mFreeLoad;
  private final HashMap<Integer, Position> mPackagePositions; // maps taskid -> position of the package (of the task)
  
  /**
   * 
   * @param vehiclePosition
   * @param freeLoad
   * @param initialPositions
   */
  public State(City vehiclePosition, double freeLoad, HashMap<Integer, Position> initialPositions) {
    
    mFreeLoad = freeLoad;
    mVehiclePosition = vehiclePosition;
    
    // copy the position-map so that it can't be manipulate from outside.
    mPackagePositions = new HashMap<Integer, Position>(initialPositions.size());
    for(Entry<Integer, Position> e : initialPositions.entrySet()){
      mPackagePositions.put(e.getKey(), e.getValue());
    }
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("S[")
        .append("free: ")
        .append(mFreeLoad)
        .append(" pos: ")
        .append(mVehiclePosition.toString())
        .append(" packages: ")
        .append(mPackagePositions.toString())
        .append("]\n")
        .toString();
  }
  
  /**
   * 
   * @param action
   * @return the state resulting on taking the given action in this state. Null
   *         if the action is illegal in this state.
   */
  public State transition(final Action action, final Vehicle vehicle) {
    return action.accept(new ActionHandler<State>() {
      
      @Override
      public State moveTo(City city) {
        if(mVehiclePosition.equals(city)){
          return null;
        }
        if(!mVehiclePosition.hasNeighbor(city)){
          return null;
        }
        
        // nothing changes, except the vehicles position.
        return new State(city, mFreeLoad, mPackagePositions);
      }
      
      @Override
      public State pickup(Task task) {
        Position packagePosition = mPackagePositions.get(task.id);
        
        if (task.weight <= mFreeLoad && packagePosition.isWaiting()
            && ((Waiting) packagePosition).city.equals(task.pickupCity)
            && ((Waiting) packagePosition).city.equals(mVehiclePosition)) {
          
          // The task can be picked up in this state.
          return new State(mVehiclePosition, mFreeLoad - task.weight, copyPackagePositions(task.id, new InDelivery(vehicle, task.deliveryCity)));
          
        }
        return null; // the task can NOT be picked up in this state.
        
      }
      
      @Override
      public State deliver(Task task) {
        Position packagePosition = mPackagePositions.get(task.id);
        
        if (packagePosition.isInDelivery()
            && mVehiclePosition.equals(task.deliveryCity)) {
          
          
          // The task can be delivered in this state.
          return new State(mVehiclePosition, mFreeLoad + task.weight, copyPackagePositions(task.id, new Delivered(mVehiclePosition, task.deliveryCity)));
          
        }
        return null; // the task can NOT be delivered in this state.
      }
    });
  }
  
  /**
   * 
   * @return a copy of the position-map of this state.
   */
  public HashMap<Integer, Position> copyPositions() {
    HashMap<Integer, Position> pos = new HashMap<>(mPackagePositions.size());
    for (Entry<Integer, Position> e : mPackagePositions.entrySet()) {
      pos.put(e.getKey(), e.getValue());
    }
    return pos;
  }
  
  /**
   * 
   * @param index
   * @param newPos
   * @return a copy of the position-map of this state. With the element newPos at the given index.
   */
  public HashMap<Integer, Position> copyPackagePositions(int taskId, Position newPos) {
    HashMap<Integer, Position> pos = copyPositions();
    pos.put(taskId, newPos);
    return pos;
  }
  
  public HashMap<Integer, Position> getPackagePositions() {
    return mPackagePositions;
  }
  
  public City getVehiclePosition() {
    return mVehiclePosition;
  }
  
  public double getFreeLoad() {
    return mFreeLoad;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(mFreeLoad);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result
        + ((mPackagePositions == null) ? 0 : mPackagePositions.hashCode());
    result = prime * result
        + ((mVehiclePosition == null) ? 0 : mVehiclePosition.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (!(obj instanceof State)) { return false; }
    State other = (State) obj;
    if (Double.doubleToLongBits(mFreeLoad) != Double
        .doubleToLongBits(other.mFreeLoad)) { return false; }
    if (mPackagePositions == null) {
      if (other.mPackagePositions != null) { return false; }
    } else if (!mPackagePositions.equals(other.mPackagePositions)) { return false; }
    if (mVehiclePosition == null) {
      if (other.mVehiclePosition != null) { return false; }
    } else if (!mVehiclePosition.equals(other.mVehiclePosition)) { return false; }
    return true;
  }
}
