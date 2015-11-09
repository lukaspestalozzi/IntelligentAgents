package template;

import java.util.ArrayList;

public class PickupSls {
  private ObjFunc mObjFunc;
  private double mProba;

  public PickupSls(ObjFunc objFunc, double probability) {
    mObjFunc = objFunc;
  }

  public Assignment updateAssignment(Assignment oldA) {
    Assignment newA = chooseNeighbours(oldA); 
    newA = localChoice(newA, oldA, mObjFunc);
    return newA;
  }

  private Assignment chooseNeighbours(Assignment oldA) {
    ArrayList<Assignment> nabos = oldA.generateNeighbors();
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

  private Assignment localChoice(Assignment newA, Assignment oldA, ObjFunc objFunc) {
    if(Math.random() < mProba){
      return newA;
    }else{
      return oldA;
    }
  }
}
