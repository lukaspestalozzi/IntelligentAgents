import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * 
 * @author
 */

public class RabbitsGrassSimulationSpace {
  private Object2DGrid mGrassFieldSpace;
  private Object2DGrid mAgentSpace;
  
  public RabbitsGrassSimulationSpace(int xSize, int ySize) {
    
    mGrassFieldSpace = new Object2DGrid(xSize, ySize);
    mAgentSpace = new Object2DGrid(xSize, ySize);
    
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        mGrassFieldSpace.putObjectAt(i, j, new Integer(0));
      }
    }
  }
  
  public void spreadGrass(int grassAmount) {
    
    for (int i = 0; i < grassAmount; i++) {
      int x = (int) (Math.random()
          * mGrassFieldSpace.getSizeX());
      int y = (int) (Math.random()
          * mGrassFieldSpace.getSizeY());
          
      mGrassFieldSpace.putObjectAt(x, y,
          new Integer(getGrassAt(x, y) + 1));
    }
  }
  
  public int getGrassAt(int x, int y) {
    return (int) mGrassFieldSpace.getValueAt(x, y);
  }
  
  public void removeAgent(RabbitsGrassSimulationAgent agent){
    mAgentSpace.putObjectAt(agent.getX(), agent.getY(), null);
  }
  
  public void removeGrassAt(int x, int y, int amount){
    // TODO throw error when amount > actual value at (x, y) ?
    mGrassFieldSpace.putValueAt(x, y, Math.max(0, getGrassAt(x, y) - amount));
  }
  
  public Object2DGrid getCurrentGrassField() {
    return mGrassFieldSpace;
  }
  
  public Object2DGrid getCurrentAgentSpace() {
    return mAgentSpace;
  }
  
  public boolean isInField(int x, int y) {
    return x >= 0 && y >= 0 && x < mAgentSpace.getSizeX()
        && y < mAgentSpace.getSizeY();
  }
  
  public boolean isCellOccupied(int x, int y) {
    return mAgentSpace.getObjectAt(x, y) != null;
  }
  
  /**
   * 
   * @param fromX
   * @param fromY
   * @param toX
   * @param toY
   * @return true if the rabbit was moved, false if something was in the way and the rabbit staid on the same place.
   */
  public boolean moveAgent(int fromX, int fromY, int toX,
      int toY) {
    RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) mAgentSpace
        .getObjectAt(fromX, fromY);
    // TODO assert that fromX, fromY contains an agent
    if (!isCellOccupied(toX, toY)) {
      mAgentSpace.putObjectAt(toX, toY, agent);
      mAgentSpace.putObjectAt(fromX, fromY, null);
      return true;
    }
    return false;
  }
  
  public boolean addAgent(
      RabbitsGrassSimulationAgent agent) {
    boolean retVal = false;
    
    for (int count = 0; !retVal
        && count < 10 * mAgentSpace.getSizeX()
            * mAgentSpace.getSizeY(); count++) {
            
      int x = (int) (Math.random()
          * mAgentSpace.getSizeX());
      int y = (int) (Math.random()
          * mAgentSpace.getSizeY());
          
      if (!isCellOccupied(x, y)) {
        mAgentSpace.putObjectAt(x, y, agent);
        agent.setXY(x, y);
        retVal = true;
      }
    }
    
    return retVal;
  }
}
