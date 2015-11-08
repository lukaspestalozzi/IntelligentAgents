package constraints;

import java.util.Map.Entry;

import logist.plan.Action.Pickup;
import template.Action;
import template.Assignment;

/**
 * 
 * times(Delivery(t_1)) > times(PickUp(t_1))
 *
 */
public class PickupBeforeDeliveryConstraint extends Constraint{

  @Override
  boolean checkAssignment(Assignment a) {
    for(Entry<Action, Long> e : a.getTimes().entrySet()){
      if(e.getKey().isDelivery()){
        long tpickup = a.getTimes().get(new Pickup(e.getKey().getTask()));
        if(tpickup >= e.getValue()){
          return false;
        }
      }
    }
    return true;
  }

}
