package template;

import java.util.List;
import java.util.Random;

import bidStrategies.BidFinder;
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
public class AuctionAgent implements AuctionBehavior {

  private Topology topology;
  private TaskDistribution distribution;
  private Agent agent;
  private Random random;
  private Vehicle vehicle;
  private City currentCity;
  
  private PlanFinder mPlanFinder;
  private BidFinder mBidFinder;

  @Override
  public void setup(Topology topology, TaskDistribution distribution,
      Agent agent) {

    this.topology = topology;
    this.distribution = distribution;
    this.agent = agent;
    this.vehicle = agent.vehicles().get(0);
    this.currentCity = vehicle.homeCity();

    long seed = -9019554669489983951L * currentCity.hashCode() * agent.id();
    this.random = new Random(seed);
    
    String strategy = agent.readProperty("bid-strategy", String.class, "BEST");
    
    mPlanFinder = new PlanFinder(agent.vehicles()); // TODO replace
    mBidFinder = new BidFinder(agent.vehicles()); // TODO replace
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
    // TODO
    return null;
  }
}
