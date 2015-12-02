package enemy_estimation;

import java.util.ArrayList;
import static enemy_estimation.EstimateCategory.*;
import logist.task.Task;

public class SingleEnemyEstimator implements EnemyEstimator {
	
	
	private static final boolean VERBOSE = true;
	
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
	
	private int maxlongcounter = 0; // counts how many long.max values were bid
	private int nullcounter = 0; // counts how many null values were bid.
	
	public EstimateCategory category = NoIdea;
	
	public final int enemyID;
	
	public SingleEnemyEstimator(int enemyID) {
		this.enemyID = enemyID;
	}
	
	public void auctionResult(Long[] bids, Task t) {
		if(bids.length == 1){
			return;
		}
		auctionedTasks.add(t);
		
		if(bids[enemyID] == null){
			nullcounter++;
			printIfVerbose("nullcounter: ", nullcounter);
			return;
		}
		if(bids[enemyID] > Long.MAX_VALUE - 21){
			maxlongcounter++;
			printIfVerbose("maxcounter: "+maxlongcounter);
			return;
		}
		
		prevBids.add(bids[enemyID]);
		prevBidsPerKm.add(bids[enemyID] / t.pathLength());
		
	}
	
	public Long estimateBidForTask(Task t){
		Long pred = this.estimateBidFor(t);
		prevEstimates.add(pred);
		if(pred == null){
			this.category = NoIdea;
		}
		return pred;
	}
	
	private Long estimateBidFor(Task t) {
		
		if (auctionedTasks.isEmpty()) {
			this.category = NoIdea;
			printIfVerbose("No baseline");
			return null;
		}
		
		// check if enemy bids a lot null or Long.max value
		if(nullcounter + maxlongcounter > 6){
			printIfVerbose("A lot of null or max values were bid (%d, %d).", nullcounter, maxlongcounter);
			if(((double)(nullcounter + maxlongcounter)/(double)auctionedTasks.size()) > 0.8){ // 80% is considered as a lot
				this.category = Extreemly_precise;
				return Long.MAX_VALUE - 21;
			}
		}
		
		
		Double meanPerKm = mean(take10D(prevBidsPerKm));
		Double medianPerKm = median(take10D(prevBidsPerKm));
		
		Double meanAbs = mean(take10L(prevBids));
		Double medianAbs = median(take10L(prevBids));
		
		if (meanAbs == null || meanPerKm == null || medianAbs == null || medianPerKm == null) {
			this.category = NoIdea;
			return null;
		}
		
		Double stdPerKm = stdDeviation(take10D(prevBidsPerKm), meanPerKm);
		Double stdAbs = stdDeviation(take10L(prevBids), meanAbs);
		
		if (stdPerKm == null || stdAbs == null) {
			this.category = Unsure;
			return Math.round(medianPerKm * t.pathLength());
			
		}
		
		Double stdNormPerKm = (100.0 / meanPerKm) * stdPerKm;
		Double stdNormAbs = (100.0 / meanAbs) * stdAbs;
		
		printIfVerbose("meanPerKm: %.2f, medianPerKm: %.2f, meanAbs: %.2f, medianAbs: %.2f, stdPerKm: %.2f, stdAbs: %.2f, stdNormPerKm: %.2f, stdNormAbs: %.2f", 
				meanPerKm, medianPerKm, meanAbs ,medianAbs, stdPerKm, stdAbs, stdNormPerKm, stdNormAbs);
		
		// check if std is very small (only after the 5th task)
		if (auctionedTasks.size() >= 5) {
			if (stdNormPerKm < 5) {
				printIfVerbose("extremly low std...");
				printIfVerbose("returning per km.");
				this.category = Extreemly_precise;
				return Math.round((meanPerKm - stdPerKm) * t.pathLength());
			} else if (stdNormAbs < 5) {
				printIfVerbose("extremly low std...");
				printIfVerbose("returning abs.");
				this.category = Extreemly_precise;
				return Math.round((meanAbs - stdAbs));
			}
		}
		
		// return the value with the smaller std		
		if(stdNormPerKm < stdNormAbs){
			this.category = stdNormPerKm < 40 ? Under : Unsure;
			// min of median and mean of bid/km * pathlenght
			printIfVerbose("returning per km.");
			return Math.round((Math.min(meanPerKm, medianPerKm)- stdPerKm)*t.pathLength());
		}else{
			this.category = stdNormAbs < 40 ? Under : Unsure;
			// min of median and mean of absolute bid
			printIfVerbose("returning abs.");
			return Math.round(Math.min(meanAbs, medianAbs) - stdAbs);
		}
	}
	
	private ArrayList<Double> take10D(ArrayList<Double> list){
		if(list.size() <= 10){
			return list;
		}
		ArrayList<Double> l = new ArrayList<>();
		for(int i = list.size() - 10; i < list.size(); i++){
			l.add(list.get(i));
		}
		return l;
		
	}
	
	private ArrayList<Long> take10L(ArrayList<Long> list){
		if(list.size() <= 10){
			return list;
		}
		ArrayList<Long> l = new ArrayList<>();
		for(int i = list.size() - 10; i < list.size(); i++){
			l.add(list.get(i));
		}
		return l;
		
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
	
	public void summarize(){
		if(prevBids.isEmpty() || auctionedTasks.isEmpty()){
			return;
		}
		ArrayList<Long> diffs = new ArrayList<>(prevBids.size()+1);
		diffs.add(prevBids.get(0));
		for(int i = 0; i < prevEstimates.size(); i++){
			diffs.add((prevBids.get(i+1) - prevEstimates.get(i)));
		}
		
		
		System.out.println("========================== Enemy Estimator summary: ");
		System.out.println(auctionedTasks.toString());
		System.out.println("Bids:  "+prevBids.toString());
		System.out.println("Estimates:   "+prevEstimates.toString());
		System.out.println("Diff: "+diffs.toString());
		System.out.println("======================================================");
	}
	
	public void printIfVerbose(String str, Object... objects) {
		printIfVerbose(String.format(str, objects));
	}
	
	/**
	 * prints s if the VERBOSE flag is set to true: </br>
	 * if(VERBOSE){ System.out.println(s); }
	 * 
	 * @param s
	 */
	public void printIfVerbose(String str) {
		if (VERBOSE) {
			System.out.println(new StringBuilder()
					.append("    ")
					.append("(enemy-estimator) ")
					.append("enemy(")
					.append(enemyID)
			    .append("): ")
			    .append(str)
			    .toString());
			System.out.flush();
		}
	}
	
}
