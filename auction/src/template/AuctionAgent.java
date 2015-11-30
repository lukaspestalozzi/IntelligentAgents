package template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import bidStrategies.BidFinderLukas;
import cern.colt.Arrays;
import logist.agent.Agent;
import logist.behavior.AuctionBehavior;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;
import planning.AbstractAssignment;
import planning.Assignment;
import planning.InsertionAssignment;
import planning.InsertionPlanFinder;
import planning.SLSPlanFinder;

/**
 * Our agent
 * 
 */
@SuppressWarnings("unused")
public class AuctionAgent implements AuctionBehavior {
	
	private static final boolean VERBOSE = true;
	
	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
	private Random random;
	private City currentCity;
	
	private double mProba;
	private int mIter;
	
	private BidFinderLukas mBidFinder;
	public  int timeout_setup;
	public  int timeout_plan;
	public  int timeout_bid;
	
	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
		
		this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;
		
		mProba = agent.readProperty("sls_proba", Double.class, 0.5);
		mIter = agent.readProperty("amnt_iter", Integer.class, 10000);
		
//		long seed = -9019554669489983951L * currentCity.hashCode() * agent.id();
		this.random = new Random(2015);
		timeout_plan = agent.readProperty("timeout-plan", Integer.class, 30000);
		timeout_bid = agent.readProperty("timeout-bid", Integer.class, 30000);
		printIfVerbose("Timeout-bid: "+timeout_bid);
		
		mBidFinder = new BidFinderLukas(agent.vehicles(), agent, topology, distribution, timeout_bid);
		printIfVerbose("...setup done (agent " + agent.id() + ")");
	}
	
	@Override
	public void auctionResult(Task previous, int winner, Long[] bids) {
		printIfVerbose(String.format("Auction result of Task %s: \n  winner: %d, bids: %s", previous.toString(), winner,
		    Arrays.toString(bids)));
		if (winner == agent.id()) {
			mBidFinder.auctionWon(previous, bids);
		} else {
			mBidFinder.auctionLost(previous, bids);
		}
	}
	
	@Override
	public Long askPrice(Task task) {
		printIfVerbose("\nTask auctioned: " + task.toString()+" -> pathlength: "+task.pathLength());
		mBidFinder.mInsertionPlanFinder.setTimeout(timeout_bid);
		Long bid = mBidFinder.howMuchForThisTask(task);
		printIfVerbose("... we bid: " + bid);
		return bid;
	}
	
	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		printIfVerbose("generating the final plan (for " + tasks.size() + " tasks)... ");
		mBidFinder.mInsertionPlanFinder.setTimeout(timeout_plan);
		List<Plan> plans = mBidFinder.mInsertionPlanFinder.computeBestPlans(vehicles, tasks);
		summarize(vehicles, plans, tasks);
		return plans;

	}
	
	private void summarize(List<Vehicle> vehicles, List<Plan> plans, TaskSet tasks) {
		printIfVerbose("Final plans: ");
		if (plans.isEmpty()) {
			printIfVerbose("No plans were made!");
		}
		long sumReward = tasks.rewardSum();
		long sumCost = 0;
		for (int i = 0; i < plans.size(); i++) {
			Plan p = plans.get(i);
			Vehicle v = vehicles.get(i);
			sumCost += p.totalDistance() * v.costPerKm();
			printIfVerbose("Plan("+i+"): "+p.toString());
			printIfVerbose("Plan("+i+") length: "+p.totalDistance()+", Vehicle("+i+") cost: "+v.costPerKm()+" => "+p.totalDistance() * v.costPerKm());

		}
		printIfVerbose(String.format("Total cost %d, total reward: %d, total profit: %d", sumCost, sumReward, sumReward-sumCost));
		
	}
	public void printIfVerbose(String str, Object...objects){
		printIfVerbose(String.format(str, objects));
	}
	
	/**
	 * prints s if the VERBOSE flag is set to true: </br>
	 * if(VERBOSE){ System.out.println(s); }
	 * 
	 * @param s
	 */
	public void printIfVerbose(String s) {
		if (VERBOSE) {
			System.out.println("agent " + this.agent.id() + ": " + s);
			System.out.flush();
		}
	}
	
}
