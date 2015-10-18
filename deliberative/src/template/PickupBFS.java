package template;

import java.util.LinkedList;
import java.util.List;

import logist.plan.Action.Delivery;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class PickupBFS extends BFS<State> {
  public static final String MOVE_ACTION = "MOVE";
  public static final String DELIVER_ACTION = "DELIVER";
  public static final String PICKUP_ACTION = "PICKUP";
  
  private final Package[] mAllPackages;
  private final TaskSet mTasks;
  private final Vehicle mVehicle;
  
  public PickupBFS(State start, Vehicle vehicle, TaskSet tasks, Package[] allPackages) {
    super(start);
    mAllPackages = allPackages;
    mTasks = tasks;
    mVehicle = vehicle;
  }
  
  @Override
  public boolean isGoal(SearchNode<State> s) {
    Position[] ps = s.getState().getPackagePositions();
    for (Task t : mTasks) {
      if (!ps[t.id].isDelivered()) { return false; }
    }
    return true;
  }
  
  @Override
  public List<SearchNode<State>> children(SearchNode<State> s) {
    // TODO make more efficient.
    
    State state = s.getState();
    List<SearchNode<State>> kids = new LinkedList<SearchNode<State>>();
    City c = state.getVehiclePosition();
    
    for (Task t : mTasks) {
      
      // Deliver
      if (t.deliveryCity.equals(c)) {
        State next = state.transition(new Delivery(t), mVehicle);
        if (next != null) {
          kids.add(new SearchNode<State>(next, deliverString(t)));
        }
      } else {
        // Move
        State next = state.transition(new Move(t.deliveryCity), mVehicle);
        if (next != null) {
          kids.add(new SearchNode<State>(next, moveString(t.deliveryCity)));
        }
      }
      
      // Pickup
      if (t.pickupCity.equals(c)) {
        State next = state.transition(new Pickup(t), mVehicle);
        if (next != null) {
          kids.add(new SearchNode<State>(next, pickUpString(t)));
        }
      } else {
        // Move
        State next = state.transition(new Move(t.pickupCity), mVehicle);
        if (next != null) {
          kids.add(new SearchNode<State>(next, moveString(t.pickupCity)));
        }
      }
    }
    return kids;
  }
  
  public static String moveString(City c){
    return MOVE_ACTION+";"+c.name;
  }
  
  public static String deliverString(Task t){
    return DELIVER_ACTION+";"+t.id;
  }
  
  public static String pickUpString(Task t){
    return PICKUP_ACTION+";"+t.id;
  }
  
  
  
  @Override
  public double heuristic(SearchNode<State> s) {
    return nbrDelivered(s);
  }
  
  /**
   * 
   * @param s
   * @return the number of delivered tasks in state s.
   */
  private double nbrDelivered(SearchNode<State> s) {
    int counter = 0;
    Position[] ps = s.getState().getPackagePositions();
    for (Task t : mTasks) {
      if (ps[t.id].isDelivered()) {
        counter++;
      }
    }
    return counter;
  }
  
  @Override
  public double cost(SearchNode<State> from, SearchNode<State> to) {
    return from.getState().getVehiclePosition()
        .distanceTo(to.getState().getVehiclePosition());
  }
}
