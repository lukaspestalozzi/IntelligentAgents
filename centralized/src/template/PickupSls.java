package template;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

import logist.task.Task;

public class PickupSls {
  // general
  private ObjFunc mObjFunc;
  private final double mProba;
  private final int mNbrIterations;
  private final Task[] mAllTasks;
  private final Comparator<Assignment> mAssigmentComp;
  private int mI;
  // annealing
  private double mProbaAnn;
  private double mAnnStep;
  
  private Random r = new Random(2015);

  public PickupSls(ObjFunc objFunc, double probability, int nbrIterations, Task[] allTasks) {
    mObjFunc = objFunc;
    mProba = probability;
    mProbaAnn = mProba;
    mNbrIterations = nbrIterations;
    mAnnStep = mProba/(mNbrIterations);
    mAllTasks = allTasks;
    
    mI = 0;
    
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
  
  /**
   * runs the sls algorithm.
   * @param initA
   * @return
   */
  public Assignment run(Assignment initA){
    Assignment newA = initA;
    Assignment oldA = initA;
    Assignment bestA = null;
    double bestCost = Double.MAX_VALUE;
    
    for (mI = 0; mI < mNbrIterations; mI++) {
      System.out.println("\n\nIteration: " + mI);
      newA = this.updateAssignment(oldA);
      
      double val = mObjFunc.compute(newA);
      if(bestCost > val){
        bestCost = val;
        bestA = newA;
      }
      
      oldA = newA;
    }
    System.out.println("(final)best cost: "+bestCost);
    return bestA;
  }
  
  

  private Assignment updateAssignment(Assignment oldA) {
//    TreeSet<Assignment> nabos = oldA.generateNeighbors(1000, mAssigmentComp);
    TreeSet<Assignment> nabos = oldA.generateAllNeighbors(mAssigmentComp, randomTask());

    System.out.println("nbr nabos: "+nabos.size());
    if(nabos.size() == 1){
      return nabos.first();
    }
    Assignment newA = choose(nabos);

//    System.out.println("anh proba: "+mProbaAnn);
    System.out.println("new cost: "+mObjFunc.compute(newA));
    return newA;
  }
  
  private Assignment choose(TreeSet<Assignment> nabos){
    double progress = (double)mI/(double)mNbrIterations;
    int maxIndex = (int) Math.floor(nabos.size()*(1-progress));
    maxIndex += r.nextDouble() < mProba ? 1 : 0;
    System.out.println("progress: "+progress+" --> maxIndex: "+maxIndex);
    return chooseSuboptimalNaboGauss(nabos, maxIndex);
  }
  
  private Assignment chooseSuboptimalNaboUniform(TreeSet<Assignment> nabos){
    
    int nbrCandidates = (int) Math.ceil(nabos.size()*0.5);
    int index = r.nextInt(nbrCandidates);
    
    System.out.println("index: "+index);
    // go to index
    Iterator<Assignment> it = nabos.iterator();
    while(it.hasNext() && --index > 0){it.next();}
    return it.next();
  }
  
  private Assignment chooseSuboptimalNaboGauss(TreeSet<Assignment> nabos, int maxIndex){
    if(maxIndex < 0){
      maxIndex = 0;
    }else if(maxIndex == 0){
      return nabos.first();
    }
    double rand = r.nextGaussian();
    while(rand > 1 || rand < 0){
      rand = r.nextGaussian();
    }

    int index = (int)Math.floor(rand*maxIndex);
    
    System.out.println("index: "+index);
    // go to index
    Iterator<Assignment> it = nabos.iterator();
    while(--index > 0){it.next();}
    return it.next();
  }
  
  private Task randomTask(){
    return mAllTasks[r.nextInt(mAllTasks.length)];
  }
  
  private Assignment chooseMinNabo(TreeSet<Assignment> nabos) {
    if(nabos.isEmpty()){
      return null;
    }
    return nabos.first();
    
  }
}
