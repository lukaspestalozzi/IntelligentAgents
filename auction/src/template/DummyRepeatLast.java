package template;

import java.util.ArrayList;
import java.util.List;

import logist.agent.Agent;
import logist.behavior.AuctionBehavior;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class DummyRepeatLast implements AuctionBehavior {
	private long bid = 4000;
	private int id;
	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
		this.id = agent.id();
	}

	@Override
	public Long askPrice(Task task) {
		return bid;
	}

	@Override
	public void auctionResult(Task lastTask, int lastWinner, Long[] lastOffers) {
		bid = lastOffers[lastWinner];
		
	}

	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		List<Plan> plans = new ArrayList<>();
		boolean b = true;
		for(Vehicle v : vehicles){
			if(b){
				b = false;
				plans.add(naivePlan(v, tasks));
			}else{
				plans.add(new Plan(v.getCurrentCity()));
			}
			
		}
		return plans;
	}
	
	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path())
				plan.appendMove(city);

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}
	

}
