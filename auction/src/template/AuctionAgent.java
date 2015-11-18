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
  }

  @Override
  public void auctionResult(Task previous, int winner, Long[] bids) {
    if (winner == agent.id()) {
      //TODO
    }
  }
  
  @Override
  public Long askPrice(Task task) {
    // TODO

//    if (vehicle.capacity() < task.weight){
//      return null;
//  }
//
//    long distanceTask = task.pickupCity.distanceUnitsTo(task.deliveryCity);
//    long distanceSum = distanceTask
//        + currentCity.distanceUnitsTo(task.pickupCity);
//    double marginalCost = Measures.unitsToKM(distanceSum
//        * vehicle.costPerKm());
//
//    double ratio = 1.0 + (random.nextDouble() * 0.05 * task.id);
//    double bid = ratio * marginalCost;

    return null;//(long) Math.round(bid);
  }

  @Override
  public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
    // TODO
    return null;
  }
}
