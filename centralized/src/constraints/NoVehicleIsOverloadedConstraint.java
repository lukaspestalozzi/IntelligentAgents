package constraints;

import javax.management.RuntimeErrorException;

import template.Assignment;

/**
 * 
 * load(a_i @ PickUp) > freeload(v_k) => vehicle(a_i) != v_k  
 *
 */
public class NoVehicleIsOverloadedConstraint extends Constraint{

  @Override
  boolean checkAssignment(Assignment a) {
    //TODO
    throw new RuntimeException("Not yet implemented");
  }

}
