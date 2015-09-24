import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;

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
  private static final int AGENT_MIN_LIFESPAN = 30;
  private static final int AGENT_MAX_LIFESPAN = 50;

  private int mNumAgents = INIT_NUM_AGENT;
  private int mWorldXSize = INIT_WOLRD_SIZE;
  private int mWorldYSize = INIT_WOLRD_SIZE;
  private int mGrassAmount = TOTAL_GRASS_AMOUNT;
  private int mAgentMinLifespan = AGENT_MIN_LIFESPAN;
  private int mAgentMaxLifespan = AGENT_MAX_LIFESPAN;

  private Schedule mSchedule;
  private RabbitsGrassSimulationSpace mGrassFieldSpace;
  private ArrayList mAgentList;
  private DisplaySurface mTiles;

  public static void main(String[] args) {
    SimInit init = new SimInit();
    RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
    init.loadModel(model, "", false);
  }

  public void setup() {
    System.out.println("[+] Running setup");
    mGrassFieldSpace = null;
    mAgentList = new ArrayList();

    if(mTiles != null) {
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

    for (int i = 0; i < numAgents; i++) {
      addNewAgent();
    }
  }

  public void buildSchedule() {
    System.out.println(" ├─ Building schedule");
  }

  public void buildDisplay() {
    System.out.println(" ├─ Building display");

    ColorMap colorMap = new ColorMap();

    for (int i = 1; i < 16; i++) {
      colorMap.mapColor(i, new Color(0, i * 8 + 127, 0));
    }
    colorMap.mapColor(0, Color.white);

    Value2DDisplay displayGrass =
      new Value2DDisplay(mGrassFieldSpace.getCurrentGrassField(), colorMap);

    mTiles.addDisplayable(displayGrass, "Grass");
  }

  private void addNewAgent() {
    RabbitsGrassSimulationAgent newAgent = 
      new RabbitsGrassSimulationAgent(mAgentMinLifespan, mAgentMaxLifespan);
    mAgentList.add(newAgent);
    mGrassFieldSpace.addAgent(newAgent);
  }

  public String[] getInitParam(){
    String[] initParams = {"NumAgents", "WorldXSize", "WorldYSize",
      "AgentMinLifespan", "AgentMaxLifespan", "GrassAmount"};
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

  public int getAgentMinLifespan() {
    return mAgentMinLifespan;
  }

  public void setAgentMinLifespan(int i) {
    mAgentMinLifespan = i;
  }

  public int getAgentMaxLifespan() {
    return mAgentMaxLifespan;
  }

  public void setAgentMaxLifespan(int i) {
    mAgentMaxLifespan = i;
  }
}
