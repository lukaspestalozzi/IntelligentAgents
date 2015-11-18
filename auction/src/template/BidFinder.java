package template;

import java.util.ArrayList;
import java.util.List;

import logist.simulation.Vehicle;
import logist.task.Task;

public class BidFinder {
  
  private List<Task> mAuctionsWon;
  private List<Vehicle> mVehicles;
  private Assignment mCurrentAssignment;
  private PlanFinder mPlanFinder;
  
  public BidFinder(List<Vehicle> vehicles) {
    mVehicles = vehicles;
    mAuctionsWon = new ArrayList<>();
    mCurrentAssignment = null;
    mPlanFinder = new PlanFinder(mVehicles);
  }
  
  public Integer howMuchForThisTask(Task task) {
    Integer currentTasksCost = mCurrentAssignment.cost;
    List<Task> augmentedList = new ArrayList<>(mAuctionsWon);
    augmentedList.add(task);
    Assignment bestAssWithNewTask = mPlanFinder.computeBestPlan(augmentedList);
    Integer bestWithNewCost = bestAssWithNewTask.cost;
    return secretStrategyWithSuperBiddingPowers(bestWithNewCost - currentTasksCost);
  }
  
  private Integer secretStrategyWithSuperBiddingPowers(Integer costForTask) {
    //TODO black magic shit
    return 2 * costForTask;
  }
}
