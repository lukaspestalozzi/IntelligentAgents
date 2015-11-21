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
import template.CityTuple;
import template.DistributionTable;

public class BidFinderLukas extends AbstractBidFinder{
  
  private final int mMaxTasks = 40;
  private double auctionNbr = 1;
  private final DistributionTable dt;
  private CityTuple[] ptasks;
  private CityTuple[] bestTasks;
  private Map<CityTuple, Double> bid  = new HashMap<CityTuple, Double>();
  private EnemyBidEstimator ebe;
  private Agent mAgent;

  public BidFinderLukas(List<Vehicle> vehicles, Agent agent, Topology topology,
                        TaskDistribution distribution) {
    super(vehicles, agent, topology, distribution);
    dt = new DistributionTable(topology, distribution);
    ptasks = dt.sortedCities;
    ebe = new EnemyBidEstimator(agent_id);
    mAgent = agent;
  }
  
  private void precompute(){
    int nbrTuples = (int)Math.ceil(ptasks.length*0.3);
    bestTasks = dt.getMostProbable(nbrTuples);
    int sumWeights = 0;
    for(int i = 0; i < nbrTuples; i++){
      CityTuple ct = ptasks[i];
      sumWeights += ct.proba*dt.weight(ct.from, ct.to);
      bid.put(ct, ct.proba*ct.from.distanceUnitsTo(ct.to));
    }
    
    
    
    
  }

  @Override
  public Long howMuchForThisTask(Task task) {
    double p = calcP();
    return Math.round((1-p) * ownBid(task) + p * this.min(ebe.estimateNextBids()));
  }
  
  private Long ownBid(Task t){
    // TODO
  }
  
  private Double calcEstimatedCostKm(){
    double sum = 0;
    for(CityTuple ct : ptasks){
      sum += ct.proba * ct.from.distanceTo(ct.to); // TODO not yet correct (is not yet cost per km)
    }
    sum *= calcAvgVehicCost();
    return sum;
  }
  
  
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
   * @return 1/auctionNbr.
   */
  private double calcP(){
    return 1/auctionNbr;
  }
  
  @Override
  public void auctionLost(Task t, Long[] bids) {
    super.auctionLost(t, bids);
  }
  
  @Override
  public void auctionWon(Task t, Long[] bids) {
    super.auctionWon(t, bids);
  }
  

}
