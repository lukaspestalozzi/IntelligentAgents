package constraints;

import java.util.Map.Entry;

import template.Action;
import template.Assignment;

/**
 * 
 * nextAction(a) != a
 */
public class DifferentNextActionConstraint extends Constraint {

  @Override
  public boolean checkAssignment(Assignment a) {
    for(Entry<Action, Action> e : a.nextAction.entrySet()){
      if(e.getKey().equals(e.getValue())){
        return false;
      }
    }
    
    return true;
  }
  
  

}
