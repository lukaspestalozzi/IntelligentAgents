package template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    if(mCarriedTasks != null){
      tasks = TaskSet.union(tasks, mCarriedTasks);
    }
    if(tasks.isEmpty()){
      return new Plan(vehicle.getCurrentCity());
    }
    Plan plan;
    System.out.println("Calculate plan...");
    
    // Compute the plan with the selected algorithm.
    switch (mAlgorithm) {
      case ASTAR:
        plan = bestFSPlan(vehicle, tasks);
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
    Plan plan = new Plan(vehicle.getCurrentCity());
    for (SearchNode<State> n : path) {
      String as = n.getActionFromParent();
      if (as.contains(PickupAstar.MOVE_ACTION)) {
        City c = mTopology.parseCity(as.split(";")[1]);
        plan.appendMove(c);
        
      } else if (as.contains(PickupAstar.PICKUP_ACTION)) {
        int id = Integer.valueOf(as.split(";")[1]);
        plan.appendPickup(getTask(tasks, id));
        
      } else if (as.contains(PickupAstar.DELIVER_ACTION)) {
        int id = Integer.valueOf(as.split(";")[1]);
        plan.appendDelivery(getTask(tasks, id));
        
      } else if (as.contains("ROOT")) {
        // Nothing to add here.
      } else {
        throw new RuntimeException("Never happens");
      }
    }
    System.out.println("plan: " + plan.toString());
    return plan;
  }
  
  private static Task getTask(TaskSet tasks, int id) {
    for (Task t : tasks) {
      if (t.id == id) { return t; }
    }
    throw new IllegalArgumentException(id + "is no valid id");
  }
}
