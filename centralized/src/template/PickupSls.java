package template;

import java.util.List;

public class PickupSls {
  private Assignment mOldAssignment;
  private List<Variable> mVariables;
  private List<Boolean> mConstraints;
  private ObjFunc mObjFunc;

  public PickupSls(Assignment oldA, List<Variable> variables,
      List<Boolean> constraints, ObjFunc objFunc) {
    mOldAssignment = oldA;
    mVariables = variables;
    mConstraints = constraints;
    mObjFunc = objFunc;
  }

  public Assignment updateAssignment() {
    Assignment newA = chooseNeighbours(mOldAssignment, mVariables, mConstraints, mObjFunc); 
    newA = localChoice(newA, mObjFunc);
    return newA;
  }

  private Assignment chooseNeighbours(Assignment mOldAssignment2,
      List<Variable> mVariables2, List<Boolean> mConstraints2, ObjFunc mObjFunc2) {
    // TODO Auto-generated method stub
    return null;
  }

  private Assignment localChoice(Assignment newA, ObjFunc objFunc) {
    // TODO Auto-generated method stub
    return null;
  }

}
