package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import logist.LogistPlatform;
import logist.LogistSettings;
import logist.agent.Agent;
import logist.behavior.CentralizedBehavior;
import logist.config.Parsers;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 *
 */
@SuppressWarnings("unused")
public class CentralizedAgent implements CentralizedBehavior {
  
  public static Vehicle[] allVehicles;
  
  private Topology mTopology;
  private TaskDistribution mDistribution;
  private Agent mAgent;
  private long mTimeout_setup;
  private long mTimeout_plan;
  private int mIter;
  private double mProba;
  
  @Override
  public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
    
    // this code is used to get the timeouts
    LogistSettings ls = null;
    try {
      ls = LogistPlatform.getSettings();
      // ls = Parsers.parseSettings("config\\settings_default.xml");
      // the setup method cannot last more than timeout_setup milliseconds
      mTimeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
      // the plan method cannot execute more than timeout_plan milliseconds
      mTimeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);
    } catch (Exception exc) {
      System.out.println(
          "There was a problem loading the configuration file. taking default");
      // default 300 seconds
      mTimeout_setup = 300000;
      mTimeout_plan = 300000;
    }
    
    mProba = agent.readProperty("sls_proba", Double.class, 0.5);

    
    mIter = agent.readProperty("amnt_iter", Integer.class, 10000);
    
    this.mTopology = topology;
    this.mDistribution = distribution;
    this.mAgent = agent;
  }
  
  @Override
  public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
    System.out.println("nbr tasks: "+tasks.size());
    System.out.println("nbr vehicles: "+vehicles.size());
    
    long time_start = System.nanoTime();
    allVehicles = vehicles.toArray(new Vehicle[vehicles.size()]);
    // System.out.println("Agent " + agent.id() + " has tasks " + tasks);
    
    List<Plan> plans = slsPlans(vehicles, tasks);
    
    long time_end = System.nanoTime();
    long duration = time_end - time_start;
    System.out.println("The plan was generated in " + duration/1000 + " milliseconds.");
    double realcost = 0;
    for(int i = 0; i < plans.size(); i++){
      realcost += plans.get(i).totalDistance()*vehicles.get(i).costPerKm();
    }
    System.out.println("Real total cost is: "+realcost);
    return plans;
  }
  
  private List<Plan> slsPlans(List<Vehicle> vehicles, TaskSet tasks) {
    
    ObjFunc objFunc = new ObjFunc();
    Assignment initA = selectInitalSolution(vehicles, tasks);
    if (initA == null) {
      System.out.println("No Plan possible!!!");
      List<Plan> plans = new ArrayList<>();
      for(Vehicle v : vehicles){
        plans.add(new Plan(v.getCurrentCity()));
      }
      return plans;
    }
    
//    if (vehicles.size() == 1) { return initA.generatePlans(vehicles); }
    
    // search
    PickupSls sls = new PickupSls(objFunc, mProba, mIter, tasks.toArray(new Task[tasks.size()]));
    Assignment bestA = sls.run(initA);
    List<Plan> plans = bestA.generatePlans(vehicles);
    
    // print the plans
    System.out.println("Plans: ");
    for (int i = 0; i < plans.size(); i++) {
      Plan p = plans.get(i);
      
      System.out.println("vehic: "+vehicles.get(i).id()+" --> "+p.toString());
      System.out.println();
    }
    
    return plans;
  }
  
  private Assignment selectInitalSolution(List<Vehicle> vehicles, TaskSet tasks) {
    if (tasks == null || tasks.isEmpty() || vehicles == null || vehicles
        .isEmpty()) { return null; }
    Map<Vehicle, List<Action>> vehicleRoutes = new HashMap<Vehicle, List<Action>>();
    Map<Task, Vehicle> tv = new HashMap<>();
    Map<Action, Integer> indexOf = new HashMap<>();
    
    // initialize all lists and find vehicle with the biggest capacity.
    double maxC = -1;
    Vehicle maxV = null;
    for (Vehicle v : vehicles) {
      vehicleRoutes.put(v, new LinkedList<Action>());
      if (v.capacity() > maxC) {
        maxC = v.capacity();
        maxV = v;
      }
    }
    
    // add all actions to the maxV indexOf and vehicles
    List<Action> maxVRoute = vehicleRoutes.get(maxV);
    int index = 0;
    for (Task t : tasks) {
      Pickup pick = new Pickup(t);
      Deliver del = new Deliver(t);
      
      // add to route
      maxVRoute.add(pick);
      maxVRoute.add(del);
      
      // add to indexOf
      indexOf.put(pick, index++);
      indexOf.put(del, index++);
      
      // add to vehicle
      tv.put(t, maxV);
    }
    
    Assignment a = new Assignment(vehicleRoutes, tv, indexOf);
    if (!Constraints.checkAllConstraints(a, tasks
        .size())) { throw new IllegalStateException(
            "Not all constraints are satisfied!"); }
    return a;
    
  }
  
  private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
    City current = vehicle.getCurrentCity();
    Plan plan = new Plan(current);
    
    for (Task task : tasks) {
      // move: current city => pickup location
      for (City city : current.pathTo(task.pickupCity)) {
        plan.appendMove(city);
      }
      
      plan.appendPickup(task);
      
      // move: pickup location => delivery location
      for (City city : task.path()) {
        plan.appendMove(city);
      }
      
      plan.appendDelivery(task);
      
      // set current city
      current = task.deliveryCity;
    }
    return plan;
  }
}
