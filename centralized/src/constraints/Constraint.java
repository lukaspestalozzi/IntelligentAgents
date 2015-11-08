package constraints;

import template.Assignment;

abstract public class Constraint {
  
  /**
   * Checks whether the assignment satisfies the constraint.
   * @param a
   * @return true iff it satisfies the constraint, false otherwise
   */
  abstract boolean checkAssignment(Assignment a);

}
