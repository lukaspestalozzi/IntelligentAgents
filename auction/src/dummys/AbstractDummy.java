package dummys;

import java.util.ArrayList;
import java.util.List;

import logist.LogistPlatform;
import logist.LogistSettings;
import logist.agent.Agent;
import logist.behavior.AuctionBehavior;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;
import planning.Assignment;
import planning.SLSPlanFinder;

public abstract class AbstractDummy  implements AuctionBehavior {

	protected long timeout_plan;
	protected long timeout_bid;
	protected Agent agent;
	final ArrayList<Long> myBids = new ArrayList<>();
	private Long reward = 0L;
	
	@Override
	public final void setup(Topology topology, TaskDistribution distribution, Agent agent) {
		this.agent = agent;
		this.timeout_bid = LogistPlatform.getSettings().get(LogistSettings.TimeoutKey.BID);
		this.timeout_plan = LogistPlatform.getSettings().get(LogistSettings.TimeoutKey.PLAN);
		
		myprint("Timeout read: %d", timeout_plan);
		
		setupsub(topology, distribution, agent);
	}
	
	public void setupsub(Topology topology, TaskDistribution distribution, Agent agent){}

	@Override
	public final List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		List<Plan> plans = makePlan(vehicles, tasks);
		summarize(vehicles, plans);
		return plans;
	}
	
	public List<Plan> makePlan(List<Vehicle> vehicles, TaskSet tasks) {
		return slsPlans(vehicles, tasks);
	}
	
	@Override
	public final void auctionResult(Task lastTask, int lastWinner, Long[] lastOffers) {
		if(lastWinner == this.agent.id()){
			this.reward += lastTask.reward;
		}
		auctionRes(lastTask, lastWinner, lastOffers);
	}
	
	public abstract void auctionRes(Task lastTask, int lastWinner, Long[] lastOffers);
	

	@Override
	public final Long askPrice(Task task) {
		
		Long bid = askBid(task);
		myBids.add(bid);
		myprint("%d) bid: "+ bid, task.id);
		return bid;
	}
	
	public abstract Long askBid(Task task);
	
	protected final List<Plan> naivePlans(List<Vehicle> vehicles, TaskSet tasks) {
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
	
	protected final Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
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
	
	protected final List<Plan> slsPlans(List<Vehicle> vehicles, TaskSet tasks){
//		if(tasks.isEmpty()){
//			return new Assignment(vehicles).generatePlans(vehicles);
//		}
		SLSPlanFinder slsp = new SLSPlanFinder(vehicles, 100000, 0.5, this.timeout_plan - 2000);
		Assignment a = slsp.computeBestPlan(new ArrayList<Task>(tasks));
		return a.generatePlans(vehicles);
	}
	
	protected final void summarize(List<Vehicle> vehicles, List<Plan> plans){
		myprint("MyBids %s", myBids.toString());
		myprint("Profit: %d", reward - cost(vehicles, plans));
	}
	
	public final long cost(List<Vehicle> vehicles, List<Plan> plans){
		long sum = 0;
		int i = 0;
		for(Plan p : plans){
			sum += p.totalDistance()*vehicles.get(i++).costPerKm();
		}
		return sum;
	}
	
	public void myprint(String str, Object...objects){
		myprint(String.format(str, objects));
	}
	
	
	
	/**
	 * prints s if the VERBOSE flag is set to true: </br>
	 * if(VERBOSE){ System.out.println(s); }
	 * 
	 * @param s
	 */
	public void myprint(String str) {
			System.out.println(new StringBuilder()
					.append("    ")
					.append("(agent) ("+this.agent.id()+"): ")
					.append(str)
					.toString());
			System.out.flush();
		
	}

}
