package template;

import java.util.ArrayList;

public class PickupSls {
  private Assignment mOldAssignment;
  private ObjFunc mObjFunc;
  private double mProba;

  public PickupSls(Assignment oldA, ObjFunc objFunc, double probability) {
    mOldAssignment = oldA;
    mObjFunc = objFunc;
  }

  public Assignment updateAssignment() {
    Assignment newA = chooseNeighbours(); 
    newA = localChoice(newA, mObjFunc);
    return newA;
  }

  private Assignment chooseNeighbours() {
    ArrayList<Assignment> nabos = mOldAssignment.generateNeighbors();
    int minindex = -1;
    double minval = Integer.MAX_VALUE;
    
    // find min nabo
    for(int i = 0; i < nabos.size(); i++){
      double val = mObjFunc.compute(nabos.get(i));
      if(val  < minval){
        minindex = i;
        minval = val;
      }
    }
    return nabos.get(minindex);
    
  }

  private Assignment localChoice(Assignment newA, ObjFunc objFunc) {
    if(Math.random() < mProba){
      return newA;
    }else{
      return mOldAssignment;
    }
  }
}
