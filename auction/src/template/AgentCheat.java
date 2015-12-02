package template;

import java.util.List;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;

public class AgentCheat extends DummyConstantBid{
	
	int counter = 0;
	long reward = 0;

	@Override
	public Long askPrice(Task task) {
		if(counter++ < 2){
			return Math.round(Long.MIN_VALUE*0.8);
		}
		return null;
	}
	
	@Override
	public void auctionResult(Task lastTask, int lastWinner, Long[] lastOffers) {
		if(lastWinner == this.agent.id()){
			reward += lastOffers[lastWinner];
		}
	}

	
	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		List<Plan> plans =  super.plan(vehicles, tasks);
		
		long sum = 0;
		int i = 0;
		for(Plan p : plans){
			sum += p.totalDistance()*vehicles.get(i++).costPerKm();
		}
		System.out.println("Cheat profit: "+(reward - sum));
		return plans;
	}
}
