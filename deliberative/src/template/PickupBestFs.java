package template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class PickupBestFs extends PickupAstar {
  
  public PickupBestFs(State start, Vehicle vehicle, TaskSet tasks) {
    super(start, vehicle, tasks);
  }

  @Override
  public double heuristic(SearchNode<State> s) {
    double min = Double.MAX_VALUE;
    int delivered = 0;
    for(Map.Entry<Integer, Position> entry : s.getState().getPackagePositions().entrySet()) {
      int id = entry.getKey();
      Position pos = entry.getValue();
      
      Task myTask = null;
      for(Task aTask : mTasks) {
        if (aTask.id == id)
          myTask = aTask;
      }
      City goal = myTask.deliveryCity;
      double currentVal = Double.MAX_VALUE;
      if(pos.isInDelivery()) {
        InDelivery delivery = (InDelivery) pos;
        currentVal = delivery.vehicle.getCurrentCity().distanceTo(goal);
      }
      else if(pos.isWaiting()) {
        currentVal = ((Waiting) pos).city.distanceTo(goal);
      }
      else {
        delivered++;
      }
      min = currentVal < min ? currentVal : min;
    }
    return min*(s.getState().getPackagePositions().size() - delivered);
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
