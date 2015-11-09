package template;

import java.util.Map;
import java.util.Map.Entry;

import logist.plan.Action.Pickup;
import logist.simulation.Vehicle;
import logist.task.Task;

public class Constraints {
  
  public static boolean checkAllConstraints(Assignment a){
    if(! checkActionVehicleConstraint(a)){
      return false;
    }
    
    else if(! checkAllTasksMustBeDoneConstraint(a)){
      return false;
    }
    
    else if(! checkDifferentNextActionConstraint(a)){
      return false;
    }
    
    else if(! checkFirstActionTime1Constraint(a)){
      return false;
    }
    
    else if(! checkNextActionSameVehicleConstraint(a)){
      return false;
    }
    
    else if(! checkNextActionTimePlusOneConstraint(a)){
      return false;
    }
    
    else if(! checkNoVehicleOverloadedConstraint(a)){
      return false;
    }
    
    else if(! checkPickupBeforeDeliveryConstraint(a)){
      return false;
    }
    
    else{
      return true;
    }
    
  }
  
  /**
   * firstAction(v_k) = a_j => vehicle(a_j) = v_k
   * 
   * @param a
   * @return
   */
  public static boolean checkActionVehicleConstraint(Assignment a) {
    Map<Task, Vehicle> tv = a.vehicles;
    
    for (Entry<Vehicle, Action> e : a.firstAction.entrySet()) {
      Action act = e.getValue();
      Task t = act.task;
      
      if (!tv.get(t).equals(e.getKey())) { return false; }
      
    }
    return true;
  }
  
  /**
   * all tasks must be picked up and delivered: the set of values of the
   * variables in the union of the nextAction and the firstAction must be equal
   * to the set of the PickUp and Delivery for all elements of the set of tasks
   * T plus Nv times the value NULL.
   * 
   * In other words: The number of NULL in next action must be the same as nbr
   * of vehicules where firstAction(v) != NULL.
   * 
   * @param a
   * @return
   */
  public static boolean checkAllTasksMustBeDoneConstraint(Assignment a) {
    int finished = 0;
    for(Action act : a.nextAction.values()){
      if(act == null){
        finished++;
      }
    }
    
    int started = 0;
    for(Action act : a.firstAction.values()){
      if(act != null){
        started++;
      }
    }
    
    return finished == started;
  }
  
  /**
   * nextAction(a) != a
   * @param a
   * @return
   */
  public static boolean checkDifferentNextActionConstraint(Assignment a) {
    for(Entry<Action, Action> e : a.nextAction.entrySet()){
      if(e.getKey().equals(e.getValue())){
        return false;
      }
    }
    
    return true;
  }
  
  /**
   * firstAction(v_k) = a_j => times(a_j) = 1
   * @param a
   * @return
   */
  public static boolean checkFirstActionTime1Constraint(Assignment a) {
    Map<Action, Long> times = a.times;
    for(Action fa : a.firstAction.values()){
      if(times.get(fa) != 1){
        return false;
      }
    }
    return true;
  }
  
  /**
   * nextAction(a_i) = a_j => vehicle(a_j) = vehicle(a_i)
   * @param a
   * @return
   */
  public static boolean checkNextActionSameVehicleConstraint(Assignment a) {
Map<Task, Vehicle> tv = a.vehicles;
    
    for(Entry<Action, Action> e : a.nextAction.entrySet()){
      Vehicle v1 = tv.get(e.getKey().task);
      Vehicle v2 = tv.get(e.getValue().task);
      
      if(! v1.equals(v2)){
        return false;
      }
    }
    return true;
  }
  
  /**
   * nextAction(a_i) = a_j => times(a_j) = times(a_i) + 1
   * @param a
   * @return
   */
  public static boolean checkNextActionTimePlusOneConstraint(Assignment a) {
Map<Action, Long> times = a.times;
    
    for(Entry<Action, Action> e : a.nextAction.entrySet()){
      long time1 = times.get(e.getKey());
      long time2 = times.get(e.getValue());
      
      if(time1 != (time2+1)){
        return false;
      }
    }    
    return true;
  }
  
  /**
   * load(a_i @ PickUp) > freeload(v_k) => vehicle(a_i) != v_k  
   * @param a
   * @return
   */
  public static boolean checkNoVehicleOverloadedConstraint(Assignment a) {
  //TODO
    throw new RuntimeException("Not yet implemented");
  }
  
  /**
   * times(Delivery(t_1)) > times(PickUp(t_1))
   * @param a
   * @return
   */
  public static boolean checkPickupBeforeDeliveryConstraint(Assignment a) {
    for (Entry<Action, Long> e : a.times.entrySet()) {
      if (e.getKey().isDelivery()) {
        long tpickup = a.times.get(new Pickup(e.getKey().task));
        if (tpickup >= e.getValue()) { return false; }
      }
    }
    return true;
  }
  
}
