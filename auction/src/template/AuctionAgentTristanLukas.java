package template;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bidStrategies.BidFinderSls;
import cern.colt.Arrays;
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

/**
 * Our agent
 * 
 */
@SuppressWarnings("unused")
public class AuctionAgentTristanLukas implements AuctionBehavior {
	
	private static boolean VERBOSE = false;
	private static boolean SUMMARY = true;
	
	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
	private Random random;
	private City currentCity;
	
	private double mProba;
	private int mIter;
	
	private BidFinderSls mBidFinder;
	public int timeout_setup;
	public long timeout_plan;
	public long timeout_bid;
	
	private Task prevTask = null;
	
	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
		
		this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;
		
		mProba = agent.readProperty("sls_proba", Double.class, 0.5);
		mIter = agent.readProperty("amnt_iter", Integer.class, 10000);
		
		// long seed = -9019554669489983951L * currentCity.hashCode() * agent.id();
		this.random = new Random(2015);
		this.timeout_bid = LogistPlatform.getSettings().get(LogistSettings.TimeoutKey.BID);
		this.timeout_plan = LogistPlatform.getSettings().get(LogistSettings.TimeoutKey.PLAN);
		
		this.timeout_bid = Math.round(timeout_bid * 0.9);
		this.timeout_plan = Math.round(timeout_plan * 0.9);
		
		printIfVerbose("Timeout-plan: " + timeout_plan);
		printIfVerbose("Timeout-bid: " + timeout_bid);
		
		mBidFinder = new BidFinderSls(agent.vehicles(), agent, topology, distribution, timeout_bid);
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
		try {
			prevTask = task;
			printIfVerbose("==========================================================================================");
			printIfVerbose("\nTask auctioned: " + task.toString() + " -> pathlength: " + task.pathLength());
			mBidFinder.mSLSPlanFinder.setTimeout(timeout_bid);
			Long bid = mBidFinder.howMuchForThisTask(task);
			printIfVerbose("... we bid: " + bid);
			return bid;
		} catch (Exception e) {
			int maxcapacity = agent.vehicles().stream().max(((v1, v2) -> ((Integer) v1.capacity()).compareTo(v2.capacity())))
			    .get().capacity();
			return maxcapacity < task.weight ? null : Long.MAX_VALUE;
		}
	}
	
	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		printIfVerbose("generating the final plan (for " + tasks.size() + " tasks)... ");
		mBidFinder.mSLSPlanFinder.setTimeout((long) (timeout_plan * 0.9));
		List<Plan> plans = mBidFinder.mSLSPlanFinder.computeBestPlan(mBidFinder.mPlan, new ArrayList<Task>(tasks))
		    .generatePlans(vehicles);
		if (SUMMARY) {
			summarize(vehicles, plans, tasks);
		}
		return plans;
		
	}
	
	private void summarize(List<Vehicle> vehicles, List<Plan> plans, TaskSet tasks) {
		boolean temp = VERBOSE;
		VERBOSE = SUMMARY;
		try {
			printIfVerbose("Final plans: ");
			if (plans.isEmpty()) {
				printIfVerbose("No plans were made!");
			}
			
			mBidFinder.summarize();
			
			long sumReward = tasks.rewardSum();
			long sumCost = 0;
			for (int i = 0; i < plans.size(); i++) {
				Plan p = plans.get(i);
				Vehicle v = vehicles.get(i);
				sumCost += p.totalDistance() * v.costPerKm();
				printIfVerbose("Plan(" + i + "): " + p.toString());
				printIfVerbose("Plan(" + i + ") length: " + p.totalDistance() + ", Vehicle(" + i + ") cost: " + v.costPerKm()
				    + " => " + p.totalDistance() * v.costPerKm());
						
			}
			printIfVerbose(
			    String.format("Total cost %d, total reward: %d, total profit: %d", sumCost, sumReward, sumReward - sumCost));
		} catch (Exception e) {
			System.out.println("Error in summarize: "+e.getMessage());
		} finally {
			VERBOSE = temp;
		}
		
	}
	
	public void printIfVerbose(String str, Object... objects) {
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
