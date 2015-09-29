import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.reflector.RangePropertyDescriptor;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
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

public class RabbitsGrassSimulationModel
    extends SimModelImpl {
    
  private static final int INIT_NUM_RABBITS = 1;
  private static final int INIT_WOLRD_SIZE = 20;
  private static final int INIT_TOTAL_GRASS_AMOUNT = 7;
  private static final int INIT_Reproduce_THRESHOLD = 50;
  private static final int INIT_ENERGY = 49;
  private static final int INIT_ENERGY_LOST_MOVING = 1;
  private static final int INIT_MAX_GRASS_EATING = 2;
  private static final int INIT_ENERGY_PER_GRASS = 5;
  private static final int INIT_REPRODUCE_COST = INIT_Reproduce_THRESHOLD
      / 2;
      
  private int mNumRabbits = INIT_NUM_RABBITS;
  private int mWorldXSize = INIT_WOLRD_SIZE;
  private int mWorldYSize = INIT_WOLRD_SIZE;
  private int mGrassAmount = INIT_TOTAL_GRASS_AMOUNT;
  private int mReproduceThreshold = INIT_Reproduce_THRESHOLD;
  private int mStartEnergy = INIT_ENERGY;
  private int mEnergyLostMoving = INIT_ENERGY_LOST_MOVING;
  private int mMaxGrassEating = INIT_MAX_GRASS_EATING;
  private int mEnergyPerGRass = INIT_ENERGY_PER_GRASS;
  private int mReproduceCost = INIT_REPRODUCE_COST;
  
  private Schedule mSchedule;
  private RabbitsGrassSimulationSpace mGrassFieldSpace;
  private ArrayList<RabbitsGrassSimulationAgent> mAgentList;
  private DisplaySurface mTiles;
  
  private OpenSequenceGraph mStatisticsGraph;

  private class NbrRabbits implements DataSource, Sequence {

    @Override
    public Object execute() {
      return new Double(getSValue());
    }

    @Override
    public double getSValue() {
      return (double)mAgentList.size();
    }
  }
  
  private class GrassAmount implements DataSource, Sequence {
	  public Object execute() {
		  return new Double(getSValue());
	  }
	  
	  public double getSValue() {
		  return (double)mGrassAmount;
	  }
  }
  
  public static void main(String[] args) {
    SimInit init = new SimInit();
    RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
    init.loadModel(model, "", false);
  }
  
  @Override
  public void setup() {
    System.out.println("[+] Running setup");
    mGrassFieldSpace = null;
    mAgentList = new ArrayList<RabbitsGrassSimulationAgent>();
    mSchedule = new Schedule(1);
    
    // dispose displays
    if (mTiles != null) {
      mTiles.dispose();
    }
    mTiles = null;
    
    if (mStatisticsGraph != null){
      mStatisticsGraph.dispose();
    }
    mStatisticsGraph = null;
    
    // Create Displays
    mTiles = new DisplaySurface(this,
        "Rabbit Grass Simulation Window 1");
    mStatisticsGraph = new OpenSequenceGraph("Amount Of Things in our world",this);

    
 // register Displays
    registerDisplaySurface(
        "Rabbit Grass Simulation Window 1", mTiles);
    this.registerMediaProducer("Plot", mStatisticsGraph);
    
    // Add sliders
    RangePropertyDescriptor sliderInitRabbits = new RangePropertyDescriptor("NumRabbits", 0, mWorldXSize*mWorldYSize, 5);
    RangePropertyDescriptor sliderGrassGrowth = new RangePropertyDescriptor("GrassAmount", 0, 500, 1);
    RangePropertyDescriptor sliderReproduceThreshold = new RangePropertyDescriptor("ReproduceThreshold", 0, 500, 5);
    descriptors.put("NumRabbits", sliderInitRabbits);
    descriptors.put("GrassAmount", sliderGrassGrowth);
    descriptors.put("ReproduceThreshold", sliderReproduceThreshold);    
  }
  
  @Override
  public void begin() {
    buildModel();
    buildSchedule();
    buildDisplay();
    
    mTiles.display();
    mStatisticsGraph.display();
  }
  
  @Override
  public String getName() {
    return "Hey it's me, Little Rabbit";
  }
  
  @Override
  public Schedule getSchedule() {
    return mSchedule;
  }
  
  public void buildModel() {
    System.out.println(" ├─ Building model");
    mGrassFieldSpace = new RabbitsGrassSimulationSpace(
        mWorldXSize, mWorldYSize);
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
      @Override
      public void execute() {
        SimUtilities.shuffle(mAgentList);
        ArrayList<RabbitsGrassSimulationAgent> babies = new ArrayList<>();
        for (int i = 0; i < mAgentList.size(); i++) {
          RabbitsGrassSimulationAgent agent = mAgentList
              .get(i);
          // move
          agent.move(mWorldXSize, mWorldYSize);
          agent.looseEnergy(mEnergyLostMoving);
          
          // eat
          agent.eat(mMaxGrassEating, mEnergyPerGRass);
          
          // reproduce
          RabbitsGrassSimulationAgent baby = agent
              .reproduce(mReproduceThreshold, mStartEnergy,
                  mReproduceCost);
          if (baby != null) {
            babies.add(baby);
          }
          
          // die
          if (agent.hasToDie()) {
            mGrassFieldSpace.removeAgent(agent);
            mAgentList.remove(i);
          }
          
        }
        for (RabbitsGrassSimulationAgent baby : babies) {
          mAgentList.add(baby);
        }
        System.out.println("Total nbr of living rabbits: "+mAgentList.size());
        mTiles.updateDisplay();
      }
    }
    
    class GrowGrassAction extends BasicAction {
      @Override
      public void execute() {
        mGrassFieldSpace.spreadGrass(mGrassAmount);
      }
    }
    
    class UpdateNbrRabbits extends BasicAction {
      @Override
      public void execute(){
        mStatisticsGraph.step();
      }
    }
    
    mSchedule.scheduleActionAtInterval(1, new RabbitStep());
    mSchedule.scheduleActionAtInterval(1,
        new GrowGrassAction());
    mSchedule.scheduleActionAtInterval(1, new UpdateNbrRabbits());
        
  }
  
  public void buildDisplay() {
    System.out.println(" ├─ Building display");
    
    ColorMap colorMap = new ColorMap();
    
    for (int i = 1; i < 16; i++) {
      colorMap.mapColor(i, new Color(0, i * 8 + 127, 0));
    }
    colorMap.mapColor(0, Color.white);
    
    Value2DDisplay displayGrass = new Value2DDisplay(
        mGrassFieldSpace.getCurrentGrassField(), colorMap);
        
    Object2DDisplay displayAgents = new Object2DDisplay(
        mGrassFieldSpace.getCurrentGrassField());
    displayAgents.setObjectList(mAgentList);
    
    mTiles.addDisplayable(displayGrass, "Grass");
    mTiles.addDisplayable(displayAgents, "Agents");
    mStatisticsGraph.addSequence("Rabbits In Space", new NbrRabbits());
    mStatisticsGraph.addSequence("Grass Amount", new GrassAmount());
  }
  
  private void addNewAgent() {
    RabbitsGrassSimulationAgent newAgent = new RabbitsGrassSimulationAgent(
        mStartEnergy, mGrassFieldSpace);
    mAgentList.add(newAgent);
    mGrassFieldSpace.addAgent(newAgent);
  }
  
  @Override
  public String[] getInitParam() {
    String[] initParams = { "NumRabbits", "WorldXSize",
        "WorldYSize", "StartEnergy", "ReproduceThreshold",
        "GrassAmount", "EnergyLostMoving", "MaxGrassEating",
        "EnergyPerGRass", "ReproduceCost" };
    return initParams;
  }
  
  public int getNumRabbits() {
    return mNumRabbits;
  }
  
  public void setNumRabbits(int na) {
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
  
  public int getReproduceThreshold() {
    return mReproduceThreshold;
  }
  
  public void setReproduceThreshold(int reproduceThreshold) {
    mReproduceThreshold = reproduceThreshold;
  }
  
  public int getStartEnergy() {
    return mStartEnergy;
  }
  
  public void setStartEnergy(int startEnergy) {
    mStartEnergy = startEnergy;
  }
  
  public int getEnergyLostMoving() {
    return mEnergyLostMoving;
  }
  
  public void setEnergyLostMoving(int energyLostMoving) {
    mEnergyLostMoving = energyLostMoving;
  }
  
  public int getEnergyPerGRass() {
    return mEnergyPerGRass;
  }
  
  public void setEnergyPerGRass(int energyPerGRass) {
    this.mEnergyPerGRass = energyPerGRass;
  }
  
  public int getMaxGrassEating() {
    return mMaxGrassEating;
  }
  
  public void setMaxGrassEating(int maxGrassEating) {
    this.mMaxGrassEating = maxGrassEating;
  }
  
  public int getReproduceCost() {
    return mReproduceCost;
  }
  
  public void setReproduceCost(int reproduceCost) {
    this.mReproduceCost = reproduceCost;
  }
  
}
