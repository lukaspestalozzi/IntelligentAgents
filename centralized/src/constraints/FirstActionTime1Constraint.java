package constraints;

import java.util.Map;

import template.Action;
import template.Assignment;

/**
 * 
 *firstAction(v_k) = a_j => times(a_j) = 1
 *
 */
public class FirstActionTime1Constraint extends Constraint {

  @Override
  boolean checkAssignment(Assignment a) {
    Map<Action, Long> times = a.getTimes();
    for(Action fa : a.getFirstAction().values()){
      if(times.get(fa) != 1){
        return false;
      }
    }
    return true;
  }

}
