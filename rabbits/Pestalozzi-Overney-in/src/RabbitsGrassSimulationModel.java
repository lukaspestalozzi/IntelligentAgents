import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.util.SimUtilities;

/**
 * Class that implements the simulation model for the rabbits grass simulation.
 * This is the first class which needs to be setup in order to run Repast
 * simulation. It manages the entire RePast environment and the simulation.
 *
 * @author
 */

public class RabbitsGrassSimulationModel extends SimModelImpl {

	private static final int INIT_NUM_RABBITS = 10;
	private static final int INIT_WOLRD_SIZE = 20;
	private static final int TOTAL_GRASS_AMOUNT = 300;
	private static final int BIRTH_THRESHOLD = 50;
	private static final int INIT_ENERGY = 10;
	private static final int ENERGY_LOST_MOVING = 1;

	private int mNumRabbits = INIT_NUM_RABBITS;
	private int mWorldXSize = INIT_WOLRD_SIZE;
	private int mWorldYSize = INIT_WOLRD_SIZE;
	private int mGrassAmount = TOTAL_GRASS_AMOUNT;
	private int mBirthThreshold = BIRTH_THRESHOLD;
	private int mStartEnergy = INIT_ENERGY;
	private int mEnergyLostMoving = ENERGY_LOST_MOVING;

	private Schedule mSchedule;
	private RabbitsGrassSimulationSpace mGrassFieldSpace;
	private ArrayList<RabbitsGrassSimulationAgent> mAgentList;
	private DisplaySurface mTiles;

	public static void main(String[] args) {
		SimInit init = new SimInit();
		RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
		init.loadModel(model, "", false);
	}

	public void setup() {
		System.out.println("[+] Running setup");
		mGrassFieldSpace = null;
		mAgentList = new ArrayList<RabbitsGrassSimulationAgent>();
		mSchedule = new Schedule(1);

		if (mTiles != null) {
			mTiles.dispose();
		}
		mTiles = null;

		mTiles = new DisplaySurface(this, "Rabbit Grass Simulation Window 1");
		registerDisplaySurface("Rabbit Grass Simulation Window 1", mTiles);
	}

	public void begin() {
		buildModel();
		buildSchedule();
		buildDisplay();

		mTiles.display();
	}

	public String getName() {
		return "Hey it's me, Little Rabbit";
	}

	public Schedule getSchedule() {
		return mSchedule;
	}

	public void buildModel() {
		System.out.println(" ├─ Building model");
		mGrassFieldSpace = new RabbitsGrassSimulationSpace(mWorldXSize, mWorldYSize);
		mGrassFieldSpace.spreadGrass(mGrassAmount);

		for (int i = 0; i < mNumRabbits; i++) {
			addNewAgent();
		}

		for (RabbitsGrassSimulationAgent rabbit : mAgentList) {
			rabbit.report();
		}
	}

	public void buildSchedule() {
		System.out.println(" ├─ Building schedule");

		class RabbitStep extends BasicAction {
			public void execute() {
				SimUtilities.shuffle(mAgentList);
				for (int i = 0; i < mAgentList.size(); i++) {
					RabbitsGrassSimulationAgent agent = mAgentList.get(i);
					int x = agent.getX();
					int y = agent.getY();
					
					Random r = new Random();
					int[] dirs = {-1, 1};
					int direction = dirs[r.nextInt(2)];
					
					int newX = x;
					int newY = y;
					boolean isVarX = r.nextBoolean();
					if(isVarX){
						newX = x+direction;
						if(!mGrassFieldSpace.isInField(newX, newY)){
							newX = x-direction;
						}
					}else{
						newY = y+direction;
						if(!mGrassFieldSpace.isInField(newX, newY)){
							newY = y-direction;
						}
					}
					
					mGrassFieldSpace.moveAgent(x, y, newX, newY);
					agent.looseEnergy(mEnergyLostMoving);
					agent.report();
				}
				
				mTiles.updateDisplay();
			}
		}

		mSchedule.scheduleActionAtInterval(1, new RabbitStep());

	}

	public void buildDisplay() {
		System.out.println(" ├─ Building display");

		ColorMap colorMap = new ColorMap();

		for (int i = 1; i < 16; i++) {
			colorMap.mapColor(i, new Color(0, i * 8 + 127, 0));
		}
		colorMap.mapColor(0, Color.white);

		Value2DDisplay displayGrass = new Value2DDisplay(mGrassFieldSpace.getCurrentGrassField(), colorMap);

		Object2DDisplay displayAgents = new Object2DDisplay(mGrassFieldSpace.getCurrentGrassField());
		displayAgents.setObjectList(mAgentList);

		mTiles.addDisplayable(displayGrass, "Grass");
		mTiles.addDisplayable(displayAgents, "Agents");
	}

	private void addNewAgent() {
		RabbitsGrassSimulationAgent newAgent = new RabbitsGrassSimulationAgent(mStartEnergy, mBirthThreshold);
		mAgentList.add(newAgent);
		mGrassFieldSpace.addAgent(newAgent);
	}

	public String[] getInitParam() {
		String[] initParams = { "NumAgents", "WorldXSize", "WorldYSize", "StartEnergy", "BirthThreshold",
				"GrassAmount" , "EnergyLostMoving"};
		return initParams;
	}

	public int getNumAgents() {
		return mNumRabbits;
	}

	public void setNumAgents(int na) {
		mNumRabbits = na;
	}

	public int getWorldXSize() {
		return mWorldXSize;
	}

	public void setWorldXSize(int xSize) {
		mWorldXSize = xSize;
	}

	public int getWorldYSize() {
		return mWorldYSize;
	}

	public void setWorldYSize(int ySize) {
		mWorldYSize = ySize;
	}

	public int getGrassAmount() {
		return mGrassAmount;
	}

	public void setGrassAmount(int grassAmount) {
		mGrassAmount = grassAmount;
	}

	public int getBirthThreshold() {
		return mBirthThreshold;
	}

	public void setBirthThreshold(int birthThreshold) {
		this.mBirthThreshold = birthThreshold;
	}

	public int getStartEnergy() {
		return mStartEnergy;
	}

	public void setStartEnergy(int startEnergy) {
		this.mStartEnergy = startEnergy;
	}
	
	public int getEnergyLostMoving() {
		return this.mEnergyLostMoving;
	}
	
	public void setEnergyLostMoving(int energyLostMoving) {
		this.mEnergyLostMoving = energyLostMoving;
	}
	
}
