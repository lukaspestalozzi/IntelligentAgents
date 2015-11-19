package template;

import java.util.ArrayList;
import java.util.List;

import planning.Assignment;
import planning.PlanFinder;
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
  
  public Long howMuchForThisTask(Task task) {
    Long currentTasksCost = mCurrentAssignment.cost;
    List<Task> augmentedList = new ArrayList<>(mAuctionsWon);
    augmentedList.add(task);
    Assignment bestAssWithNewTask = mPlanFinder.computeBestPlan(augmentedList);
    Long bestWithNewCost = bestAssWithNewTask.cost;
    return secretStrategyWithSuperBiddingPowers(bestWithNewCost - currentTasksCost);
  }
  
  private Long secretStrategyWithSuperBiddingPowers(Long costForTask) {
    //TODO black magic shit
    return 2 * costForTask;
  }
}
