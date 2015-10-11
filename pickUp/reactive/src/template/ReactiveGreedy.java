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

  @Override
  public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Action act(Vehicle vehicle, Task availableTask) {
    return availableTask == null ? new Move(vehicle.getCurrentCity().neighbors().get(r.nextInt(vehicle.getCurrentCity().neighbors().size()))) : new Pickup(availableTask);
  }

}
