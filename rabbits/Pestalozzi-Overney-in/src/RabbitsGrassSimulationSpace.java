import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
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
      int x = (int)(Math.random()*mGrassFieldSpace.getSizeX());
      int y = (int)(Math.random()*mGrassFieldSpace.getSizeY());

      mGrassFieldSpace.putObjectAt(x, y, new Integer(getGrassAt(x, y) + 1));
    }
  }

  public int getGrassAt(int x, int y) {

    Object currentOccupant = mGrassFieldSpace.getObjectAt(x,y);
    
    return (currentOccupant != null) ?
        ((Integer)currentOccupant).intValue() : 0;
  }

  public Object2DGrid getCurrentGrassField() {
    return mGrassFieldSpace;
  }

  public Object2DGrid getCurrentAgentSpace() {
    return mAgentSpace;
  }

  public boolean isCellOccupied(int x, int y) {
    return mAgentSpace.getObjectAt(x, y) != null;
  }

  public boolean addAgent(RabbitsGrassSimulationAgent agent) {
    boolean retVal = false;

    for (int count = 0;
      !retVal && count < 10 * mAgentSpace.getSizeX() * mAgentSpace.getSizeY();
      count++) {

      int x = (int)(Math.random()*mAgentSpace.getSizeX());
      int y = (int)(Math.random()*mAgentSpace.getSizeY());
      System.out.println(mAgentSpace.getSizeX());
      if (!isCellOccupied(x, y)) {
        mAgentSpace.putObjectAt(x, y, agent);
        agent.setXY(x,y);
        retVal = true;
      }
    }

    return retVal;
  }
}
