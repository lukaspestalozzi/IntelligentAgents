package template;

import java.util.List;

import constraints.Constraint;

public class PickupSls {
  private Assignment mOldAssignment;
  private List<Variable> mVariables;
  private List<Constraint> mConstraints;
  private ObjFunc mObjFunc;
  private double mProba;

  public PickupSls(Assignment oldA, List<Variable> variables,
      List<Constraint> constraints, ObjFunc objFunc, double probability) {
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
      List<Variable> mVariables2, List<Constraint> mConstraints2, ObjFunc mObjFunc2) {
    // TODO Auto-generated method stub
    return null;
  }

  private Assignment localChoice(Assignment newA, ObjFunc objFunc) {
    if(Math.random() < mProba){
      // choose newA
      return newA;
    }else{
      return mOldAssignment;
    }
  }

}
