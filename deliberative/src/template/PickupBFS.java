package template;

import java.util.LinkedList;

import logist.simulation.Vehicle;
import logist.task.TaskSet;

public class PickupBFS extends PickupAstar{

  public PickupBFS(State start, Vehicle vehicle, TaskSet tasks) {
    super(start, vehicle, tasks, false);
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
