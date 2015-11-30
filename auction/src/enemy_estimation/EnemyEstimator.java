package enemy_estimation;

import logist.task.Task;

public interface EnemyEstimator {

	public Long estimateBidForTask(Task t);
	public void auctionResult(Long[] bids, Task t);
}
