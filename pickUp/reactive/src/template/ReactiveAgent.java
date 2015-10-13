package template;

import java.util.HashMap;

import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public class ReactiveAgent implements ReactiveBehavior {
  
  private HashMap<Integer, ActionTable> mActionTables;
  // this variable keeps reference of the Agent object
  Agent agent;
  // this variable counts how many actions have passed so far
  int counterSteps = 0;
  
  @Override
  public void setup(Topology topology, TaskDistribution td, Agent agent) {
    this.agent = agent;
    // Reads the discount factor from the agents.xml file.
    // If the property is not present it defaults to 0.85
    Double gamma = agent.readProperty("discount-factor", Double.class, 0.85);
    mActionTables = new HashMap<>();
    ActionTableBuilder tableBuilder = new ActionTableBuilder(topology.cities(), td);
    for (Vehicle vehicle : agent.vehicles()) {
    	mActionTables.put(vehicle.costPerKm(), tableBuilder.generateActionTable(gamma, vehicle));
    }
  }
  
  @Override
  public Action act(Vehicle vehicle, Task availableTask) {
    // this output gives information about the "goodness" of your agent (higher
    // values are preferred)
    if ((counterSteps > 0) && (counterSteps % 100 == 0)) {
      System.out.println("REACTIVE AGENT: ");
      System.out.println("The total profit after " + counterSteps + " steps is "
          + agent.getTotalProfit() + ".");
      System.out.println("The profit per action after " + counterSteps + " steps is "
          + ((double) agent.getTotalProfit() / counterSteps) + ".");
      System.out.println();
    }
    counterSteps++;
    
    Action best = mActionTables.get(vehicle.costPerKm()).bestAction(vehicle.getCurrentCity(), availableTask);
    // System.out.println(best.toLongString());
    return best;
    
  }
}
