package template;

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
    
    mProba = agent.readProperty("SLS_Proba", double.class, 0.9); // TODO put in
                                                                 // xml file
    
    mIter = agent.readProperty("amnt_iter", int.class, 10000);
    
    this.mTopology = topology;
    this.mDistribution = distribution;
    this.mAgent = agent;
  }
  
  @Override
  public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
    long time_start = System.currentTimeMillis();
    allVehicles = vehicles.toArray(new Vehicle[vehicles.size()]);
    // System.out.println("Agent " + agent.id() + " has tasks " + tasks);
    
    List<Plan> plans = slsPlans(vehicles, tasks);
    
    long time_end = System.currentTimeMillis();
    long duration = time_end - time_start;
    System.out.println("The plan was generated in " + duration + " milliseconds.");
    
    return plans;
  }
  
  private List<Plan> slsPlans(List<Vehicle> vehicles, TaskSet tasks) {
    
    ObjFunc objFunc = new ObjFunc();
    Assignment oldA = selectInitalSolution(vehicles, tasks);
    if (vehicles.size() == 1) { return oldA.generatePlans(vehicles); }
    if (oldA == null) {
      // TODO what to return if no plan is possible?
      System.out.println("Plan not possible!!!");
      return null;
    }
    Assignment bestA = null;
    double bestCost = Double.MAX_VALUE;
    Assignment newA = oldA;
    PickupSls sls = new PickupSls(objFunc, mProba, mIter);
    
    for (int i = 0; i < mIter; i++) {
      System.out.println("\n\nIteration: " + i);
      newA = sls.updateAssignment(oldA);
      
      double val = objFunc.compute(newA);
      if(bestCost > val){
        bestCost = val;
        bestA = newA;
      }
      
      oldA = newA;
    }
    
    System.out.println("Final cost: "+bestCost);
    List<Plan> plans = bestA.generatePlans(vehicles);
    System.out.println("Plans: ");
    for (Plan p : plans) {
      
      System.out.println(p.toString() + " --> " + p.totalDistance());
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
