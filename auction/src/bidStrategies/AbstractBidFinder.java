package bidStrategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;

public abstract class AbstractBidFinder {

  private static final Comparator<Vehicle> CAPACITY_COMPARATOR = ((v1,
      v2) -> ((Integer) v1.capacity()).compareTo(v2.capacity()));

  protected List<Task> mAuctionsWon;
  protected List<Vehicle> mVehicles;
  protected Long[] mLastBids;
  public final int agent_id;

  protected final Topology mTopology;
  protected final TaskDistribution mDistribution;

  public AbstractBidFinder(List<Vehicle> vehicles, Agent agent,
      Topology topology, TaskDistribution distribution) {
    this.agent_id = agent.id();
    mVehicles = vehicles;
    mAuctionsWon = new ArrayList<Task>();
    mLastBids = null;
    mTopology = topology;
    mDistribution = distribution;
  }

  /**
   * 
   * @param task
   * @return the value for the desired reward for the given task
   */
  public abstract Long howMuchForThisTask(Task task);

  /**
   * Is called when we've won the auction for task t. IMPORTANT: If overriding
   * this method then first call the super method!
   * 
   * @param t
   *          the auctioned task
   * @param bids
   *          the bids placed. our bid is bids[agent_id] = min(bids)
   */
  public void auctionWon(Task t, Long[] bids) {
    mAuctionsWon.add(t);
    mLastBids = bids;
  }

  /**
   * Is called when we've lost the auction for task t. IMPORTANT: If overriding
   * this method then first call the super method!
   * 
   * @param t
   *          the auctioned task
   * @param bids
   *          the bids placed. our bid is bids[agent_id]
   */
  public void auctionLost(Task t, Long[] bids) {
    mLastBids = bids;
  }

  protected int findMaxCapacity(List<Vehicle> vehics) {
    if (vehics.isEmpty())
      return 0;
    return vehics.stream().max(CAPACITY_COMPARATOR).get().capacity();
  }
}
