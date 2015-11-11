package template;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class PickupSls {
  private ObjFunc mObjFunc;
  private final double mProba;
  private double mProbaAnh;
  private final int mNbrIterations;
  private final Comparator<Assignment> mAssigmentComp;
  private Random r = new Random(2015);

  public PickupSls(ObjFunc objFunc, double probability, int nbrIterations) {
    mObjFunc = objFunc;
    mProba = probability;
    mProbaAnh = mProba;
    mNbrIterations = nbrIterations;
    
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
    TreeSet<Assignment> nabos = oldA.generateNeighbors(500, mAssigmentComp);

    System.out.println("nbr nabos: "+nabos.size());
    if(nabos.size() == 1){
      return nabos.first();
    }
    Assignment newA;
    
    if(Math.random() < mProbaAnh){
      // suboptimal solution
      newA = chooseSuboptimalNabo(nabos);
      System.out.println("--> suboptimal");
    }else{
      System.out.println("--> min");
      newA=  chooseMinNabo(nabos);
    }
    
    mProbaAnh -= mProba/mNbrIterations;
    
//    System.out.println(newA.toString());
    System.out.println("anh proba: "+mProbaAnh);
    System.out.println("new cost: "+mObjFunc.compute(newA));
    return newA;
  }
  
  private Assignment chooseSuboptimalNabo(TreeSet<Assignment> nabos){
    
    int nbrCandidates = (int) Math.ceil(nabos.size()*0.4);
    int index = r.nextInt(nbrCandidates);
    
    System.out.println("index: "+index);
    // go to index
    Iterator<Assignment> it = nabos.iterator();
    while(it.hasNext() && --index > 0){it.next();}
    return it.next();
  }
  
  private Assignment chooseMinNabo(TreeSet<Assignment> nabos) {
    if(nabos.isEmpty()){
      return null;
    }
    return nabos.first();
    
  }
}
