package bidStrategies;

import java.util.ArrayList;
import java.util.List;

import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import planning.Assignment;

public class BidFinder extends AbstractBidFinder{
  
  
  public BidFinder(List<Vehicle> vehicles, Agent agent, Topology topology,
                   TaskDistribution distribution) {
    super(vehicles, agent, topology, distribution);
  }

  @Override
  public Long howMuchForThisTask(Task task) {
    Long currentTasksCost = mCurrentAssignment.cost;
    List<Task> augmentedList = new ArrayList<Task>(mAuctionsWon);
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
