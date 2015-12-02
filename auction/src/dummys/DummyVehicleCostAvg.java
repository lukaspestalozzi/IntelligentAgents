package dummys;

import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public class DummyVehicleCostAvg extends DummyConstantCostKm{
	
	@Override
	public void setupsub(Topology topology, TaskDistribution distribution, Agent agent) {
		super.setupsub(topology, distribution, agent);
		for(Vehicle v : agent.vehicles()){
			this.bidkm += v.costPerKm();
		}
		bidkm /= agent.vehicles().size();
		
		
	}

}
