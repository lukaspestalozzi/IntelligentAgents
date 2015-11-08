package constraints;

import java.util.Map;
import java.util.Map.Entry;

import logist.simulation.Vehicle;
import logist.task.Task;
import template.Action;
import template.Assignment;

/**
 * 
 * nextAction(a_i) = a_j => vehicle(a_j) = vehicle(a_i)
 *
 */
public class NextActionSameVehicleConstraint extends Constraint{

  @Override
  boolean checkAssignment(Assignment a) {
    
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

}
