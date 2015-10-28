package template;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import logist.plan.Action.Delivery;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public abstract class PickupAstar extends Astar<State> {
  public static final String MOVE_ACTION = "MOVE";
  public static final String DELIVER_ACTION = "DELIVER";
  public static final String PICKUP_ACTION = "PICKUP";
  

  protected final TaskSet mTasks;
  private final Vehicle mVehicle;
  
  public PickupAstar(State start, Vehicle vehicle, TaskSet tasks) {
    super(start);
    mTasks = tasks;
    mVehicle = vehicle;
  }
  
  @Override
  public boolean isGoal(SearchNode<State> s) {
    HashMap<Integer, Position> ps = s.getState().getPackagePositions();
    for(Position p : ps.values()){
      if( !p.isDelivered()){return false;}
    }
    return true;
  }
  
  @Override
  public List<SearchNode<State>> children(SearchNode<State> s) {
    // TODO make more efficient.
    
    State state = s.getState();
    List<SearchNode<State>> kids = new LinkedList<SearchNode<State>>();
    City c = state.getVehiclePosition();
    
    // Move
    for(City nabo : c.neighbors()){
      State next = state.transition(new Move(nabo), mVehicle);
      
      
      
      if (next != null) {
        kids.add(new SearchNode<State>(next, moveString(nabo)));
      }
    }
    
    for (Task t : mTasks) {

      // Deliver
      if (t.deliveryCity.equals(c)) {
        State next = state.transition(new Delivery(t), mVehicle);
        if (next != null) {
          kids.add(new SearchNode<State>(next, deliverString(t)));
        }
      } 
      
      // Pickup
      if (t.pickupCity.equals(c)) {
        State next = state.transition(new Pickup(t), mVehicle);
        if (next != null) {
          kids.add(new SearchNode<State>(next, pickUpString(t)));
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
  public double cost(SearchNode<State> from, SearchNode<State> to) {
    return from.getState().getVehiclePosition()
        .distanceTo(to.getState().getVehiclePosition());
  }
}
