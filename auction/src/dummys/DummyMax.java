package dummys;

import logist.agent.Agent;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public class DummyMax extends DummyConstantBid{
	
	@Override
	public void setupsub(Topology topology, TaskDistribution distribution, Agent agent) {
		super.setupsub(topology, distribution, agent);
		this.bid = Long.MAX_VALUE;
	}
}
