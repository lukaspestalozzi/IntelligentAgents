package template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.javafx.binding.SelectBinding.AsInteger;

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
  
  /**
   * finds the action that is done before the given action
   * @param act
   * @return the previous action, or null if act is the first action (has no previous)
   */
  public Action findPrevious(Action act){
    Vehicle v = this.vehicles.get(act.task);
    Action candidate = this.firstAction.get(v);
    if(candidate.equals(act)){
      return null; // act is the first action of the vehicle
    }
    while(! candidate.equals(act)){
      candidate = this.nextAction.get(candidate);
      if(candidate == null){
        throw new IllegalStateException();
      }
    }
    
    return candidate;
  }
  
  /**
   * puts the pick and del actions at the beginning of the toV vehicles plan.
   * @param pick
   * @param del
   * @param a
   * @param fromV
   * @param toV
   * @return
   */
  private static Assignment changeVehicle(Pickup pick, Deliver del, Assignment a, Vehicle fromV, Vehicle toV){

    // store some actions
    Action oldFirstTo = a.firstAction.get(toV);
    Action oldNextPick = a.nextAction.get(pick);
    Action oldNextDel = a.nextAction.get(del);
    Action oldPrevPick = a.findPrevious(pick);
    Action oldPrevDel = a.findPrevious(del);
    
    // remove pick and del from the 'fromV'
    if(oldNextPick.equals(del)){
      // they are next to each other
      if(oldPrevPick == null){
        a.firstAction.put(fromV, oldNextDel);
      }else{
        a.nextAction.put(oldPrevPick, oldNextDel);
      }
    }else{
      // there is at least one action between pick and del
      if(oldPrevPick == null){
        a.firstAction.put(fromV, oldNextPick);
      }else{
        a.nextAction.put(oldPrevPick, oldNextPick);
      }
      a.nextAction.put(oldPrevDel, oldNextDel);
    }
    
    // put them at the beginning of 'toV'
    a.firstAction.put(toV, pick);
    a.nextAction.put(pick, del);
    a.nextAction.put(del, oldFirstTo);
    
    // update task -> vehicle
    a.vehicles.put(pick.task, toV);
    
    //update times
    updateTimes(a, fromV, toV);
    
    return a;
    
    
  }
  

  
  private static Assignment swapActions(Action a1, Action a2, Assignment ass){
    // TODO
  }
  
  /**
   * updates the times for the given vehicles plans
   * @param a
   * @param vs
   */
  private static void updateTimes(Assignment a, Vehicle... vs){
    for(Vehicle v : vs){
      long time = 1;
      Action act = a.firstAction.get(v);
      while(act != null){
        a.times.put(act, time++);
        act = a.nextAction.get(act);
      }
    }
  }
}
