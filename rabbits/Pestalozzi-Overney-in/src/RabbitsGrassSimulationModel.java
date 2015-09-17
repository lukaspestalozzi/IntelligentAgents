import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {	
		private Schedule mSchedule;
		private int mNumAgents;

		public static void main(String[] args) {
			
			System.out.println("Rabbit skeleton");
			
		}

		public void begin() {
			// TODO Auto-generated method stub
			
		}

		public String[] getInitParam() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		public Schedule getSchedule() {
			return mSchedule;
		}
		
		public void buildModel() {
			
		}
		
		public void buildSchedule() {
			
		}
		
		public void buildDisplay() {
			
		}

		public void setup() {
			buildModel();
			buildSchedule();
			buildDisplay();
		}
		
		public int getNumAgents() {
			return mNumAgents;
		}
		
		public void setNumAgents(int na) {
			mNumAgents = na;
		}
}
