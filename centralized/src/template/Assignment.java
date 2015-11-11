package template;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.topology.Topology.City;

public class Assignment {
  // The route each vehicle takes
  public final Map<Vehicle, List<Action>> vehicleRoutes;
  // The vehicle that does the task
  public final Map<Task, Vehicle> vehicles;
  // the index of a Action in a route, the same as the times array in the
  // project description
  public final Map<Action, Integer> indexOf;
  
  private boolean mNotCorrupt = true; // a flag to thell if this assigment is corrupt (true -> does not violate any constraint) 
  
  
  private Random rand = new Random(/* 2015 */);
  
  public Assignment(Map<Vehicle, List<Action>> vehicleRoutes, Map<Task, Vehicle> vehicles,
                    Map<Action, Integer> indexOf) {
    this.vehicleRoutes = vehicleRoutes;
    this.vehicles = vehicles;
    this.indexOf = indexOf;
  }
  
  public List<Plan> generatePlans(List<Vehicle> vehics) {
    List<Plan> plans = new ArrayList<Plan>();
    
    // System.out.println("raw Plans:");
    // for(List<Action> l : vehicleRoutes.values()){
    // System.out.println(l.toString());
    // System.out.println();
    // }
    
    for (Vehicle v : vehics) {
      Plan p = new Plan(v.getCurrentCity());
      if (!vehicleRoutes.get(v).isEmpty()) {
        // the vehicle has at least one action to do (actually two since it has
        // at least to pickup and deliver a package)
        City currentCity = v.getCurrentCity();
        for (Action nextA : vehicleRoutes.get(v)) {
          for(City miniGoal : currentCity.pathTo(nextA.actionCity)) {
            p.appendMove(miniGoal);
          }
          currentCity = nextA.actionCity;
        
          if (nextA.isDelivery()) {
            p.appendDelivery(nextA.task);
          } else if (nextA.isPickup()) {
            p.appendPickup(nextA.task);
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
  public TreeSet<Assignment> generateNeighbors(int nbrNabos, Comparator<Assignment> comp) {
    TreeSet<Assignment> nabos = new TreeSet<Assignment>(comp);
    ArrayList<Assignment> copies = this.copy(nbrNabos+1);
    int maxTries = nbrNabos*5;
    
    Assignment a = copies.remove(nbrNabos);
    while(nbrNabos > 0 && maxTries-- > 0){
      boolean found = false;
      if(rand.nextBoolean()){
        found = a.moveRandomAction();
      }else{
        found = a.changeVehicleRandomAction();
      }
      if(found && nabos.add(a)){
        nbrNabos--;
        a = copies.remove(nbrNabos);
      }
      
    }
    return nabos;
  }
  
  private boolean moveRandomAction(){
    // find random route
    List<Action> route = null;
    while(route == null || route.isEmpty()){
      route = vehicleRoutes.get(randomVehicle(null));
    }
    
    // find random action
    Action act = randomAction(route);
    
    // I like to ...move it move it 
//    return moveActionByOne(act, rand.nextBoolean());
    int distance = rand.nextInt(route.size())* (rand.nextBoolean() ? -1: 1);
    return moveAction(act, distance);
  }
  
  
  private boolean changeVehicleRandomAction(){
    // find two (different) random vehicles
    Vehicle aV = null;
    
    while(aV == null || vehicleRoutes.get(aV).isEmpty()){
      aV = randomVehicle(null);
    }
    Vehicle toV = randomVehicle(null);
    Task t = randomAction(vehicleRoutes.get(aV)).task;
    
    return changeVehicle(toV, t);
  }
  
  /**
   * 
   * @param route
   * @return a random action in the route
   */
  private Action randomAction(List<Action> route) {
    return route.get(rand.nextInt(route.size()));
  }
  
  /**
   * 
   * @param notV
   * @return a random vehicle not equals to notV
   */
  private Vehicle randomVehicle(Vehicle notV) {
    Vehicle v = null;
    while (v == null || v.equals(notV)) {
      v = CentralizedAgent.allVehicles[rand.nextInt(CentralizedAgent.allVehicles.length)];
    }
    return v;
  }
  
  
  
  /**
   * removes the first task from the 'fromV' route and puts it at the end of the
   * 'toV' route.
   * 
   * @param fromV
   * @param toV
   * @return true iff the change is legal, false otherwise. Does not change the
   *         calling object if false is returned.
   */
  @Deprecated
  private boolean changeVehicle(Vehicle fromV, Vehicle toV) {
    // input validation
    // the vehicles must be different
    
    if (fromV.equals(toV)) { return false; }
    
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
    
    // update times of the two lists
    updateIndexes(fromV, toV);
    return true;
  }
  
  /**
   * moves the task at the end of the toV route. if it is already in the toV route still moves it to the end
   * @param toV
   * @param t
   * @return true if successful, may throw IllegalStateExceptions
   */
  private boolean changeVehicle(Vehicle toV, Task t) {
    // input validation
    if(toV == null || t==null || t.weight > toV.capacity()){
      return false;
    }
    
    Vehicle fromV = remove(t);
    
    vehicleRoutes.get(toV).add(new Pickup(t));
    vehicleRoutes.get(toV).add(new Deliver(t));
    
    updateIndexes(toV, fromV);
    vehicles.put(t, toV);
    
    return true;
  }
  
  /**
   * removes the task from its route
   * @param t
   * @return the vehicle the task belonged to
   */
  private Vehicle remove(Task t){
    Vehicle v = vehicles.get(t);
    Pickup pick = new Pickup(t);
    Deliver del = new Deliver(t);
    
    boolean rem = vehicleRoutes.get(v).remove(del);
    rem = rem & vehicleRoutes.get(v).remove(pick);
    
    if (!rem) { throw new IllegalStateException(
        "The routes are inconsistent with the vehicles map"); }
    
    return v;
    
  }
  
  /**
   * Moves the action by at most the given distance in the given direction.
   * It stops moving if further move would violate the pickup before delivery constraint.
   * NOTE: AFTER THIS CALL THE ASSIGMENT MAY VIOLATE THE OVERLOAD CONSTRAINT. IN THIS CASE FALSE IS RETURNED.
   * @param act
   * @param distance (positive if moving right, negative if moving left)
   * @return true iff move successful and no constraint is violated, false otherwise.
   */
  private boolean moveAction(Action act, int maxdistance) {
    if(maxdistance == 0){
      return true;
    }else if(act == null){
      return false;
    }
    
    int index = this.indexOf.get(act);
    Vehicle vehic = this.vehicles.get(act.task);
    List<Action> route = this.vehicleRoutes.get(vehic);
    
 // sanity checks
    if(!Constraints.checkPickupBeforeDeliveryConstraint(this)){
      throw new IllegalStateException("moveAction not correct from the start!");
    }
    
    if (route.isEmpty()) { throw new IllegalStateException(
        "the vehicles map is inconsistent with the task (the task is not in the vehicles route)!"); }
    if (route.size() % 2 != 0) { throw new IllegalStateException(
        "A route must have a even size (always one pickup and delivery)"); }
    
    
    //**********3
    
    Action other = act.isDelivery() ? new Pickup(act.task) : new Deliver(act.task);
    int indexOther = indexOf.get(other);
    int newIndex = Math.max(index + maxdistance, 0); // newIndex >= 0
    newIndex = Math.min(newIndex, route.size()-1); // newIndex < route.size()    
    
    if(act.isPickup() && maxdistance > 0){
      // moving right pickup, must not overtake the delivery
      newIndex = Math.min(newIndex, indexOther);
    }else if(act.isDelivery() && maxdistance < 0){
      // moving left delivery, must not land before the pickup
      newIndex = Math.max(indexOther+1, newIndex);
    }
    
    route.remove(index);
    newIndex -= index < newIndex ? 1 : 0; 
    route.add(newIndex, act);
    
    
    //**********
    
    // remove the action 
//    ListIterator<Action> it = route.listIterator(index);
//    if (!it.next().equals(act)) { // just to be sure
//      throw new IllegalStateException("The indexOf map is inconistent with the route! ");
//    }
//    
//    it.remove(); // removes act from the list
//    
//    // move the action
//    if(moveRight){
//      this.moveRight(act, distance, it);
//    }else{
//      this.moveLeft(act, distance, it);
//    }
//    
//    Action curr = null;
//    do{
//      curr = moveRight ? it.next() : it.previous();
//      distance--;
//    }while(distance > 0 && !curr.task.equals(act.task));
//    
//    // put cursor at right position
//    if(moveRight){
//      it.previous();
//    }else{
//      it.next();
//    }
//    
//    // add the action again and update the indexes
//    it.add(act);
    
    updateIndexes(vehic);
    
    mNotCorrupt =  Constraints.checkVehicleOverloadConstraint(this, vehic);
    if(!Constraints.checkPickupBeforeDeliveryConstraint(this)){
      throw new IllegalStateException("moveAction not correct yet!");
    }
    return mNotCorrupt;
    
  }
  
  /**
   * @param act
   * @param distance
   * @param it the list iterator that removed the action already. so the last call of it was it.remove(). and the removed Action was the action to be moved
   */
  @Deprecated
  private void moveRight(Action act, int distance, ListIterator<Action> it){
    while(it.hasNext() && distance-- > 0 && !it.next().task.equals(act.task));
    it.previous();
    it.add(act);
  }
  
  /**
   * @param act
   * @param distance
   * @param it the list iterator that removed the action already. so the last call of it was it.remove(). and the removed Action was the action to be moved
   */
  @Deprecated
  private void moveLeft(Action act, int distance, ListIterator<Action> it){
    while(it.hasPrevious() && distance-- > 0 && !it.previous().task.equals(act.task));
    it.next();
    it.add(act);
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
   * @return true iff the change is legal, false otherwise. Does not change the
   *         calling object if false is returned.
   */
  @Deprecated
  private boolean moveActionByOne(Action act, boolean moveRight) {
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
    } else if ((moveRight && act.isDelivery() && swappedA
        .isPickup()) || (!moveRight && act.isPickup() && swappedA.isDelivery())) {
      // a pickup is moved before delivery -> may overload the vehicle
      cviolated = !Constraints.checkVehicleOverloadConstraint(this, this.vehicles.get(
          act.task));
    }
    
    if (cviolated) {
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
  
  @Override
  public String toString() {
    return new StringBuilder().append("Assignment: \n    vehicRoutes: ")
        .append(vehicleRoutsString())
//        .append("\n    vehicles: ")
//        .append(vehicles.toString())
        .append("\n    indexOf: ")
        .append(indexOf.toString())
        .append("\n\n").toString();
  }
  
  public String vehicleRoutsString() {
    StringBuilder sb = new StringBuilder();
    for (Entry<Vehicle, List<Action>> e : vehicleRoutes.entrySet()) {
      sb.append("vehicle ");
      sb.append(e.getKey().id());
      sb.append(": \n    ");
      sb.append(vehicleRoutes.get(e.getKey()).toString());
      sb.append("\n    ");
      // for(Action act: e.getValue()){
      //
      // }
    }
    return sb.toString();
    
  }
  
  /**
   * 
   * @return true iff the assigment is corrupt
   */
  public boolean isCorrupt() {
    return ! mNotCorrupt;
  }
  
  
}
