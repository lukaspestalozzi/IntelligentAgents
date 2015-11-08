package constraints;

import java.util.Map;
import java.util.Map.Entry;

import logist.simulation.Vehicle;
import logist.task.Task;
import template.Action;
import template.Assignment;

/**
 * 
 * firstAction(v_k) = a_j => vehicle(a_j) = v_k
 *
 */
public class ActionVehicleConstraint extends Constraint {
  @Override
  boolean checkAssignment(Assignment a) {
    Map<Task, Vehicle> tv = a.vehicles;
    
    for (Entry<Vehicle, Action> e : a.firstAction.entrySet()) {
      Action act = e.getValue();
      Task t = act.task;
      
      if(! tv.get(t).equals(e.getKey())){
        return false;
      }
      
    }
    return true;
  }
}
