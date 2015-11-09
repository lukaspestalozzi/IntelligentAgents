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
  public boolean checkAssignment(Assignment a) {
    Map<Action, Long> times = a.times;
    for(Action fa : a.firstAction.values()){
      if(times.get(fa) != 1){
        return false;
      }
    }
    return true;
  }

}
