package dummys;

import logist.task.Task;

public class DummyRepeatLast extends DummyConstantBid {



	@Override
	public void auctionRes(Task lastTask, int lastWinner, Long[] lastOffers) {
		this.bid = lastOffers[lastWinner];
		
	}

}
