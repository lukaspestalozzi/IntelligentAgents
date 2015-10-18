package template;

import java.util.LinkedList;

import logist.simulation.Vehicle;
import logist.task.TaskSet;

public class PickupBFS extends PickupAstar{

  public PickupBFS(State start, Vehicle vehicle, TaskSet tasks, Package[] allPackages) {
    super(start, vehicle, tasks, allPackages);
  }

  @Override
  public double heuristic(SearchNode<State> s) {
    return 0;
  }

  @Override
  public void insertOpen(SearchNode<State> k, LinkedList<SearchNode<State>> openList) {
    openList.add(k); // FIFO -> Breath First Search.
    
  }

}
