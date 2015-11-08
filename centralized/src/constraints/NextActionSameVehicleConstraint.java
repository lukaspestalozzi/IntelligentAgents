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
    
    Map<Task, Vehicle> tv = a.getVehicles();
    
    for(Entry<Action, Action> e : a.getNextAction().entrySet()){
      Vehicle v1 = tv.get(e.getKey().getTask());
      Vehicle v2 = tv.get(e.getValue().getTask());
      
      if(! v1.equals(v2)){
        return false;
      }
    }
    return true;
    
  }

}
