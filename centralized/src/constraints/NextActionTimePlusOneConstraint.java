package constraints;

import java.util.Map;
import java.util.Map.Entry;

import template.Action;
import template.Assignment;

/**
 * 
 * nextAction(a_i) = a_j => times(a_j) = times(a_i) + 1
 *
 */
public class NextActionTimePlusOneConstraint extends Constraint{

  @Override
  boolean checkAssignment(Assignment a) {
    Map<Action, Long> times = a.getTimes();
    
    for(Entry<Action, Action> e : a.getNextAction().entrySet()){
      long time1 = times.get(e.getKey());
      long time2 = times.get(e.getValue());
      
      if(time1 != (time2+1)){
        return false;
      }
    }    
    return true;
  }

}
