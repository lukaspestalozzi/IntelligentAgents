package enemy_estimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import logist.task.Task;

public class AbstractEnemyBidEstimator {
  public static int estimatedNbrBids = 100;
  
  protected ArrayList<Task> auctionedTasks = new ArrayList<Task>(estimatedNbrBids);
  /**
   * contains the previous bids (total amount) for each enemy (and our own).
   */
  protected Map<Integer, ArrayList<Long>> prevBids = new HashMap<Integer, ArrayList<Long>>();
  /**
   * contains the previous bids in $/km
   */
  protected Map<Integer, ArrayList<Double>> prevBidsNormalized = new HashMap<Integer, ArrayList<Double>>();
  /**
   * contains our previous estimates (can be used to estimate error)
   */
  protected Map<Integer, ArrayList<Long>> prevEstimates = new HashMap<Integer, ArrayList<Long>>();
  
  public final int agentID;
  
  private int mNbrEnemies = -1;
  
  public AbstractEnemyBidEstimator(int agentID) {
    this.agentID = agentID;
    
    // init the maps
    for(int i = 0; i < estimatedNbrBids; i++){
      prevBids.put(i, new ArrayList<Long>(estimatedNbrBids));
      prevBidsNormalized.put(i, new ArrayList<Double>(estimatedNbrBids));
      prevEstimates.put(i, new ArrayList<Long>(estimatedNbrBids));
      
    }
    
  }
  
  public void auctionResult(Long[] bids, Task t){
    mNbrEnemies = bids.length-1;
    
    auctionedTasks.add(t);
    for(int i = 0; i < bids.length; i++){
      Long bid = bids[i];
      prevBids.get(i).add(bid);
      prevBidsNormalized.get(i).add(bid/t.pathLength());
    }
  }
  
  public Long[] estimateNextBids(){
    
    if(mNbrEnemies == -1){
      return null;
    }else{
      Long[] estims = new Long[mNbrEnemies];
      for(int i = 0; i <= mNbrEnemies; i++){
          estims[i] = estimateNextBid(i);
        
      }
      return estims;
    }
  }
  
  private Long estimateNextBid(int enemy){
    if(enemy == agentID){
      return null;
    }
    // mean of $/km
    if(prevBidsNormalized.get(enemy).size() == 0){
      return null;
    }
    Double mean = mean(prevBidsNormalized.get(enemy));
    Double std = stdDeviation(prevBidsNormalized.get(enemy), mean);
    
    return (Long)Math.round(mean - std);
  }
  
  private Long mean(ArrayList<Long> list){
    if(list.isEmpty()){
      return null;
    }
    long sum = 0;
    for(Long l : list){
      sum += l;
    }
    return sum/list.size();
  }
  
  private Double mean(ArrayList<Double> list){
    if(list.isEmpty()){
      return null;
    }
    double sum = 0;
    for(Double d : list){
      sum += d;
    }
    return sum/list.size();
  }
  
  private Double stdDeviation(ArrayList<Double> arrayList, Double mean){
    if(mean == null){return null;}
    long sum = 0;
    for(Double d : arrayList){
      sum += Math.pow(d-mean, 2);
    }
    return Math.sqrt(sum/arrayList.size());
  }
  
  
}
