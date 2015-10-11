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
import logist.topology.Topology.City;


public class ReactiveRandom implements ReactiveBehavior {

  private Random random = new Random(2015);
  private double pPickup;

  @Override
  public void setup(Topology topology, TaskDistribution td, Agent agent) {

    // Reads the discount factor from the agents.xml file.
    // If the property is not present it defaults to 0.95
    Double gamma = agent.readProperty("discount-factor", Double.class,
        0.95);
    pPickup = gamma;
  }
  @Override
  public Action act(Vehicle vehicle, Task availableTask) {
    
Action action;
    if (availableTask == null || random.nextDouble() > pPickup) {
      City currentCity = vehicle.getCurrentCity();
      action = new Move(currentCity.randomNeighbor(random));
    } else {
      action = new Pickup(availableTask);
    }
    return action;
  }
}
