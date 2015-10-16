package template;

import logist.plan.Action;
import logist.plan.ActionHandler;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.topology.Topology.City;

public class State {
  
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
        // TODO does the city have to be a neighbor city?
        // nothing
        // changes, except the vehicles position.
        return new State(city, mFreeLoad, mPackagePositions);
      }
      
      @Override
      public State pickup(Task task) {
        Position packagePosition = mPackagePositions[task.id];
        
        if (task.weight <= mFreeLoad && packagePosition.isWaiting()
            && ((Waiting) packagePosition).city.equals(task.pickupCity)
            && ((Waiting) packagePosition).city.equals(mVehiclePosition)) {
          
          // The task can be picked up in this state.
          return new State(mVehiclePosition, mFreeLoad - task.weight, copyPositions(task.id, new InDelivery(vehicle)));
          
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
          return new State(mVehiclePosition, mFreeLoad + task.weight, copyPositions(task.id, new Delivered(mVehiclePosition)));
          
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
  public Position[] copyPositions(int index, Position newPos) {
    Position[] pos = copyPositions();
    pos[index] = newPos;
    return pos;
  }
  
}
