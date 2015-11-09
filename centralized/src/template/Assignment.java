package template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import constraints.Constraint;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;

public class Assignment {
  // The first action of a vehicle
  public final Map<Vehicle, Action> firstAction;
  public final Map<Action, Action> nextAction;
  // The vehicle that does the task
  public final Map<Task, Vehicle> vehicles;
  // The time when a task is done.
  public final Map<Action, Long> times;

  public Assignment(Map<Vehicle, Action> firstAction,
      Map<Action, Action> nextAction, Map<Task, Vehicle> vehicles,
      Map<Action, Long> times) {
    this.firstAction = firstAction;
    this.nextAction = nextAction;
    this.vehicles = vehicles;
    this.times = times;
  }

  public List<Plan> generatePlans(List<Vehicle> vehics) {
    List<Plan> plans = new ArrayList<Plan>();
    
    for(Vehicle v : vehics){
      Plan p = new Plan(v.getCurrentCity());
      if(firstAction.get(v) != null){
        // the vehicle has at least one action to do (actually two since it has at least to pickup and deliver a package)
        Action nextA = firstAction.get(v);
        while(nextA != null){
          if(nextA.isDelivery()){
            p.appendDelivery(nextA.task);
          }else if(nextA.isPickup()){
            p.appendDelivery(nextA.task);
          }else{
            throw new RuntimeException("Should never happen");
          }
        }
      }
      plans.add(p);
    }
    return plans;
    
  }
  
  public ArrayList<Assignment> generateNeighbors(){
    // TODO
    
  }
  
  private static Assignment changeVehicle(Pickup pick, Deliver del, Assignment a, Vehicle from, Vehicle to){
    // TODO
  }
  
  private static Assignment swapActions(Action a1, Action a2, Assignment ass){
    // TODO
  }
  
}
