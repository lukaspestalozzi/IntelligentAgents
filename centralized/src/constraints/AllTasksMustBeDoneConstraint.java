package constraints;

import template.Action;
import template.Assignment;

/**
 * 
 * all tasks must be picked up and delivered: 
 * the set of values of the variables in the union of the nextAction and the firstAction 
 * must be equal to the set of the PickUp and Delivery for all elements of the set of tasks T 
 * plus Nv times the value NULL.
 * 
 * In other words:
 * The number of NULL in next action must be the same as nbr of vehicules where firstAction(v) != NULL.
 */
public class AllTasksMustBeDoneConstraint extends Constraint{

  @Override
  public boolean checkAssignment(Assignment a) {    
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

}
