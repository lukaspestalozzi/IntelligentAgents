package enemy_estimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.SwingUtilities;

import logist.task.Task;
import plotting.DrawGraph;

public class AbstractEnemyBidEstimator {
  public static int estimatedNbrBids = 50;
  
	public DrawGraph mainPanel;

  
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
  protected Map<Integer, ArrayList<Double>> prevEstimatesNormalized = new HashMap<Integer, ArrayList<Double>>();
  
  public final int agentID;
  
  private int mNbrAgents = -1;
  
  public AbstractEnemyBidEstimator(int agentID) {
    this.agentID = agentID;
    
    // init the maps
    for(int i = 0; i < estimatedNbrBids; i++){
      prevBids.put(i, new ArrayList<Long>(estimatedNbrBids));
      prevBidsNormalized.put(i, new ArrayList<Double>(estimatedNbrBids));
      prevEstimatesNormalized.put(i, new ArrayList<Double>(estimatedNbrBids));
      
    }
    
  }
  
  public void auctionResult(Long[] bids, Task t){
    mNbrAgents = bids.length;
    
    auctionedTasks.add(t);
    for(int i = 0; i < bids.length; i++){
      Long bid = bids[i];
      prevBids.get(i).add(bid);
      prevBidsNormalized.get(i).add(bid/t.pathLength());
    }
    
  }
  
  public void plotGraph(int enemy){
  	this.plotBidsVsPrediction(enemy);
  }
  
  /**
   * 
   * @return the estimated bids in $/km
   */
  public Map<Integer, Long> estimateNextBids(){
    
    if(mNbrAgents == -1){
      return null;
    }else{
    	Map<Integer, Long> estims = new HashMap<Integer, Long>();
      for(int i = 0; i < mNbrAgents; i++){
      	if(i != agentID){
          estims.put(i, estimateNextBid(i));
      	}
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
//    Double std = stdDeviation(prevBidsNormalized.get(enemy), mean);
//    double median = median(prevBidsNormalized.get(enemy));
    
    long pred = (Long)Math.round(mean);
    prevEstimatesNormalized.get(enemy).add(mean);
    return pred;
  }
  
  private Double median(ArrayList<Double> list){
  	if(list.isEmpty()){
      return null;
    }else if(list.size() == 1){
    	return list.get(0);
    }
  	ArrayList<Double> copy = new ArrayList<Double>(list);
  	copy.sort(null);
  	double middle = (copy.size()-1)/2.0;
  	return copy.get((int)middle); // if middle is no integer then returns the smaller of the two 'middles'
  	
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
  
  public void plotBidsVsPrediction(final int ememy){
  	mainPanel = new DrawGraph(new LinkedList<Double>(prevBidsNormalized.get(ememy)), new LinkedList<Double>(prevEstimatesNormalized.get(ememy)));
  	SwingUtilities.invokeLater(new Runnable() {
      public void run() {
      	mainPanel.showGui();
      }
   });
  }
  
  
}
