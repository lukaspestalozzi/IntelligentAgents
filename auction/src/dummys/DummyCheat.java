package dummys;

import logist.task.Task;

public class DummyCheat extends AbstractDummy{
	
	int counter = 0;
	long reward = 0;

	@Override
	public Long askBid(Task task) {
		if(counter++ < 2){
			return Math.round(Long.MIN_VALUE*0.8);
		}
		return null;
	}
	
	@Override
	public void auctionRes(Task lastTask, int lastWinner, Long[] lastOffers) {
		if(lastWinner == this.agent.id()){
			reward += lastOffers[lastWinner];
		}
	}
}
