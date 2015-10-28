package template;

import java.util.Iterator;
import java.util.LinkedList;

import logist.simulation.Vehicle;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class PickupBestFs extends PickupAstar {
  
  public PickupBestFs(State start, Vehicle vehicle, TaskSet tasks) {
    super(start, vehicle, tasks);
  }

  @Override
  public double heuristic(SearchNode<State> s) {
    double min = Double.POSITIVE_INFINITY;
    int delivered = 0;
    for(Position pos : s.getState().getPackagePositions().values()) {
     
      City goal = pos.getGoal();
      double currentVal = Double.MAX_VALUE;
      if(pos.isInDelivery()) {
        currentVal = ((InDelivery) pos).vehicle.getCurrentCity().distanceTo(goal);
      }
      else if(pos.isWaiting()) {
        currentVal = ((Waiting) pos).city.distanceTo(goal);
      }
      else {
        delivered++;
      }
      min = currentVal < min ? currentVal : min;

    }
//    min = min == Double.MAX_VALUE ? 0 : min;
//    return min;//*(s.getState().getPackagePositions().size() - delivered);
      
    int nbrNotDelivered = (s.getState().getPackagePositions().size() - delivered);
    return nbrNotDelivered == 0 ? 0: min; //*nbrNotDelivered;
  }
  
//  /**
//   * 
//   * @param s
//   * @return the number of packages that are already delivered in the given state.
//   */
//  private double nbrDelivered(SearchNode<State> s){
//    int counter = 0;
//    for(Position p : s.getState().getPackagePositions().values()){
//      if(p.isDelivered()){
//        counter++;
//      }
//    }
//    return counter;
//  }
  
  
  
  @Override
  public void insertOpen(SearchNode<State> k, LinkedList<SearchNode<State>> openList) {
    Iterator<SearchNode<State>> it = openList.iterator();
    double f = k.getF();
    int index = 0;
    double otherF;
    while (it.hasNext()) {
      otherF = it.next().getF();
      if (f <= otherF) {
        break;
      }
      index++;
    }
    openList.add(index, k);
  }
  
}
