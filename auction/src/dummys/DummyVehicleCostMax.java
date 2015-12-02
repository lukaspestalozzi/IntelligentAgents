package dummys;

import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public class DummyVehicleCostMax extends DummyConstantCostKm{
	
	@Override
	public void setupsub(Topology topology, TaskDistribution distribution, Agent agent) {
		super.setupsub(topology, distribution, agent);
		this.bidkm = Double.MIN_VALUE;
		for(Vehicle v : agent.vehicles()){
			if(v.costPerKm() > this.bidkm){
				this.bidkm = v.costPerKm();
			}
		}
		this.bidkm *= 1.3;
	}
}