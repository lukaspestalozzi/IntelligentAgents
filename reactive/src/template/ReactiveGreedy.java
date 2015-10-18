package template;

import java.util.Random;

import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public class ReactiveGreedy implements ReactiveBehavior {
  Random r = new Random();
  // this variable keeps reference of the Agent object
  Agent agent;
  // this variable counts how many actions have passed so far
  int counterSteps = 0;
  
  @Override
  public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
    
    this.agent = agent;
  }
  
  @Override
  public Action act(Vehicle vehicle, Task availableTask) {
    // this output gives information about the "goodness" of your agent (higher
    // values are preferred)
    if ((counterSteps > 0) && (counterSteps % 100 == 0)) {
      System.out.println("REACTIVE GREEDY: ");
      System.out.println("The total profit after " + counterSteps + " steps is "
          + agent.getTotalProfit() + ".");
      System.out.println("The profit per action after " + counterSteps + " steps is "
          + ((double) agent.getTotalProfit() / counterSteps) + ".");
      System.out.println();
    }
    counterSteps++;
    
    return availableTask == null
        ? new Move(vehicle.getCurrentCity().neighbors()
            .get(r.nextInt(vehicle.getCurrentCity().neighbors().size())))
        : new Pickup(availableTask);
  }
  
}
