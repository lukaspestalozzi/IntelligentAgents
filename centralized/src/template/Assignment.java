package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;

public class Assignment {
  // The first action of a vehicle
  public final Map<Vehicle, List<Action>> vehicleRoutes;
  // The vehicle that does the task
  public final Map<Task, Vehicle> vehicles;
  // The time when a task is done.
  public final Map<Action, Long> times;
  
  public Assignment(Map<Vehicle, List<Action>> vehicleRoutes, Map<Task, Vehicle> vehicles, Map<Action, Long> times) {
    this.vehicleRoutes = vehicleRoutes;
    this.vehicles = vehicles;
    this.times = times;
  }
  
  public List<Plan> generatePlans(List<Vehicle> vehics) {
    List<Plan> plans = new ArrayList<Plan>();
    
    for (Vehicle v : vehics) {
      Plan p = new Plan(v.getCurrentCity());
      if (!vehicleRoutes.get(v).isEmpty()) {
        // the vehicle has at least one action to do (actually two since it has
        // at least to pickup and deliver a package)
        for (Action nextA : vehicleRoutes.get(v)) {
          if (nextA.isDelivery()) {
            p.appendDelivery(nextA.task);
          } else if (nextA.isPickup()) {
            p.appendDelivery(nextA.task);
          } else {
            throw new RuntimeException("Should never happen");
          }
        }
      }
      plans.add(p);
    }
    return plans;
    
  }
  
  /**
   * 
   * @return a (deep) copy of this Assignment.
   */
  public ArrayList<Assignment> copy(int howMany) {
    ArrayList<Assignment> copies = new ArrayList<>(howMany);
    
    for (int i = 0; i < howMany; i++) {
      Map<Vehicle, List<Action>> vr = new HashMap<>();
      Map<Task, Vehicle> vahics = new HashMap<>();
      Map<Action, Long> tms = new HashMap<>();
      
      copies.add(new Assignment(vr, vahics, tms));
    }
    
    // fill the copies
    for (Entry<Vehicle, List<Action>> e : vehicleRoutes.entrySet()) {
      for (Assignment ass : copies) {
        ass.vehicleRoutes.put(e.getKey(), e.getValue());
      }
    }
    
    for (Entry<Action, Long> e : this.times.entrySet()) {
      for (Assignment ass : copies) {
        ass.times.put(e.getKey(), e.getValue());
      }
    }
    
    for (Entry<Task, Vehicle> e : this.vehicles.entrySet()) {
      for (Assignment ass : copies) {
        ass.vehicles.put(e.getKey(), e.getValue());
      }
    }
    
    return copies;
  }
  
  public ArrayList<Assignment> generateNeighbors() {
    // TODO improve
    // TODO ev make functions that deal with lists of actions and not with the maps direcly.
    
    // just to look if it works
    int nbrNabos = 10;
    ArrayList<Assignment> asses = this.copy(nbrNabos);
    Random r = new Random();
    for (int i = 0; i < nbrNabos; i++) {
      boolean b = firstAction.size() > 1 ? r.nextBoolean() : true;
      
      if (r.nextBoolean()) {
        // swap
        ArrayList<Vehicle> vlist = new ArrayList<Vehicle>(this.firstAction.keySet());
        Vehicle v = vlist.get(r.nextInt(vlist.size())); // get a random vehicle
        
        // find a vehicle where actions can be swapped
        while (getActionsForVehicle(v).size() < 4) {
          v = vlist.get(r.nextInt(vlist.size())); // get another random vehicle
        }
        
        List<Action> alist = vehicleRoutes.get(v);
        
        boolean swapped = false;
        while (!swapped) {
          // find two actions
          Action a1 = alist.get(r.nextInt(alist.size()));
          Action a2 = alist.get(r.nextInt(alist.size()));
          
          // try to swap until two compatible actions are found
          try {
            asses.get(i).swapActions(a1, a2);
            swapped = true;
          } catch (IllegalArgumentException ie) {
            // ignore
            ie.printStackTrace();
          }
        }
        
      } else {
        // change vehicle
        ArrayList<Action> alist = new ArrayList<Action>(this.nextAction.keySet());
        Task t = alist.get(r.nextInt(alist.size())).task; // get a random task
        boolean changed = false;
        while(! changed){
          ArrayList<Vehicle> vlist = new ArrayList<Vehicle>(this.firstAction.keySet());
          vlist.remove(this.vehicles.get(t)); // do not chose the same vehicle
                                              // again.
          Vehicle v = vlist.get(r.nextInt(vlist.size())); // get a random vehicle
          try{
          asses.get(i).changeVehicle(t, this.vehicles.get(t), v);
          changed = true;
          }catch(IllegalArgumentException ie){
            // ignore
            ie.printStackTrace();
          }
        }
      }
    }
    return asses;
  }
  
  /**
   * puts the pick and del actions at the beginning of the toV vehicles plan.
   * throws IllegalArgumentException if the change would violate some constraints.
   * @param pick
   * @param del
   * @param fromV
   * @param toV
   * 
   */
  private void changeVehicle(Task t, Vehicle fromV, Vehicle toV) {
    // input validation
    if(fromV.equals(toV)){
      throw new IllegalArgumentException("Vehicles must not be the same");
    }
    
    Pickup pick = new Pickup(t);
    Deliver del = new Deliver(t);
    
    // check if can fit into the toV vehicle
    List<Action> alist = vehicleRoutes.get(toV);
    alist.add(0, del);
    alist.add(0, pick);
    double freeLoad = toV.capacity();
    for(Action act : alist){
      if(act.isPickup()){
        freeLoad -= act.task.weight;
      }else{
        freeLoad += act.task.weight;
      }
      if(freeLoad < 0){
        throw new IllegalArgumentException("the change would violate the capacity of the toV vehicle");
      }
    }
    
    vehicleRoutes.get(toV).add(pick);
    vehicleRoutes.get(fromV).remove(pick);
    vehicleRoutes.get(toV).add(del);
    vehicleRoutes.get(fromV).remove(del);
    
    // update task -> vehicle
    this.vehicles.put(pick.task, toV);
    
    // update times
    updateTimes(fromV, toV);
    
  }
  
  /**
   * 
   * @param a1
   * @param a2
   */
  private void swapActions(Action a1, Action a2) {
    if (a1.equals(a2)) { throw new IllegalArgumentException(
        "The actions must not be the same"); }
        
    if (!this.vehicles.get(a1.task).equals(this.vehicles
        .get(a2.task))) { throw new IllegalArgumentException(
            "The actions must belong to the same vehicle"); }
            
    // make sure a1 happens before a2
    if (this.times.get(a1) > this.times.get(a2)) {
      swapActions(a2, a1);
      return;
    }
    
    // if a1 is a pickup or a2 is a delivery, make sure the swap is legal.
    if (a1.isPickup() || a2.isDelivery()) {
      // traverse all actions in between the two
      Action act = this.nextAction.get(a1);
      while (!act.equals(a2)) {
        if ((act.isDelivery() && act.task.equals(a1.task)) || (act.isPickup() && act.task
            .equals(a2.task))) { throw new IllegalArgumentException(
                "The swap violates the pickup before delivery constraint"); }
      }
    }
    
    Vehicle v = this.vehicles.get(a1.task);
    
    updateTimes(v);
    
  }
  
  /**
   * updates the times for the given vehicles plans
   * 
   * @param a
   * @param vs
   */
  private void updateTimes(Vehicle... vs) {
    for (Vehicle v : vs) {
      long time = 1;
      for(Action act : vehicleRoutes.get(v)) {
        this.times.put(act, time++);
      }
    }
  }
}
