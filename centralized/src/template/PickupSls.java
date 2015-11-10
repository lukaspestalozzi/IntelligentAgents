package template;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class PickupSls {
  private ObjFunc mObjFunc;
  private final double mProba;
  private final Comparator<Assignment> mAssigmentComp;
  private Random r = new Random(2015);

  public PickupSls(ObjFunc objFunc, double probability) {
    mObjFunc = objFunc;
    mProba = probability;
    
    mAssigmentComp = new Comparator<Assignment>() {

      @Override
      public int compare(Assignment a1, Assignment a2) {
        double cost1 = mObjFunc.compute(a1);
        double cost2 = mObjFunc.compute(a2);
        if(cost1 == cost2){
          return 0;
        }else{
          return cost1 < cost2 ? -1: 1;
        }
      }
    };
  }

  public Assignment updateAssignment(Assignment oldA) {
    ArrayList<Assignment> nabos = oldA.generateNeighbors(10000);
//    for(Assignment ass : nabos){
//      System.out.println(mObjFunc.compute(ass));
//    }
//    System.out.println(mProba);
    System.out.println("nbr nabos: "+nabos.size());
    if(nabos.size() == 1){
      return nabos.get(0);
    }
    Assignment newA;
    
    if(Math.random() < mProba){
        // suboptimal solution
//      newA = chooseSuboptimalNabo(nabos);
      newA = chooseSuboptimalNabo(nabos);
      System.out.println("--> suboptimal");
    }else{
      System.out.println("--> min");
      newA=  chooseMinNabo(nabos);
    }
    
//    System.out.println(newA.toString());
    System.out.println("new cost: "+mObjFunc.compute(newA));
    return newA;
  }
  
  private Assignment cooseRandom(ArrayList<Assignment> nabos){
    return nabos.get(r.nextInt(nabos.size()));
  }
  
  private Assignment chooseSuboptimalNabo(ArrayList<Assignment> nabos){
    nabos.sort(mAssigmentComp);
    int nbrCandidates = (int) Math.ceil(nabos.size()*0.4);
    int index = r.nextInt(nbrCandidates);
    
    System.out.println("index: "+index);
    return nabos.get(index);
  }
  
  private Assignment chooseMinNabo(ArrayList<Assignment> nabos) {
    if(nabos.isEmpty()){
      return null;
    }else if(nabos.size() == 1){
      return nabos.get(0);
    }
    
    int minindex = -1;
    double minval = Integer.MAX_VALUE;
    
    // find min nabo
    for(int i = 0; i < nabos.size(); i++){
      double val = mObjFunc.compute(nabos.get(i));
      if(val  <= minval){
        minindex = i;
        minval = val;
      }
    }
    return nabos.get(minindex);
    
  }
}
