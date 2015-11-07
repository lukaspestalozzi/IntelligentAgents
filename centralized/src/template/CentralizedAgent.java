package template;

//the list of imports
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logist.LogistSettings;
import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.behavior.CentralizedBehavior;
import logist.agent.Agent;
import logist.config.Parsers;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
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
    
    @Override
    public void setup(Topology topology, TaskDistribution distribution,
            Agent agent) {
        
        // this code is used to get the timeouts
        LogistSettings ls = null;
        try {
            ls = Parsers.parseSettings("config\\settings_default.xml");
        }
        catch (Exception exc) {
            System.out.println("There was a problem loading the configuration file.");
        }
        
        // the setup method cannot last more than timeout_setup milliseconds
        mTimeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
        // the plan method cannot execute more than timeout_plan milliseconds
        mTimeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);
        
        mIter = agent.readProperty("amnt_iter", int.class, 10000);
        
        this.mTopology = topology;
        this.mDistribution = distribution;
        this.mAgent = agent;
    }

    @Override
    public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
        long time_start = System.currentTimeMillis();
        
//		System.out.println("Agent " + agent.id() + " has tasks " + tasks);

        List<Plan> plans = slsPlans(vehicles, tasks);
        
        long time_end = System.currentTimeMillis();
        long duration = time_end - time_start;
        System.out.println("The plan was generated in "+duration+" milliseconds.");
        
        return plans;
    }
    
    private List<Plan> slsPlans(List<Vehicle> vehicles, TaskSet tasks) {
      // TODO generate the Variables, constraint etc.
      List<Variable> variables = computeVariables(vehicles, tasks);
      List<Boolean> constraints = checkConstraints(variables);
      ObjFunc objFunc = null;
      Assignment oldA = selectInitalSolution(variables, constraints);
      Assignment newA = oldA;
      for(int i = 0; i < mIter; i++) {
        PickupSls sls = new PickupSls(oldA, variables, constraints, objFunc);
        newA = sls.updateAssignment();
        oldA = newA;
      }
      return  newA.generatePlans();
    }

    private Assignment selectInitalSolution(List<Variable> variables,
        List<Boolean> constraints) {
      // TODO Auto-generated method stub
      return null;
    }

    private List<Boolean> checkConstraints(List<Variable> variables) {
      // TODO Auto-generated method stub
      return null;
    }

    private List<Variable> computeVariables(List<Vehicle> vehicles,
        TaskSet tasks) {
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
