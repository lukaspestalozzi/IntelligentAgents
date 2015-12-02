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

public class DummyRepeatCostKm implements AuctionBehavior {
	private double bidkm = 50;
	private int id;
	long reward = 0;

	
	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
		this.id = agent.id();
	}

	@Override
	public Long askPrice(Task task) {
		return Math.round(bidkm*task.pathLength());
	}

	@Override
	public void auctionResult(Task lastTask, int lastWinner, Long[] lastOffers) {
		bidkm = lastOffers[lastWinner]/lastTask.pathLength();
		if(lastWinner == this.id){
			reward += lastOffers[lastWinner];
		}
		
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
		

		long sum = 0;
		int i = 0;
		for(Plan p : plans){
			sum += p.totalDistance()*vehicles.get(i++).costPerKm();
		}
		System.out.println("============> Dummy profit: "+(reward - sum));
		
		
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
