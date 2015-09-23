import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
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

  private static final int INIT_NUM_AGENT = 10;
  private static final int INIT_WOLRD_SIZE = 20;
  private static final int TOTAL_GRASS_AMOUNT = 300;

  private int mNumAgents = INIT_NUM_AGENT;
  private int mWorldXSize = INIT_WOLRD_SIZE;
  private int mWorldYSize = INIT_WOLRD_SIZE;
  private int mGrassAmount = TOTAL_GRASS_AMOUNT;

  private Schedule mSchedule;
  private RabbitsGrassSimulationSpace mGrassFieldSpace;

  public static void main(String[] args) {
    SimInit init = new SimInit();
    RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
    init.loadModel(model, "", false);
  }

  public void setup() {
    System.out.println("[+] Running setup");
    mGrassFieldSpace = null;
  }

  public void begin() {
    buildModel();
    buildSchedule();
    buildDisplay();
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
  }

  public void buildSchedule() {
    System.out.println(" ├─ Building schedule");
  }

  public void buildDisplay() {
    System.out.println(" ├─ Building display");
  }

  public String[] getInitParam(){
    String[] initParams = {"NumAgents", "WorldXSize", "WorldYSize", "GrassAmount"};
    return initParams;
  }

  public int getNumAgents() {
    return mNumAgents;
  }

  public void setNumAgents(int na) {
    mNumAgents = na;
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
}
