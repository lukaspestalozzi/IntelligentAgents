package dummys;

import logist.task.Task;

public class DummyConstantBid extends AbstractDummy {

	protected Long bid = 4000L;

	@Override
	public void auctionRes(Task lastTask, int lastWinner, Long[] lastOffers) {
		return;
		
	}

	@Override
	public Long askBid(Task task) {
		return bid;
	}


}
