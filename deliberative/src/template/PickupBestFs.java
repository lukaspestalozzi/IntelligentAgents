package template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import logist.simulation.Vehicle;
import logist.task.TaskSet;

public class PickupBestFs extends PickupAstar {
  
  public PickupBestFs(State start, Vehicle vehicle, TaskSet tasks) {
    super(start, vehicle, tasks);
  }

  @Override
  public double heuristic(SearchNode<State> s) { // the smaller the closer to the goal.
    return (s.getState().getPackagePositions().size() - nbrDelivered(s));
  }
  
  /**
   * 
   * @param s
   * @return the number of packages that are already delivered in the given state.
   */
  private double nbrDelivered(SearchNode<State> s){
    int counter = 0;
    for(Position p : s.getState().getPackagePositions().values()){
      if(p.isDelivered()){
        counter++;
      }
    }
    return counter;
  }
  
  
  
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
