package enemy_estimation;

import java.util.ArrayList;

import logist.task.Task;

public class SingleEnemyEstimator {
	public enum EstimateCategory {
		Extreemly_precise, Under, Over, Unsure, NoIdea
	};
	
	public static int estimatedNbrBids = 50;
	
	protected ArrayList<Task> auctionedTasks = new ArrayList<Task>(estimatedNbrBids);
	/**
	 * contains the previous bids (total amount) for each enemy (and our own).
	 */
	protected ArrayList<Long> prevBids = new ArrayList<Long>(estimatedNbrBids);
	/**
	 * contains the previous bids in $/km
	 */
	protected ArrayList<Double> prevBidsPerKm = new ArrayList<Double>(estimatedNbrBids);
	/**
	 * contains our previous estimates (can be used to estimate error)
	 */
	protected ArrayList<Long> prevEstimates = new ArrayList<Long>(estimatedNbrBids);
	
	public EstimateCategory category = EstimateCategory.NoIdea;
	
	public final int enemyID;
	
	public SingleEnemyEstimator(int enemyID) {
		this.enemyID = enemyID;
	}
	
	public void auctionResult(Long[] bids, Task t) {
		
		auctionedTasks.add(t);
		prevBids.add(bids[enemyID]);
		prevBidsPerKm.add(bids[enemyID] / t.pathLength());
		
	}
	
	public Long estimateBidForTask(Task t){
		Long pred = this.estimateBidFor(t);
		prevEstimates.add(pred);
		return pred;
	}
	
	private Long estimateBidFor(Task t) {
		
		if (auctionedTasks.isEmpty()) {
			this.category = EstimateCategory.NoIdea;
			return null;
		}
		
		Double meanPerKm = mean(prevBidsPerKm);
		Double medianPerKm = median(prevBidsPerKm);
		
		Double meanAbs = mean(prevBids);
		Double medianAbs = median(prevBids);
		
		if (meanAbs == null || meanPerKm == null || medianAbs == null || medianPerKm == null) {
			this.category = EstimateCategory.NoIdea;
			return null;
		}
		
		Double stdPerKm = stdDeviation(prevBidsPerKm, meanPerKm);
		Double stdAbs = stdDeviation(prevBids, meanAbs);
		
		if (stdPerKm == null || stdAbs == null) {
			this.category = EstimateCategory.Unsure;
			return Math.round(medianPerKm * t.pathLength());
			
		}
		
		Double stdNormPerKm = (100.0 / meanPerKm) * stdPerKm;
		Double stdNormAbs = (100.0 / meanAbs) * stdAbs;
		
		// check if std is very small (only after the 5th task)
		if (auctionedTasks.size() >= 5) {
			if (stdNormPerKm < 5) {
				this.category = EstimateCategory.Extreemly_precise;
				return Math.round((meanPerKm - stdPerKm) * t.pathLength());
			} else if (stdNormAbs < 5) {
				this.category = EstimateCategory.Extreemly_precise;
				return Math.round((meanAbs - stdAbs) * t.pathLength());
			}
		}
		
		// return the value with the smaller std		
		if(stdNormPerKm < stdNormAbs){
			this.category = EstimateCategory.Under;
			// min of median and mean of bid/km * pathlenght
			return Math.round(Math.min(meanPerKm, medianPerKm)*t.pathLength());
		}else{
			this.category = EstimateCategory.Under;
			// min of median and mean of absolute bid
			return Math.round(Math.min(meanAbs, medianAbs));
		}
		
		
	}
	
	private Double median(ArrayList<? extends Number> list) {
		if (list.isEmpty()) {
			return null;
		} else if (list.size() == 1) { return list.get(0).doubleValue(); }
		ArrayList<Number> copy = new ArrayList<Number>(list);
		copy.sort(null);
		double middle = (copy.size() - 1) / 2.0;
		return copy.get((int) middle).doubleValue(); // if middle is no integer then
		                                             // returns the smaller of the
		                                             // two 'middles'
		
	}
	
	private Double mean(ArrayList<? extends Number> list) {
		if (list.isEmpty()) { return null; }
		double sum = 0;
		for (Number d : list) {
			sum += d.doubleValue();
		}
		return sum / list.size();
	}
	
	private Double stdDeviation(ArrayList<? extends Number> arrayList, Double mean) {
		if (mean == null) { return null; }
		double sum = 0.0;
		for (Number d : arrayList) {
			sum += Math.pow(d.doubleValue() - mean, 2);
		}
		return Math.sqrt(sum / arrayList.size());
	}
	
}
