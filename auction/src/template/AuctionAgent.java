package template;

import java.util.List;
import java.util.Random;

import logist.agent.Agent;
import logist.behavior.AuctionBehavior;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

import bidStrategies.BidFinder;
import bidStrategies.BidFinderLukas;
import planning.PlanFinder;

/**
 * Our agent
 * 
 */
@SuppressWarnings("unused")
public class AuctionAgent implements AuctionBehavior {

  private Topology topology;
  private TaskDistribution distribution;
  private Agent agent;
  private Random random;
  private Vehicle vehicle;
  private City currentCity;
  
  private double mProba;
  private int mIter;
  
  private BidFinderLukas mBidFinder;

  @Override
  public void setup(Topology topology, TaskDistribution distribution,
      Agent agent) {

    this.topology = topology;
    this.distribution = distribution;
    this.agent = agent;
    this.vehicle = agent.vehicles().get(0);
    this.currentCity = vehicle.homeCity();
    
    mProba = agent.readProperty("sls_proba", Double.class, 0.5);
    mIter = agent.readProperty("amnt_iter", Integer.class, 10000);

    long seed = -9019554669489983951L * currentCity.hashCode() * agent.id();
    this.random = new Random(seed);
    
    String strategy = agent.readProperty("bid-strategy", String.class, "BEST");
    
    mBidFinder = new BidFinderLukas(agent.vehicles(), agent, topology, distribution);
  }

  @Override
  public void auctionResult(Task previous, int winner, Long[] bids) {
    if (winner == agent.id()) {
      mBidFinder.auctionWon(previous, bids);
    }else{
      mBidFinder.auctionLost(previous, bids);
    }
  }
  
  @Override
  public Long askPrice(Task task) {
    return mBidFinder.howMuchForThisTask(task);
  }

  @Override
  public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
    return mBidFinder.mPlan.generatePlans(vehicles);
  }
}
