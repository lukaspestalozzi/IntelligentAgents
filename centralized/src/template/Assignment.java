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
    throw new RuntimeException("not yet implemented");
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
   * @param fromV
   * @param toV
   * @return
   */
  private void changeVehicle(Pickup pick, Deliver del, Vehicle fromV, Vehicle toV){

    // store some actions
    Action oldFirstTo = this.firstAction.get(toV);
    Action oldNextPick = this.nextAction.get(pick);
    Action oldNextDel = this.nextAction.get(del);
    Action oldPrevPick = this.findPrevious(pick);
    Action oldPrevDel = this.findPrevious(del);
    
    // remove pick and del from the 'fromV'
    if(oldNextPick.equals(del)){
      // they are next to each other
      if(oldPrevPick == null){
        this.firstAction.put(fromV, oldNextDel);
      }else{
        this.nextAction.put(oldPrevPick, oldNextDel);
      }
    }else{
      // there is at least one action between pick and del
      if(oldPrevPick == null){
        this.firstAction.put(fromV, oldNextPick);
      }else{
        this.nextAction.put(oldPrevPick, oldNextPick);
      }
      this.nextAction.put(oldPrevDel, oldNextDel);
    }
    
    // put them at the beginning of 'toV'
    this.firstAction.put(toV, pick);
    this.nextAction.put(pick, del);
    this.nextAction.put(del, oldFirstTo);
    
    // update task -> vehicle
    this.vehicles.put(pick.task, toV);
    
    //update times
    updateTimes(fromV, toV);
    
    
    
  }
  

  
  private void swapActions(Action a1, Action a2){
    if(a1.equals(a2)){
      throw new IllegalArgumentException("The actions must not be the same");
    }
    if(! this.vehicles.get(a1.task).equals(this.vehicles.get(a2.task)) ){
      throw new IllegalArgumentException("The actions must belong to the same vehicle");
    }
    
    // make sure a1 happens before a2
    if(this.times.get(a1) > this.times.get(a2)){
      swapActions(a2, a1);
      return;
    }
    
    // TODO if a1 is a pickup or a2 is a delivery, make sure the swap is legal.
    if(a1.isPickup() || a2.isDelivery()){
      // traverse all actions in between the two
      Action act = this.nextAction.get(a1);
      while(! act.equals(a2)){
        if((act.isDelivery() && act.task.equals(a1.task)) 
            || (act.isPickup() && act.task.equals(a2.task))){
          throw new IllegalArgumentException("The swap violates the pickup before delivery constraint");
        }
      }
    }
    
    Vehicle v = this.vehicles.get(a1.task);
    Action preva1 = this.findPrevious(a1);
    Action preva2 = this.findPrevious(a2);
    Action nexta1 = this.nextAction.get(a1);
    Action nexta2 = this.nextAction.get(a2);
    
    // swap 
    if(nexta1.equals(a2)){
      // they are next to each other
      if(preva1 == null){
        this.firstAction.put(v, a2);
      }else{
        this.nextAction.put(preva1, a2);
      }
      this.nextAction.put(a2, a1);
      this.nextAction.put(a1, nexta2);
      
    }else{
      // there is at least one in between
      if(preva1 == null){
        this.firstAction.put(v, a2);
      }else{
        this.nextAction.put(preva1, a2);
      }
      this.nextAction.put(a2, nexta1);
      this.nextAction.put(preva2, a1);
      this.nextAction.put(a1, nexta2);
    }
    
  }
  
  /**
   * updates the times for the given vehicles plans
   * @param a
   * @param vs
   */
  private void updateTimes(Vehicle... vs){
    for(Vehicle v : vs){
      long time = 1;
      Action act = this.firstAction.get(v);
      while(act != null){
        this.times.put(act, time++);
        act = this.nextAction.get(act);
      }
    }
  }
}
