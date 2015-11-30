package bidStrategies;

import java.util.List;
import java.util.Map;

import enemy_estimation.SingleEnemyEstimator;
import enemy_estimation.SingleEnemyEstimator.EstimateCategory;
import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import planning.Assignment;
import planning.InsertionPlanFinder;
import template.CityTuple;
import template.DistributionTable;

public class BidFinderLukas extends AbstractBidFinder {
	private static final boolean VERBOSE = true;
	
	private final double[] p_array = { 0.9, 0.8, 0.7, 0.6, 0.5, 0.45, 0.4, 0.4, 0.35, 0.3, 0.28, 0.26, 0.24, 0.22, 0.2,
	    0.19, 0.18, 0.17, 0.16, 0.15, 0.14, 0.13, 0.12, 0.11, 0.1, 0.09, 0.08, 0.05 };
			
	private final int mMaxTasks = 40;
	private double auctionNbr = 1;
	private final DistributionTable dt;
	private CityTuple[] ptasks;
	public final SingleEnemyEstimator mEnemyEstimator;
	private Agent mAgent;
	public Assignment mPlan = null;
	private Assignment mPlanWithNewTask = null;
	public final InsertionPlanFinder mInsertionPlanFinder;
	private final long mLowerBound; // TODO make it vary a bit so it is hard to
	                                // predict.
	private final int mMaxCapacity;
	private double mExpectedTaskCost;
	
	public BidFinderLukas(List<Vehicle> vehicles, Agent agent, Topology topology, TaskDistribution distribution,
	    int bid_timeout) {
		super(vehicles, agent, topology, distribution);
		dt = new DistributionTable(topology, distribution);
		ptasks = dt.sortedCities;
		mEnemyEstimator = new SingleEnemyEstimator(1 - agent_id);
		mAgent = agent;
		mExpectedTaskCost = calcExpectedCost();
		
		mInsertionPlanFinder = new InsertionPlanFinder(vehicles, bid_timeout);
		
		mMaxCapacity = findMaxCapacity(agent.vehicles());
		
		mLowerBound = Math.round(mExpectedTaskCost * 0.6);
		
	}
	
	public Long howMuchForThisTask_new(Task task) {
		if (task.weight > mMaxCapacity) {
			// the task is too heavy to be handled by our company.
			return null;
		}
		
		Long bid;
		if (auctionNbr == 1) {
			printIfVerbose("First auction, returning the expected task cost... ");
			double estimate = mExpectedTaskCost;
			bid = Math.round(estimate);// Math.round(estimate * taskLength);
			printIfVerbose(String.format("... bid = %.2f (estimation)", estimate));
		} else {
			double p = calcP();
			Long enemy_estim = mEnemyEstimator.estimateBidForTask(task);
			Long ownBid = Math.max(ownBid_insertionPlan(task), mLowerBound);
			switch (mEnemyEstimator.category) {
			case Extreemly_precise:
				p = Math.min(2*p, 1);
				break;
				
			case Over:
				enemy_estim = Math.round(enemy_estim*0.8);
				break;
				
			case Under:
				// nothing to do
				break;
				
			case Unsure:
				p = p*0.6;
				break;
				
			case NoIdea:
				p = 0;
				break;
				
			default: // should never happen
				break;
			}
			
			if(ownBid == null){
				ownBid = Math.round(mExpectedTaskCost*1.5);
			}else if(enemy_estim == null || p == 0){
				enemy_estim = ownBid;
			}
			
			double ownBidPart = (1 - p) * ownBid;
			double enemyestimPart = p * enemy_estim;
			
			bid = Math.round(ownBidPart + enemyestimPart);
			
			
			
		}
		return bid;
	}
	
	@Override
	public Long howMuchForThisTask(Task task) {
		return howMuchForThisTask_new(task);
		/*
		if (task.weight > mMaxCapacity) {
			// the task is too heavy to be handled by our company.
			return null;
		}
		
		Long bid;
		if (auctionNbr == 1) {
			printIfVerbose("First auction, returning the expected task cost... ");
			double estimate = mExpectedTaskCost;
			bid = Math.round(estimate);// Math.round(estimate * taskLength);
			printIfVerbose(String.format("... bid = %.2f (estimation)", estimate));
		} else {
			double p = calcP();
			Long ownBid = Math.max(ownBid_insertionPlan(task), mLowerBound);
			
			Long enemy_estim = mEnemyEstimator.estimateBidForTask(task);
			
			double ownBidPart = (1 - p) * ownBid;
			double enemyEstimValue = Math.max(enemy_estim, mLowerBound);
			double enemyestimPart = p * enemyEstimValue;
			
			bid = Math.round(ownBidPart + enemyestimPart);
			printIfVerbose(String.format(
			    "final bid = (ownBidPart(%.2f) + enemyEstimPart(%.2f)) \n"
			        + "                                   = ((1-p)*ownBid(%d) + p*enemyEstimation(%.2f)) \n"
			        + "                                   = ((1-p)*ownBid(%d) + p*min_enemy(%d), p = %.2f => %d",
			    ownBidPart, enemyestimPart, ownBid, enemyEstimValue, ownBid, enemy_estim, p, bid));
		}
		printIfVerbose("Returned bid: " + bid);
		return bid;
		*/
	}
	
	private Long ownBid_insertionPlan(Task t) {
		// TODO keep the better solution and put it into insertionPlanfinder.current assignemnt
		long oldCost = mInsertionPlanFinder.getCost();
		long withCost = mInsertionPlanFinder.costWithTaskSls(t);
		
		long diff = withCost - oldCost;
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
		printIfVerbose("proba total: " + tmp);
		printIfVerbose("weighted distance : " + sum);
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
		double p = p_array[Math.min(mAuctionsWon.size(), p_array.length - 1)];
		printIfVerbose("p value: " + p);
		return p;
	}
	
	@Override
	public void auctionLost(Task t, Long[] bids) {
		super.auctionLost(t, bids);
		auctionNbr++;
		this.mEnemyEstimator.auctionResult(bids, t);
	}
	
	@Override
	public void auctionWon(Task t, Long[] bids) {
		super.auctionWon(t, bids);
		auctionNbr++;
		this.mEnemyEstimator.auctionResult(bids, t);
		mPlan = mPlanWithNewTask;
		mInsertionPlanFinder.addTask(t);
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
	public void printIfVerbose(String str) {
		if (VERBOSE) {
			System.out.println(new StringBuilder().append("  ").append("(bid-finder) ").append("agent(").append(mAgent.id())
			    .append("): ").append(str).toString());
			System.out.flush();
		}
	}
}
