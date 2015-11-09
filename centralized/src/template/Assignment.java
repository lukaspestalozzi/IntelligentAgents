package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;

public class Assignment {
  // The route each vehicle takes
  public final Map<Vehicle, List<Action>> vehicleRoutes;
  // The vehicle that does the task
  public final Map<Task, Vehicle> vehicles;
  // the index of a Action in a route, the same as the times array in the
  // project description
  public final Map<Action, Integer> indexOf;
  
  private final Vehicle[] mAllVehics;
  
  private Random rand = new Random(/*2015*/);
  
  public Assignment(Map<Vehicle, List<Action>> vehicleRoutes, Map<Task, Vehicle> vehicles,
                    Map<Action, Integer> indexOf) {
    this.vehicleRoutes = vehicleRoutes;
    this.vehicles = vehicles;
    this.indexOf = indexOf;
    mAllVehics = vehicleRoutes.keySet().toArray(new Vehicle[vehicleRoutes.keySet().size()]);
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
   * @param howMany
   *          defines how many copies should be made.
   * @return a list of (deep) copys of this Assignment. (length of the list =
   *         howMany)
   */
  public ArrayList<Assignment> copy(int howMany) {
    ArrayList<Assignment> copies = new ArrayList<>(howMany);
    
    for (int i = 0; i < howMany; i++) {
      Map<Vehicle, List<Action>> vr = new HashMap<>();
      Map<Task, Vehicle> vehics = new HashMap<>();
      Map<Action, Integer> idxOf = new HashMap<>();
      
      copies.add(new Assignment(vr, vehics, idxOf));
    }
    
    // fill the copies
    // vehicleRoutes
    for (Entry<Vehicle, List<Action>> e : vehicleRoutes.entrySet()) {
      Vehicle v = e.getKey();
      List<Action> actions = e.getValue();
      // copy the list into all copies
      for (Assignment ass : copies) {
        ass.vehicleRoutes.put(v, new LinkedList<Action>());
        ass.vehicleRoutes.get(v).addAll(actions);
      }
    }
    
    // idxOf
    for (Entry<Action, Integer> e : this.indexOf.entrySet()) {
      for (Assignment ass : copies) {
        ass.indexOf.put(e.getKey(), e.getValue());
      }
    }
    
    // vehicles
    for (Entry<Task, Vehicle> e : this.vehicles.entrySet()) {
      for (Assignment ass : copies) {
        ass.vehicles.put(e.getKey(), e.getValue());
      }
    }
    
    return copies;
  }
  
  public ArrayList<Assignment> generateNeighbors() {
    // make move and one vehicle swap per vehicle
    int maxNabos = this.vehicleRoutes.size()*2;
    ArrayList<Assignment> nabos = new ArrayList<>(maxNabos);
    ArrayList<Assignment> copies = this.copy(maxNabos);
    
    // moves
    for(List<Action> route : this.vehicleRoutes.values()){
      if(route.isEmpty()){
        continue;
      }
      Assignment a = copies.remove(copies.size() - 1);
      int maxTries = route.size()*4;
      boolean moved = false;
      while((! moved) && --maxTries >= 0){ // try until legal move found (or max tries)
        Action act = randomAction(route);
        moved = a.moveAction(act, rand.nextBoolean());
      }
      if(moved){
        nabos.add(a);
      }
      
    }
    
    // vehicle change
    if(mAllVehics.length > 1){
      for(Vehicle v : this.vehicles.values()){
        if(v == null){
          throw new IllegalStateException("a value in the vehicles map can not be null");
        }
        Assignment a = copies.remove(copies.size() - 1);
        int maxTries = mAllVehics.length*2;
        boolean changed = false;
        while((! changed) && --maxTries >= 0){ // try until legal move found (or max tries)
          Vehicle v2 = randomVehicle(v);
          a.changeVehicle(v, v2);
        }
        if(changed){
          nabos.add(a);
        }
      }
    }
    return nabos;
    
  }
  
  /**
   * 
   * @param route
   * @return a random action in the route
   */
  private Action randomAction(List<Action> route){
    return route.get(rand.nextInt(route.size()));
  }
  
  /**
   * 
   * @param notV
   * @return a random vehicle not equals to notV
   */
  private Vehicle randomVehicle(Vehicle notV){
    Vehicle v = null;
    while(notV.equals(v)){
      v = mAllVehics[rand.nextInt(mAllVehics.length)];
    }
    return v;
  }
  
  /**
   * removes the first task from the 'fromV' route and puts it at the end of the
   * 'toV' route.
   * 
   * @param fromV
   * @param toV
   * @return true iff the change is legal, false otherwise. Does not change the calling object if false is returned.
   */
  private boolean changeVehicle(Vehicle fromV, Vehicle toV) {
    // input validation
    // the vehicles must be different
    if (fromV.equals(toV)) { return false;}
        
    // there must be a task
    if (this.vehicleRoutes.get(fromV).isEmpty()) { return false; }
        
    // find the pickup and deliver of the first task
    Action firstA = this.vehicleRoutes.get(fromV).get(0);
    if (firstA.isDelivery()) { throw new IllegalStateException(
        "The first Action must not be a delivery"); }
    Pickup pick = (Pickup) firstA;
    Deliver del = new Deliver(pick.task);
    
    // the task must fit in the toV
    if (toV.capacity() < pick.task.weight) { throw new IllegalStateException(
        "The task is to big for vehicle toV!"); }
        
    // remove the task from fromV route TODO ev use iterator for efficiency
    boolean rem = vehicleRoutes.get(fromV).remove(del);
    rem = rem & vehicleRoutes.get(fromV).remove(pick);
    // sanity check ('rem' must be true for both or else they were not in the
    // route of fromV).
    if (!rem) { throw new IllegalStateException(
        "The routes are inconsistent with the vehicles map"); }
        
    // put at the end of toV route
    vehicleRoutes.get(toV).add(pick);
    vehicleRoutes.get(toV).add(del);
    
    // update task -> vehicle
    this.vehicles.put(pick.task, toV);
    
    // update times of the two lists TODO make more efficient
    updateIndexes(fromV, toV);
    return true;
  }
  
  /**
   * Moves the action in its route by one index in the given direction. Note
   * that the first element always is moved back and the last element moved
   * towards the front.
   * 
   * @param act
   * @param moveRight
   *          true -> move back in list (index + 1) false -> move towards the
   *          front (index - 1)
   *          
   * @return true iff the change is legal, false otherwise. Does not change the calling object if false is returned.
   */
  private boolean moveAction(Action act, boolean moveRight) {
    int index = this.indexOf.get(act);
    List<Action> route = this.vehicleRoutes.get(this.vehicles.get(act.task));
    
    // sanity checks
    if (route.isEmpty()) { throw new IllegalStateException(
        "the vehicles map is inconsistent with the task (the task is not in the vehicles route)!"); }
    if (route.size() % 2 != 0) { throw new IllegalStateException(
        "A route must have a even size (always one pickup and delivery)"); }
        
    // set direction for first or last element
    if (index == 0) {
      moveRight = true;
    } else if (index == route.size() - 1) {
      moveRight = false;
    }
    
    // move the action (resp. swap the action with the previous or next one).
    ListIterator<Action> it = route.listIterator(index);
    if (!it.next().equals(act)) { // just to be sure
      throw new IllegalStateException("The indexOf map is inconistent with the route! ");
    }
    
    it.remove(); // removes act from the list
    Action swappedA;
    if (moveRight) {
      swappedA = it.next(); // goes to the next action
    } else {
      swappedA = it.previous(); // goes one back
    }
    
    // check the constraints
    boolean cviolated = false;
    if (swappedA.task.equals(act.task)) {
      cviolated = true;
    }else if((moveRight && act.isDelivery() && swappedA.isPickup())
      || (!moveRight && act.isPickup() && swappedA.isDelivery())){
      // a pickup is moved before delivery -> may overload the vehicle
      cviolated = !Constraints.checkVehicleOverloadConstraint(this, this.vehicles.get(act.task));
    }
    
    if(cviolated){
      // put act back and return false
      if (moveRight) {
        it.previous();
      } else {
        it.next();
      }
      it.add(act);
      return false;
    }
    
    it.add(act); // insert the act (after next or before previous)
    
    
    // update the indexes
    this.indexOf.put(swappedA, index);
    if (moveRight) {
      this.indexOf.put(act, index + 1);
    } else {
      this.indexOf.put(act, index - 1);
    }
    return true;
  }
  
  /**
   * updates the times for the given vehicles plans (Times is 0 indexed, so the
   * first action happens on time 0)
   * 
   * @param a
   * @param vs
   */
  private void updateIndexes(Vehicle... vs) {
    for (Vehicle v : vs) {
      int time = 0;
      for (Action act : vehicleRoutes.get(v)) {
        this.indexOf.put(act, time++);
      }
    }
  }
}
