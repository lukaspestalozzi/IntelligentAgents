package dummys;

import logist.task.Task;

public class DummyRepeatCostKm extends AbstractDummy {
	private double bidkm = 50;


	@Override
	public Long askBid(Task task) {
		return Math.round(bidkm*task.pathLength());
	}

	@Override
	public void auctionRes(Task lastTask, int lastWinner, Long[] lastOffers) {
		bidkm = lastOffers[lastWinner]/lastTask.pathLength();
		
	}
}
