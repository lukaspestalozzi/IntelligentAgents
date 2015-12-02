package dummys;

import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public class DummyVehicleCostMin extends DummyConstantCostKm{
	
	@Override
	public void setupsub(Topology topology, TaskDistribution distribution, Agent agent) {
		super.setupsub(topology, distribution, agent);
		this.bidkm = Double.MAX_VALUE;
		for(Vehicle v : agent.vehicles()){
			if(v.costPerKm() < this.bidkm){
				this.bidkm = v.costPerKm();
			}
		}
	}

}
