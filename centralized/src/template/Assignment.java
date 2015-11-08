package template;

import java.util.List;
import java.util.Map;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;

public class Assignment {
  public final Map<Vehicle, Action> firstAction; // the first action of a vehicle
  public final Map<Action, Action> nextAction; 
  public final Map<Task, Vehicle> vehicles; // The vehicle that does the task
  public final Map<Action, Long> times; // The time when a task is done.

  public Assignment(Map<Vehicle, Action> firstAction,
      Map<Action, Action> nextAction, Map<Task, Vehicle> vehicles,
      Map<Action, Long> times) {
    this.firstAction = firstAction;
    this.nextAction = nextAction;
    this.vehicles = vehicles;
    this.times = times;
  }

  public List<Plan> generatePlans() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public Map<Vehicle, Action> getFirstAction() {
    return firstAction;
  }
  
  public Map<Action, Action> getNextAction() {
    return nextAction;
  }
  
  public Map<Action, Long> getTimes() {
    return times;
  }
  
  public Map<Task, Vehicle> getVehicles() {
    return vehicles;
  }
}
