package constraints;

import template.Assignment;

abstract public class Constraint {
  
  /**
   * Checks whether the assignment satisfies the constraint.
   * @param a
   * @return true iff it satisfies the constraint, false otherwise
   */
  abstract public boolean checkAssignment(Assignment a);

}
