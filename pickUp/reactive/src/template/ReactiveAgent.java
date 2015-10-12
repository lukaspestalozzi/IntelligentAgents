package template;

import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public class ReactiveAgent implements ReactiveBehavior {
  
  private ActionTable mActionTable;
  // this variable keeps reference of the Agent object
  Agent agent;
  // this variable counts how many actions have passed so far
  int counterSteps = 0;
  
  @Override
  public void setup(Topology topology, TaskDistribution td, Agent agent) {
    this.agent = agent;
    // Reads the discount factor from the agents.xml file.
    // If the property is not present it defaults to 0.95
    Double gamma = agent.readProperty("discount-factor", Double.class, 0.95);
    
    mActionTable = new ActionTableBuilder(topology.cities(), td)
        .generateActionTable(gamma);
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
    
    Action best = mActionTable.bestAction(vehicle.getCurrentCity(), availableTask);
    // System.out.println(best.toLongString());
    return best;
    
  }
}
