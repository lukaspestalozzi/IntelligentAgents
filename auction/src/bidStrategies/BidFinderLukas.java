package bidStrategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import template.CityTuple;
import template.DistributionTable;

public class BidFinderLukas extends AbstractBidFinder{

  private final DistributionTable dt;
  private CityTuple[] ptasks;
  private CityTuple[] bestTasks;
  private Map<CityTuple, Double> bid  = new HashMap<CityTuple, Double>();

  public BidFinderLukas(List<Vehicle> vehicles, int agent_id, Topology topology,
                        TaskDistribution distribution) {
    super(vehicles, agent_id, topology, distribution);
    dt = new DistributionTable(topology, distribution);
    ptasks = dt.sortedCities;
  }
  
  private void precompute(){
    int max_idx = (int)Math.ceil(ptasks.length*0.3);
    bestTasks = new CityTuple[max_idx];
    int sumWeights = 0;
    for(int i = 0; i < max_idx; i++){
      CityTuple ct = ptasks[i];
      bestTasks[i] = ct;
      sumWeights += ct.proba*dt.weight(ct.from, ct.to);
      bid.put(ct, ct.proba*ct.from.distanceUnitsTo(ct.to));
    }
    
    

    
    
  }

  @Override
  public Long howMuchForThisTask(Task task) {
    for(CityTuple ct : bestTasks){
      if(task.pickupCity.equals(ct.from) && task.deliveryCity.equals(ct.to)){
        // the task is one of the best ones.
        return bid.get(ct);
      }
    }
    return (long)Math.ceil( computeRealCost() * 1.2);
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
