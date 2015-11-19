package planning;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import logist.simulation.Vehicle;
import logist.task.Task;

public class Constraints {
  
  /**
   * 
   * @param a
   * @param nbrTasks the number of tasks there should be
   * @return
   */
  public static boolean checkAllConstraints(Assignment a, int nbrTasks){
    if(! checkActionVehicleConstraint(a)){
      return false;
    }
    
    else if(! checkAllTasksMustBeDoneConstraint(a, nbrTasks)){
      return false;
    }
    
    else if(! checkFirstActionTime1Constraint(a)){
      return false;
    }
    
    else if(! checkNextActionSameVehicleConstraint(a)){
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
    for(Vehicle v : a.vehicleRoutes.keySet()){
      for(Action act : a.vehicleRoutes.get(v)){
        if(! a.vehicles.get(act.task).equals(v)){
          return false;
        }
      }
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
  public static boolean checkAllTasksMustBeDoneConstraint(Assignment a, int nbrTasks) {
    int nbrActs = 0;
    for(List<Action> l : a.vehicleRoutes.values()){
      nbrActs += l.size();
    }
    if(nbrActs % 2 != 0){
      return false;
    }
    
    return nbrActs/2 == nbrTasks;
  }
  
//  /**
//   * nextAction(a) != a
//   * @param a
//   * @return
//   */
//  public static boolean checkDifferentNextActionConstraint(Assignment a) {
//    for(List<Action> l : a.vehicleRoutes.values()){
//      for(Action act : l){
//        if(e.getKey().equals(e.getValue())){
//          return false;
//        }
//      }
//    }
//    
//    return true;
//  }
  
  /**
   * indexes are correct
   * @param a
   * @return
   */
  public static boolean checkFirstActionTime1Constraint(Assignment a) {
    for(List<Action> l : a.vehicleRoutes.values()){
      int index = 0;
      for(Action act : l){
        if(a.indexOf.get(act) != index++){
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * all actions in a route belong to the correct vehicle
   * @param a
   * @return
   */
  public static boolean checkNextActionSameVehicleConstraint(Assignment a) {
    for(Entry<Vehicle, List<Action>> e : a.vehicleRoutes.entrySet()){
      Vehicle v = e.getKey();
      for(Action act : e.getValue()){
        if(! a.vehicles.get(act.task).equals(v)){
          return false;
        }
      }
    }
    return true;
  }
  
//  /**
//   * nextAction(a_i) = a_j => times(a_j) = times(a_i) + 1
//   * @param a
//   * @return
//   */
//  public static boolean checkNextActionTimePlusOneConstraint(Assignment a) {
//Map<Action, Long> times = a.indexOf;
//    
//    for(Entry<Action, Action> e : a.nextAction.entrySet()){
//      long time1 = times.get(e.getKey());
//      long time2 = times.get(e.getValue());
//      
//      if(time1 != (time2+1)){
//        return false;
//      }
//    }    
//    return true;
//  }
  
  /**
   * load(a_i @ PickUp) > freeload(v_k) => vehicle(a_i) != v_k  
   * @param a
   * @return
   */
  public static boolean checkNoVehicleOverloadedConstraint(Assignment a) {
    for(Vehicle v : a.vehicleRoutes.keySet()){
      if(! checkVehicleOverloadConstraint(a, v)){
        return false;
      }
    }
    return true;
  }
  
  public static boolean checkVehicleOverloadConstraint(Assignment a, Vehicle v){
    double freeLoad = v.capacity();
    for(Action act : a.vehicleRoutes.get(v)){
      if(act.isPickup()){
        freeLoad -= act.task.weight;
      }else{
        freeLoad += act.task.weight;
      }
      if(freeLoad < 0){
        return false;
      }
    }
    return true;
  }
  
  /**
   * times(Delivery(t_1)) > times(PickUp(t_1))
   * @param a
   * @return
   */
  public static boolean checkPickupBeforeDeliveryConstraint(Assignment a) {
    
    for(List<Action> l : a.vehicleRoutes.values()){
      HashSet<Task> set = new HashSet<>();
      for(Action act : l){
        if(act.isPickup()){
          if(! set.add(act.task)){
            return false;
          }
        }else{
          if(set.add(act.task)){
            return false;
          }
        }
      }
    }
    return true;
  }
  
}
