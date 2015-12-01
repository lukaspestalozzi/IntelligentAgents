package template;

import logist.agent.Agent;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public class DummyMax extends DummyConstantBid{
	
	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
		super.setup(topology, distribution, agent);
		this.bid = Long.MAX_VALUE;
	}

}
