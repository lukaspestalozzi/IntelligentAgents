package dummys;

import java.util.ArrayList;

import logist.agent.Agent;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import planning.Assignment;
import planning.SLSPlanFinder;

public class DummyGreedySls extends AbstractDummy {

	private SLSPlanFinder planer;
	private Assignment bestPlan;
	private Assignment planWithT;
	private ArrayList<Task> wonT = new ArrayList<>();
	
	@Override
	public void setupsub(Topology topology, TaskDistribution distribution, Agent agent) {
		super.setupsub(topology, distribution, agent);
		bestPlan = new Assignment(agent.vehicles());
		bestPlan.computeCost();
		planer = new SLSPlanFinder(agent.vehicles(), 50000, 0.5, this.timeout_bid-5000);
	}
	
	@Override
	public void auctionRes(Task lastTask, int lastWinner, Long[] lastOffers) {
		if(lastWinner == this.agent.id()){
			bestPlan = planWithT;
			wonT.add(lastTask);
			bestPlan.replace(lastTask);
		}
	}

	@Override
	public Long askBid(Task task) {
		planWithT = planer.computeBestPlan(wonT, task);
		return  Math.round(Math.max((planWithT.computeCost() - bestPlan.cost)*1.2, 700));
	}
	

}
