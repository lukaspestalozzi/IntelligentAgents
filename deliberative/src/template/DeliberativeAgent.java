package template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import logist.agent.Agent;
import logist.behavior.DeliberativeBehavior;
import logist.plan.Plan;
/* import table */
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 * An optimal planner for one vehicle.
 */
@SuppressWarnings("unused")
public class DeliberativeAgent implements DeliberativeBehavior {
  
  enum Algorithm {
    BFS, ASTAR, NAIVE
  }
  
  private Topology mTopology;
  private TaskDistribution mTd;
  
  private Agent mAgent;
  private int mCapacity;
  private TaskSet mCarriedTasks;
  
  private Algorithm mAlgorithm;
  
  @Override
  public void setup(Topology topology, TaskDistribution td, Agent agent) {
    mTopology = topology;
    mTd = td;
    mAgent = agent;
    mCarriedTasks = null; // no tasks are carried at the begining
    
    // initialize the planner
    int capacity = agent.vehicles().get(0).capacity();
    String algorithmName = agent.readProperty("algorithm", String.class, "ASTAR");
    
    mAlgorithm = Algorithm.valueOf(algorithmName.toUpperCase());
    System.out.println("using " + mAlgorithm);
  }
  
  @Override
  public Plan plan(Vehicle vehicle, TaskSet tasks) {
    System.out.println("\nTasks: "+tasks.toString());
    if(mCarriedTasks != null){
      tasks = TaskSet.union(tasks, mCarriedTasks);
    }
    if(tasks.isEmpty()){
      return new Plan(vehicle.getCurrentCity());
    }
    Plan plan;
    System.out.println("Calculate plan...");
    Long elapsedTime = 0L;
    
    // Compute the plan with the selected algorithm.
    switch (mAlgorithm) {
      case ASTAR:
        long bef = System.nanoTime();
        plan = bestFSPlan(vehicle, tasks);
        long aft = System.nanoTime();
        elapsedTime = aft - bef;
        break;
      case BFS:
        plan = bfsPlan(vehicle, tasks);
        break;
      case NAIVE:
        plan = naivePlan(vehicle, tasks);
        break;
      default:
        throw new AssertionError("Should not happen.");
    }
    System.out.println("Time elapsed to compute the plan: " + (elapsedTime / 1000000000.0) + "s");
    mCarriedTasks = null;
    return plan;
  }
  
  private State createInitialState(Vehicle vehicle, TaskSet tasks) {
    HashMap<Integer, Position> initialPositions = new HashMap<>();
    int capacity = vehicle.capacity();
    
    
    
    if(mCarriedTasks != null){
      // remove all tasks that are carried at the moment from the tasks-set 
      // and put them InDelivery.
      tasks = TaskSet.intersectComplement(tasks, mCarriedTasks);
      for (Task t : mCarriedTasks) {
        initialPositions.put(t.id, new InDelivery(vehicle, t.deliveryCity));
        capacity -= t.weight;
      }
    }
    
//    System.out.println("Tasks: "+tasks.toString());
//    System.out.println("Carried: "+(mCarriedTasks == null ? "-" : mCarriedTasks.toString()));
    
    Iterator<Task> it = tasks.iterator();
    for (Task t : tasks) {
      initialPositions.put(t.id, new Waiting(t.pickupCity, t.deliveryCity));
    }
    
    // initial state:
    State initialState = new State(vehicle.getCurrentCity(), capacity,
        initialPositions);
        
    return initialState;
  }
  
  private Plan bfsPlan(Vehicle vehicle, TaskSet tasks) {
    
    // initial state:
    State initialState = createInitialState(vehicle, tasks);
    
    
    
    PickupBFS bfs = new PickupBFS(initialState, vehicle, tasks);
    List<SearchNode<State>> path = bfs.search();
    return pathToPlan(path, vehicle, tasks);
    
  }
  
  private Plan bestFSPlan(Vehicle vehicle, TaskSet tasks) {
    
    
    // initial state:
    State initialState = createInitialState(vehicle, tasks);
    
    
    
    PickupBestFs bestFs = new PickupBestFs(initialState, vehicle, tasks);
    List<SearchNode<State>> path = bestFs.search();
    return pathToPlan(path, vehicle, tasks);
    
  }
  
  private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
    City current = vehicle.getCurrentCity();
    Plan plan = new Plan(current);
    
    for (Task task : tasks) {
      // move: current city => pickup location
      for (City city : current.pathTo(task.pickupCity))
        plan.appendMove(city);
        
      plan.appendPickup(task);
      
      // move: pickup location => delivery location
      for (City city : task.path())
        plan.appendMove(city);
        
      plan.appendDelivery(task);
      
      // set current city
      current = task.deliveryCity;
    }
    return plan;
  }
  
  @Override
  public void planCancelled(TaskSet carriedTasks) {
    System.out.println("Re calculate plan...");
    mCarriedTasks = carriedTasks;
  }
  
  public Plan pathToPlan(List<SearchNode<State>> path, Vehicle vehicle, TaskSet tasks) {
//    System.out.println(path.toString());
    Plan plan = new Plan(vehicle.getCurrentCity());
    Iterator<SearchNode<State>> it = path.iterator();
    State last = it.next().getState(); // start at the root.
    while(it.hasNext()){
      State next = it.next().getState();
      
      // find move that leads from 'last' to 'next'
      if(last.getVehiclePosition().equals(next.getVehiclePosition())){
        // It was no move
        if(last.getFreeLoad() > next.getFreeLoad()){
          // Pickup
          plan.appendPickup(getTask(tasks, last, next));
        }else{
          // Delivery
          plan.appendDelivery(getTask(tasks, last, next));
        }
      }else{
        // it was a move
        plan.appendMove(next.getVehiclePosition());
      }
      last = next;
    }
//    for (SearchNode<State> n : path) {
//      String as = n.getActionFromParent();
//
//      if (as.contains(PickupAstar.MOVE_ACTION)) {
//        City c = mTopology.parseCity(as.split(";")[1]);
//        plan.appendMove(c);
//        
//      } else if (as.contains(PickupAstar.PICKUP_ACTION)) {
//        int id = Integer.valueOf(as.split(";")[1]);
//        plan.appendPickup(getTask(tasks, id));
//        
//      } else if (as.contains(PickupAstar.DELIVER_ACTION)) {
//        int id = Integer.valueOf(as.split(";")[1]);
//        plan.appendDelivery(getTask(tasks, id));
//        
//      } else if (as.contains("ROOT")) {
//        // Nothing to add here.
//      } else {
//        throw new RuntimeException("Never happens");
//      }
//    }
    System.out.println("plan: " + plan.toString());
    return plan;
  }
  
  private static Task getTask(TaskSet tasks, State last, State next){
    for(Integer k : last.getPackagePositions().keySet()){
      if(! last.getPackagePositions().get(k).equals(next.getPackagePositions().get(k))){
        return getTask(tasks, k);
      }
    }    
    throw new IllegalArgumentException();
  }
  
  private static Task getTask(TaskSet tasks, int id) {
    for (Task t : tasks) {
      if (t.id == id) { return t; }
    }
    throw new IllegalArgumentException(id + "is no valid id");
  }
}
