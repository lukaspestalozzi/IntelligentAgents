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
import planning.Assignment;
import planning.InsertionAssignment;
import planning.InsertionPlanFinder;
import planning.PlanFinder;

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
	
	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
		
		this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;
		
		mProba = agent.readProperty("sls_proba", Double.class, 0.5);
		mIter = agent.readProperty("amnt_iter", Integer.class, 10000);
		
//		long seed = -9019554669489983951L * currentCity.hashCode() * agent.id();
		this.random = new Random(2015);
		
		String strategy = agent.readProperty("bid-strategy", String.class, "BEST");
		
		mBidFinder = new BidFinderLukas(agent.vehicles(), agent, topology, distribution);
		printIfVerbose("...setup done (agent " + agent.id() + ")");
	}
	
	@Override
	public void auctionResult(Task previous, int winner, Long[] bids) {
		printIfVerbose(String.format("Auction result of Task %s: \n  winner: %d, bids: %s\n", previous.toString(), winner,
		    Arrays.toString(bids)));
		if (winner == agent.id()) {
			mBidFinder.auctionWon(previous, bids);
		} else {
			mBidFinder.auctionLost(previous, bids);
		}
	}
	
	@Override
	public Long askPrice(Task task) {
		printIfVerbose("Task auctioned: " + task.toString()+" -> pathlength: "+task.pathLength());
		Long bid = mBidFinder.howMuchForThisTask(task);
		printIfVerbose("... we bid: " + bid);
		return bid;
	}
	
	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		mBidFinder.mEnemyEstimator.plotBidsVsPrediction(1);
		printIfVerbose("generating the final plan (for " + tasks.size() + " tasks)... ");
//		Assignment a = new PlanFinder(vehicles, 100000, 0.5).computeBestPlan(tasks);
		InsertionAssignment a = mBidFinder.mInsertionPlanFinder.getAssignment();
		if (a == null) {
			List<Plan> pls = new ArrayList<Plan>(vehicles.size());
			for (Vehicle v : vehicles) {
				pls.add(new Plan(v.getCurrentCity()));
			}
			summarize(vehicles, pls, tasks);
			return pls;
		} else {
			List<Plan> p = a.generatePlans(vehicles);
			summarize(vehicles, p, tasks);
			
			return p;
		}
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
