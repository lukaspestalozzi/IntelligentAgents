package planning;

import java.util.List;

import logist.simulation.Vehicle;
import logist.task.Task;

public class PlanFinder {
  private List<Task> mTasks;
  private List<Vehicle> mVehicles;
  
  public PlanFinder(List<Vehicle> vehicles) {
    mTasks = null;
    mVehicles = vehicles;
  }
  
  
  public Assignment computeBestPlan(List<Task> tasks) {
    mTasks = tasks;
    // TODO
    
    return null;
  }
}
