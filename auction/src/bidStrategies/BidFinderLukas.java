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
import planning.InsertionPlanFinder;
import planning.PlanFinder;
import template.CityTuple;
import template.DistributionTable;

public class BidFinderLukas extends AbstractBidFinder {
	private static final boolean VERBOSE = true;
	
	private final double[] p_array = {0.9, 0.8, 0.7, 0.6, 0.5, 0.45, 0.4, 0.4, 0.35, 0.3, 0.28, 0.26, 0.24, 0.22, 0.2, 0.19, 0.18, 0.17, 0.16, 0.15, 0.14, 0.13, 0.12, 0.11, 0.1, 0.09, 0.08, 0.05};
	
	private final int mMaxTasks = 40;
	private double auctionNbr = 1;
	private final DistributionTable dt;
	private CityTuple[] ptasks;
	private CityTuple[] bestTasks;
	private Map<CityTuple, Double> bid = new HashMap<CityTuple, Double>();
	public final EnemyBidEstimator mEnemyEstimator;
	private Agent mAgent;
	private PlanFinder mPlanFinder;
	public Assignment mPlan = null;
	private Assignment mPlanWithNewTask = null;
	public final InsertionPlanFinder mInsertionPlanFinder;
	private final long mLowerBound; // TODO make it vary a bit so it is hard to predict.
	private final int mMaxCapacity;
	
	public BidFinderLukas(List<Vehicle> vehicles, Agent agent, Topology topology, TaskDistribution distribution) {
		super(vehicles, agent, topology, distribution);
		dt = new DistributionTable(topology, distribution);
		ptasks = dt.sortedCities;
		mEnemyEstimator = new EnemyBidEstimator(agent_id);
		mAgent = agent;
		mMaxCapacity = findMaxCapacity(agent.vehicles());
		mPlanFinder = new PlanFinder(agent.vehicles(), 50000, 0.5); // TODO set as
		                                                            // parameters
		mInsertionPlanFinder  = new InsertionPlanFinder(vehicles);
		mLowerBound = Math.round(calcExpectedCost() * 0.5);
	}
	
	@Override
	public Long howMuchForThisTask(Task task) {
		// TODO only in first acution return estimate
		// in later auctions, use enemyestimator even if no auctions were won
		
		if(task.weight > mMaxCapacity) { 
		  // the task is too heavy to be handled by our company.
		  return null;
		}
		
		Long bid;
		if (mAuctionsWon.size() == 0) {
			printIfVerbose("No previous auctions won therefore calculating an estimated bid... ");
			double estimate = calcExpectedCost();
			bid = Math.round(estimate);//Math.round(estimate * taskLength);
			printIfVerbose(String.format("... bid = %.2f (estimation)", estimate));
		} else {
			double p = calcP();
			Long ownBid = Math.max(ownBid_insertionPlan(task), mLowerBound);
			
			Map<Integer, Long> enemy_estim = mEnemyEstimator.estimateNextBids();
			Long min_enemy = this.min(enemy_estim);
			
			double ownBidPart = (1 - p) * ownBid;
			double enemyEstimValue = Math.max(min_enemy *task.pathLength(), mLowerBound);
			double enemyestimPart = p * enemyEstimValue;
			
			bid = Math.round(ownBidPart + enemyestimPart);
			printIfVerbose(String.format("final bid = (ownBidPart(%.2f) + enemyEstimPart(%.2f)) \n"
					+ "                                   = ((1-p)*ownBid(%d) + p*enemyEstimation(%.2f)) \n"
					+ "                                   = ((1-p)*ownBid(%d) + p*min_enemy(%d)*tasklength(%.2f)), p = %.2f => %d",
					ownBidPart, enemyestimPart, ownBid, enemyEstimValue, ownBid, min_enemy, task.pathLength(), p, bid));
}
		printIfVerbose("Returned bid: " + bid);
		return bid;
	}
	
	private Long ownBid(Task t) {
		mPlanWithNewTask = mPlanFinder.computeBestPlan(mAuctionsWon, t);
		mPlanWithNewTask.computeCost();
		if (mPlan != null) {
			long diff = mPlanWithNewTask.cost - mPlan.cost;
			printIfVerbose("Plan cost with new Task: %d, cost without: %d -> difference: %d", mPlanWithNewTask.cost, mPlan.cost, diff);
			
			return diff;
		} else {
			return mPlanWithNewTask.cost;
		}
	}
	
	private Long ownBid_insertionPlan(Task t) {
		// TODO make testing much more efficient!
		long oldCost = mInsertionPlanFinder.getCost();
		long withCost = mInsertionPlanFinder.costWithTask(t);
		
		long diff = withCost-oldCost;
		printIfVerbose("Plan cost with new Task: %d, cost without: %d -> difference: %d.", withCost, oldCost, diff);

		return diff;
		
	}
	
	/**
	 * Calculates the expected cost of a task. Can be used as a soft lower bound
	 * to our bids
	 * 
	 * @return the expected cost of any task
	 */
	private Double calcExpectedCost() { // TODO include the weight / capacity
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
	
	/**
	 * @param map
	 * @return the min of the maps value set.
	 */
	private Long min(Map<Integer, Long> map) {
		Long min = Long.MAX_VALUE;
		for (Long l : map.values()) {
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
//		double p = 1.0 / (double)(mAuctionsWon.size() + 1);
		double p = p_array[Math.min(mAuctionsWon.size(), p_array.length-1)];
		printIfVerbose("p value: " + p);
		return p;
	}
	
	@Override
	public void auctionLost(Task t, Long[] bids) {
		super.auctionLost(t, bids);
		this.mEnemyEstimator.auctionResult(bids, t);
	}
	
	@Override
	public void auctionWon(Task t, Long[] bids) {
		super.auctionWon(t, bids);
		this.mEnemyEstimator.auctionResult(bids, t);
		mPlan = mPlanWithNewTask;
		mInsertionPlanFinder.addTask(t);
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
