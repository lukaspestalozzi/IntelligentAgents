package bidStrategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enemy_estimation.EnemyBidEstimator;
import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import planning.Assignment;
import planning.ObjFunc;
import planning.PlanFinder;
import template.CityTuple;
import template.DistributionTable;

public class BidFinderLukas extends AbstractBidFinder{
  
  private final int mMaxTasks = 40;
  private double auctionNbr = 1;
  private final DistributionTable dt;
  private CityTuple[] ptasks;
  private CityTuple[] bestTasks;
  private Map<CityTuple, Double> bid  = new HashMap<CityTuple, Double>();
  private EnemyBidEstimator mEnemyEstimator;
  private Agent mAgent;
  private PlanFinder mPlanFinder;
  public Assignment mPlan = null;
  private Assignment mPlanWithNewTask = null;

  public BidFinderLukas(List<Vehicle> vehicles, Agent agent, Topology topology,
                        TaskDistribution distribution) {
    super(vehicles, agent, topology, distribution);
    dt = new DistributionTable(topology, distribution);
    ptasks = dt.sortedCities;
    mEnemyEstimator = new EnemyBidEstimator(agent_id);
    mAgent = agent;
    mPlanFinder = new PlanFinder(agent.vehicles(), 10000, 0.5); // TODO set as parameters
  } 
    

  @Override
  public Long howMuchForThisTask(Task task) {
    
    double p = calcP();
    return Math.round((1-p) * ownBid(task) + p * this.min(mEnemyEstimator.estimateNextBids()));
  }
  
  private Long ownBid(Task t){
    mPlanWithNewTask = mPlanFinder.computeBestPlan(t);
    mPlanWithNewTask.computeCost();
    if(mPlan != null){
      long diff = mPlanWithNewTask.cost - mPlan.cost;
      long lowerBound = Math.round(calcEstimatedCostKm()*0.3);
      if(diff <= lowerBound){
        return lowerBound;
      }else{
        return diff;
      }
      
    }else{
      return mPlanWithNewTask.cost;
    }
  }
  
  /**
   * Calculates the expected cost of a task. Can be used as a soft lower bound to our bids
   * @return the expected cost of any task
   */
  private Double calcEstimatedCostKm(){ // TODO include the weight / capacity
    double sum = 0;
    // average (weighted) distance (in km)
    for(CityTuple ct : ptasks){
      sum += ct.proba * ct.from.distanceTo(ct.to);
    }
    // times the average cost per km of a vehicle
    sum *= calcAvgVehicCost();
    return sum;
  }
  
  /**
   * 
   * @return average vehicle cost / km.
   */
  private double calcAvgVehicCost(){
    double sum = 0;
    for(Vehicle v : mAgent.vehicles()){
      sum += v.costPerKm();
    }
    return sum/mAgent.vehicles().size();
  }
  
  private Long min(Long[] list){
    Long min = Long.MAX_VALUE;
    for(Long l : list){
      if(l < min){
        min = l;
      }
    }
    return min;
  }
  
  /**
   * 
   * @return 1/(auctionsWon+1).
   */
  private double calcP(){
    return 1/(mAuctionsWon.size()+1);
  }
  
  @Override
  public void auctionLost(Task t, Long[] bids) {
    super.auctionLost(t, bids);
  }
  
  @Override
  public void auctionWon(Task t, Long[] bids) {
    super.auctionWon(t, bids);
    mPlan = mPlanWithNewTask;
  }
  

}
