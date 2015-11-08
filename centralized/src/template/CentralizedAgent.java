package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constraints.ActionVehicleConstraint;
import constraints.AllTasksMustBeDoneConstraint;
import constraints.Constraint;
import constraints.DifferentNextActionConstraint;
import constraints.FirstActionTime1Constraint;
import constraints.NextActionSameVehicleConstraint;
import constraints.NextActionTimePlusOneConstraint;
import constraints.NoVehicleIsOverloadedConstraint;
import constraints.PickupBeforeDeliveryConstraint;
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
  
  private Topology mTopology;
  private TaskDistribution mDistribution;
  private Agent mAgent;
  private long mTimeout_setup;
  private long mTimeout_plan;
  private int mIter;
  private double mProba;
  
  private final List<Constraint> allConstraints = new ArrayList<Constraint>();
  
  @Override
  public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
    
    // this code is used to get the timeouts
    LogistSettings ls = null;
    try {
      ls = Parsers.parseSettings("config\\settings_default.xml");
    } catch (Exception exc) {
      System.out.println("There was a problem loading the configuration file.");
    }
    
    // the setup method cannot last more than timeout_setup milliseconds
    mTimeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
    // the plan method cannot execute more than timeout_plan milliseconds
    mTimeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);
    
    mProba = agent.readProperty("SLS_Proba", double.class, 0.5); // TODO put in
                                                                 // xml file
    
    mIter = agent.readProperty("amnt_iter", int.class, 10000);
    
    this.mTopology = topology;
    this.mDistribution = distribution;
    this.mAgent = agent;
    
    // Add all constraints:
    allConstraints.add(new DifferentNextActionConstraint());
    allConstraints.add(new FirstActionTime1Constraint());
    allConstraints.add(new ActionVehicleConstraint());
    allConstraints.add(new AllTasksMustBeDoneConstraint());
    allConstraints.add(new NextActionSameVehicleConstraint());
    allConstraints.add(new NextActionTimePlusOneConstraint());
    allConstraints.add(new NoVehicleIsOverloadedConstraint());
    allConstraints.add(new PickupBeforeDeliveryConstraint());
    
  }
  
  @Override
  public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
    long time_start = System.currentTimeMillis();
    
    // System.out.println("Agent " + agent.id() + " has tasks " + tasks);
    
    List<Plan> plans = slsPlans(vehicles, tasks);
    
    long time_end = System.currentTimeMillis();
    long duration = time_end - time_start;
    System.out.println("The plan was generated in " + duration + " milliseconds.");
    
    return plans;
  }
  
  private List<Plan> slsPlans(List<Vehicle> vehicles, TaskSet tasks) {
    // TODO generate the Variables, constraint etc.
    
    ObjFunc objFunc = new ObjFunc();
    Assignment oldA = selectInitalSolution(vehicles, tasks);
    Assignment newA = oldA;
    
    for (int i = 0; i < mIter; i++) {
      PickupSls sls = new PickupSls(oldA, allConstraints, objFunc, mProba);
      newA = sls.updateAssignment();
      oldA = newA;
      if (false/* TODO insert termination condition */) {
        break;
      }
    }
    
    return newA.generatePlans(vehicles);
  }
  
  private Assignment selectInitalSolution(List<Vehicle> vehicles, TaskSet tasks) {
    if(tasks == null || tasks.isEmpty() || vehicles == null || vehicles.isEmpty() ){
      return null;
    }
    Map<Vehicle, Action> firstAction = new HashMap<Vehicle, Action>();
    Map<Action, Action> nextAction = new HashMap<>();
    Map<Task, Vehicle> tv = new HashMap<>();
    Map<Action, Long> times = new HashMap<>();
    
    // initialize all to null and find vehicle with the biggest capacity.
    double maxC = -1;
    Vehicle maxV = null;
    for(Vehicle v : vehicles){
      firstAction.put(v, null);
      if(v.capacity() > maxC){
        maxC = v.capacity();
        maxV = v;
      }
    }
    // give all tasks to the maxV
    // first make a list out of the taskset
    List<Task> tasksList = new ArrayList<>();
    for(Task t : tasks){
      if(t.weight > maxC){
        // no plan can be created since no vehicle can carry this task
        return null;
      }
      tasksList.add(t);
    }
    
    // add all to the max vehicle
    Task t0 = tasksList.get(0);
    Action firstpickup = new Pickup(t0);
    firstAction.put(maxV, firstpickup);
    Action last = new Deliver(t0);
    nextAction.put(firstpickup, last);
    
    // update the vehicle for the task
    tv.put(t0, maxV);
    long time = 1;
    times.put(firstpickup, time++);
    times.put(last, time++);
    
    for(int i = 1; i < tasksList.size(); i++){
      Task t = tasksList.get(i);
      Action np = new Pickup(t);
      Action nd = new Deliver(t);
      nextAction.put(last, np);
      nextAction.put(np, nd);
      last = nd;
      
      // update the vehicle for the task
      tv.put(t, maxV);
      
      // update times
      times.put(np, time++);
      times.put(nd, time++);
    }
    // put last action -> null
    nextAction.put(last, null);
    
    return new Assignment(firstAction, nextAction, tv, times);
    
    
    
    
  }
  
  private List<Boolean> checkConstraints(List<Variable> variables) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private List<Variable> computeVariables(List<Vehicle> vehicles, TaskSet tasks) {
    // TODO Auto-generated method stub
    return null;
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
