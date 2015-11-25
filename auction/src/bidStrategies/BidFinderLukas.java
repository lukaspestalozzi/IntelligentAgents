package bidStrategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cern.colt.Arrays;
import enemy_estimation.EnemyBidEstimator;
import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import planning.Assignment;
import planning.PlanFinder;
import template.CityTuple;
import template.DistributionTable;

public class BidFinderLukas extends AbstractBidFinder {
	private static final boolean VERBOSE = true;
	
	private final int mMaxTasks = 40;
	private double auctionNbr = 1;
	private final DistributionTable dt;
	private CityTuple[] ptasks;
	private CityTuple[] bestTasks;
	private Map<CityTuple, Double> bid = new HashMap<CityTuple, Double>();
	private EnemyBidEstimator mEnemyEstimator;
	private Agent mAgent;
	private PlanFinder mPlanFinder;
	public Assignment mPlan = null;
	private Assignment mPlanWithNewTask = null;
	
	public BidFinderLukas(List<Vehicle> vehicles, Agent agent, Topology topology, TaskDistribution distribution) {
		super(vehicles, agent, topology, distribution);
		dt = new DistributionTable(topology, distribution);
		ptasks = dt.sortedCities;
		mEnemyEstimator = new EnemyBidEstimator(agent_id);
		mAgent = agent;
		mPlanFinder = new PlanFinder(agent.vehicles(), 10000, 0.5); // TODO set as
		                                                            // parameters
	}
	
	@Override
	public Long howMuchForThisTask(Task task) {
		// TODO only in first acution return estimate
		// in later auctions, use enemyestimator even if no auctions were won
		
		// TODO fix estimated cost/km
		// TODO fix bid if (mAuctionsWon.size() == 0)
		
		Long bid;
		if (mAuctionsWon.size() == 0) {
			printIfVerbose("No previous auctions won therefore calculating an estimated bid... ");
			double estimate = calcEstimatedCostKm();
			double taskLength = task.pathLength();
			bid = Math.round(estimate * taskLength);
			printIfVerbose(String.format("... bid = %.2f * %.2f = %d (estimation * task length)", estimate, taskLength, bid));
		} else {
			double p = calcP();
			Long ownBid = ownBid(task);
			Long[] enemy_estim = mEnemyEstimator.estimateNextBids();
			printIfVerbose(String.format("Own bid: %d , enemy estimation: %s", ownBid, Arrays.toString(enemy_estim)));
			
			bid = Math.round((1 - p) * ownBid + p * this.min(enemy_estim));
		}
		printIfVerbose("Returned bid: " + bid);
		return bid;
	}
	
	private Long ownBid(Task t) {
		mPlanWithNewTask = mPlanFinder.computeBestPlan(t);
		mPlanWithNewTask.computeCost();
		if (mPlan != null) {
			long diff = mPlanWithNewTask.cost - mPlan.cost;
			long lowerBound = Math.round(calcEstimatedCostKm() * 0.3);
			if (diff <= lowerBound) {
				return lowerBound;
			} else {
				return diff;
			}
			
		} else {
			return mPlanWithNewTask.cost;
		}
	}
	
	/**
	 * Calculates the expected cost of a task. Can be used as a soft lower bound
	 * to our bids
	 * 
	 * @return the expected cost of any task
	 */
	private Double calcEstimatedCostKm() { // TODO include the weight / capacity
		double sum = 0;
		// average (weighted) distance (in km)
		double tmp = 0;
		for (CityTuple ct : ptasks) {
			sum += ct.proba * ct.from.distanceTo(ct.to);
			tmp += ct.proba;
		}
		printIfVerbose("proba total: "+tmp);
		printIfVerbose("weighted distance : "+sum);
		// times the average cost per km of a vehicle
		sum *= calcAvgVehicCost();
		printIfVerbose("Expected cost of a task: " + sum);
		return sum;
	}
	
	/**
	 * 
	 * @return average vehicle cost / km.
	 */
	private double calcAvgVehicCost() {
		double sum = 0;
		for (Vehicle v : mAgent.vehicles()) {
			sum += v.costPerKm();
		}
		double res = sum / mAgent.vehicles().size();
		printIfVerbose("Average cost per km (vehicle): " + res);
		return res;
	}
	
	private Long min(Long[] list) {
		Long min = Long.MAX_VALUE;
		for (Long l : list) {
			if (l < min) {
				min = l;
			}
		}
		return min;
	}
	
	/**
	 * 
	 * @return 1/(auctionsWon+1).
	 */
	private double calcP() {
		double p = 1 / (mAuctionsWon.size() + 1);
		printIfVerbose("p value: " + p);
		return p;
	}
	
	@Override
	public void auctionLost(Task t, Long[] bids) {
		super.auctionLost(t, bids);
	}
	
	@Override
	public void auctionWon(Task t, Long[] bids) {
		super.auctionWon(t, bids);
		mPlan = mPlanWithNewTask;
	}
	
	/**
	 * prints s if the VERBOSE flag is set to true: </br>
	 * if(VERBOSE){ System.out.println(s); }
	 * 
	 * @param s
	 */
	public void printIfVerbose(String str) {
		if (VERBOSE) {
			System.out.println(new StringBuilder()
					.append("  ")
					.append("(bid-finder) ")
					.append("agent(")
					.append(mAgent.id())
					.append("): ")
					.append(str)
					.toString());
			System.out.flush();
		}
	}
	
}
