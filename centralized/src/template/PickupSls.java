package template;

import java.util.ArrayList;

public class PickupSls {
  private ObjFunc mObjFunc;
  private double mProba;

  public PickupSls(ObjFunc objFunc, double probability) {
    mObjFunc = objFunc;
    mProba = probability;
  }

  public Assignment updateAssignment(Assignment oldA) {
    if(Math.random() < mProba){
      return chooseNeighbours(oldA);
    }else{
      return oldA;
    }
  }

  private Assignment chooseNeighbours(Assignment oldA) {
    ArrayList<Assignment> nabos = oldA.generateNeighbors();
    if(nabos.isEmpty()){
      return oldA;
    }
    
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
}
