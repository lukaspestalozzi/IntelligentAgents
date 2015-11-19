package bidStrategies;

import java.util.ArrayList;
import java.util.List;

import logist.simulation.Vehicle;
import logist.task.Task;
import planning.Assignment;
import planning.PlanFinder;

public abstract class AbstractBidFinder {

  protected List<Task> mAuctionsWon;
  protected List<Vehicle> mVehicles;
  protected Assignment mCurrentAssignment;
  protected PlanFinder mPlanFinder;
  protected Long[] mLastBids;
  protected boolean mNeedToUpdateAssignment = true;
  public final int agent_id;
  
  public AbstractBidFinder(List<Vehicle> vehicles, int agent_id) {
    this.agent_id = agent_id;
    mVehicles = vehicles;
    mAuctionsWon = new ArrayList<Task>();
    mCurrentAssignment = null;
    mPlanFinder = new PlanFinder(mVehicles);
    mLastBids = null;
  }
  
  /**
   * 
   * @param task
   * @return the value for the desired reward for the given task
   */
  public abstract Long howMuchForThisTask(Task task);
  
  /**
   * Is called when we've won the auction for task t.
   * @param t the auctioned task
   * @param bids the bids placed. our bid is bids[agent_id] = min(bids)
   */
  public void auctionWon(Task t, Long[] bids){
    mAuctionsWon.add(t);
    mLastBids = bids;
    mNeedToUpdateAssignment = true;
  }
  
  /**
   * Is called when we've lost the auction for task t.
   * @param t the auctioned task
   * @param bids the bids placed. our bid is bids[agent_id]
   */
  public void auctionLost(Task t, Long[] bids){
    mLastBids = bids;
  }
  
  /**
   * 
   * @param newBest
   */
  public void updateBestAssignment(Assignment newBest){
    mCurrentAssignment = newBest;
    mNeedToUpdateAssignment = false;
  }
}
