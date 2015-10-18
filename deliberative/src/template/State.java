package template;

import java.util.Arrays;

import logist.plan.Action;
import logist.plan.ActionHandler;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.topology.Topology.City;
import uchicago.src.sim.space.Torus;

public class State{
  
  private final City mVehiclePosition;
  private final double mFreeLoad;
  private final Position[] mPackagePositions;
  
  /**
   * 
   * @param vehiclePosition
   * @param freeLoad
   * @param packagePositions
   */
  public State(City vehiclePosition, double freeLoad, Position[] packagePositions) {
    
    mFreeLoad = freeLoad;
    mVehiclePosition = vehiclePosition;
    
    // copy the position-array so that it can't be manipulate from outside.
    mPackagePositions = new Position[packagePositions.length];
    for (int i = 0; i < packagePositions.length; i++) {
      mPackagePositions[i] = packagePositions[i];
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
        .append(Arrays.toString(mPackagePositions))
        .append("]")
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
        // TODO does the city have to be a neighbor city?
        // nothing changes, except the vehicles position.
        return new State(city, mFreeLoad, mPackagePositions);
      }
      
      @Override
      public State pickup(Task task) {
        Position packagePosition = mPackagePositions[task.id];
        
        if (task.weight <= mFreeLoad && packagePosition.isWaiting()
            && ((Waiting) packagePosition).city.equals(task.pickupCity)
            && ((Waiting) packagePosition).city.equals(mVehiclePosition)) {
          
          // The task can be picked up in this state.
          return new State(mVehiclePosition, mFreeLoad - task.weight, copyPackagePositions(task.id, new InDelivery(vehicle)));
          
        }
        return null; // the task can NOT be picked up in this state.
        
      }
      
      @Override
      public State deliver(Task task) {
        Position packagePosition = mPackagePositions[task.id];
        
        if (packagePosition.isInDelivery()
            && mVehiclePosition.equals(task.deliveryCity)) {
          // && ((InDelivery)packagePosition).vehicle.equals(/* the vehicle
          // of the agent */)
          
          // The task can be delivered in this state.
          return new State(mVehiclePosition, mFreeLoad + task.weight, copyPackagePositions(task.id, new Delivered(mVehiclePosition)));
          
        }
        return null; // the task can NOT be delivered in this state.
      }
    });
  }
  
  /**
   * 
   * @return a copy of the position-array of this state.
   */
  public Position[] copyPositions() {
    Position[] pos = new Position[mPackagePositions.length];
    for (int i = 0; i < mPackagePositions.length; i++) {
      pos[i] = mPackagePositions[i];
    }
    return pos;
  }
  
  /**
   * 
   * @param index
   * @param newPos
   * @return a copy of the position-array of this state. With the element newPos at the given index.
   */
  public Position[] copyPackagePositions(int index, Position newPos) {
    Position[] pos = copyPositions();
    pos[index] = newPos;
    return pos;
  }
  
  public Position[] getPackagePositions() {
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
    result = prime * result + Arrays.hashCode(mPackagePositions);
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
    if (!Arrays.equals(mPackagePositions, other.mPackagePositions)) { return false; }
    if (mVehiclePosition == null) {
      if (other.mVehiclePosition != null) { return false; }
    } else if (!mVehiclePosition.equals(other.mVehiclePosition)) { return false; }
    return true;
  }
  
  
  
}
