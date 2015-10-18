package template;

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

	enum Algorithm { BFS, ASTAR, NAIVE}
	
	private Topology mTopology;
	private TaskDistribution mTd;
	
	private Agent mAgent;
	private int mCapacity;

	private Algorithm mAlgorithm;
	
	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {
		mTopology = topology;
		mTd = td;
		mAgent = agent;
		
		// initialize the planner
		int capacity = agent.vehicles().get(0).capacity();
		String algorithmName = agent.readProperty("algorithm", String.class, "ASTAR");
		
		mAlgorithm = Algorithm.valueOf(algorithmName.toUpperCase());
		System.out.println("using "+mAlgorithm);
	}
	
	@Override
	public Plan plan(Vehicle vehicle, TaskSet tasks) {
		Plan plan;

		// Compute the plan with the selected algorithm.
		switch (mAlgorithm) {
		case ASTAR:
			plan = bestFSPlan(vehicle, tasks);
			break;
		case BFS:
			// ...
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
	
	private Plan bfsPlan(Vehicle vehicle, TaskSet tasks){
	  
	  final Package[] allPackages = new Package[tasks.size()];
	  Position[] initialPositions = new Position[tasks.size()];
    
    Iterator<Task> it = tasks.iterator();
    while(it.hasNext()){
      Task t = it.next();
      allPackages[t.id] = new Package(t.weight, t.id);
      initialPositions[t.id] = new Waiting(t.pickupCity);
    }
    
    // initial state:
    State initialState = new State(vehicle.getCurrentCity(), vehicle.capacity(), initialPositions);
    
    PickupBFS bfs = new PickupBFS(initialState, vehicle, tasks, allPackages);
    List<SearchNode<State>> path = bfs.search();
    return pathToPlan(path, vehicle, tasks);
	  
	}
	
private Plan bestFSPlan(Vehicle vehicle, TaskSet tasks){
    
    final Package[] allPackages = new Package[tasks.size()];
    Position[] initialPositions = new Position[tasks.size()];
    
    Iterator<Task> it = tasks.iterator();
    while(it.hasNext()){
      Task t = it.next();
      allPackages[t.id] = new Package(t.weight, t.id);
      initialPositions[t.id] = new Waiting(t.pickupCity);
    }
    
    // initial state:
    State initialState = new State(vehicle.getCurrentCity(), vehicle.capacity(), initialPositions);
    
    PickupBestFs bestFs = new PickupBestFs(initialState, vehicle, tasks, allPackages);
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
		
		if (!carriedTasks.isEmpty()) {
			// This cannot happen for this simple agent, but typically
			// you will need to consider the carriedTasks when the next
			// plan is computed.
		}
	}
	
	public Plan pathToPlan(List<SearchNode<State>> path, Vehicle vehicle, TaskSet tasks){
	  Plan plan = new Plan(vehicle.getCurrentCity());
    for(SearchNode<State> n : path){
      String as = n.getActionFromParent();
      if(as.contains(PickupAstar.MOVE_ACTION)){
        City c = mTopology.parseCity(as.split(";")[1]);
        plan.appendMove(c);
        
      }else if(as.contains(PickupAstar.PICKUP_ACTION)){
        int id = Integer.valueOf(as.split(";")[1]);
        plan.appendPickup(getTask(tasks, id));
        
      }else if(as.contains(PickupAstar.DELIVER_ACTION)){
        int id = Integer.valueOf(as.split(";")[1]);
        plan.appendDelivery(getTask(tasks, id));
        
      }else if (as.contains("ROOT")){
        // Nothing to add here.
      }else{
        throw new RuntimeException("Never happens");
      }
    }
    System.out.println("plan: "+plan.toString());
    return plan;
  }
	
	private static Task getTask(TaskSet tasks, int id){
	  for(Task t : tasks){
	    if(t.id == id){
	      return t;
	    }
	  }
	  throw new IllegalArgumentException(id+ "is no valid id");
	}
}
