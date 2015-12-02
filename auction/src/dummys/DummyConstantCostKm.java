package dummys;

import logist.task.Task;

public class DummyConstantCostKm extends AbstractDummy  {
	protected double bidkm = 20;

	@Override
	public void auctionRes(Task lastTask, int lastWinner, Long[] lastOffers) {
		return;
		
	}

	@Override
	public Long askBid(Task task) {
		return Math.round(bidkm*task.pathLength());
	}
	
	
}
