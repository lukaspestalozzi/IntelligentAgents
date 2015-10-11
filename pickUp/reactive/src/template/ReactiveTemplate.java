package template;

import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;


public class ReactiveTemplate implements ReactiveBehavior {

//	private Random random;
//	private double pPickup;
	private ActionTable mActionTable;

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double gamma = agent.readProperty("discount-factor", Double.class,
				0.95);
		
		mActionTable = new ActionTableBuilder(topology.cities(), td).generateActionTable(gamma);
	}
	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
	  return mActionTable.bestAction(vehicle.getCurrentCity(), availableTask);
		

//		if (availableTask == null || random.nextDouble() > pPickup) {
//			City currentCity = vehicle.getCurrentCity();
//			action = new Move(currentCity.randomNeighbor(random));
//		} else {
//			action = new Pickup(availableTask);
//		}
//		return action;
	}
}
