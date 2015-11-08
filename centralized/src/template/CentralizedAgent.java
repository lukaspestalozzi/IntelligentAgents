package template;

import java.util.ArrayList;
import java.util.List;

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
    
    mProba = agent.readProperty("SLS_Proba", double.class, 0.5); // TODO put in xml file
    
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
    
    List<Variable> variables = computeVariables(vehicles, tasks);
    
    ObjFunc objFunc = new ObjFunc();
    Assignment oldA = selectInitalSolution(variables, allConstraints);
    Assignment newA = oldA;
    
    for (int i = 0; i < mIter; i++) {
      PickupSls sls = new PickupSls(oldA, variables, allConstraints, objFunc, mProba);
      newA = sls.updateAssignment();
      oldA = newA;
      if(false/*TODO insert termination condition*/){
        break;
      }
    }
    
    return newA.generatePlans(vehicles);
  }
  
  private Assignment selectInitalSolution(List<Variable> variables,
      List<Constraint> constraints) {
    // TODO Auto-generated method stub
    return null;
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
