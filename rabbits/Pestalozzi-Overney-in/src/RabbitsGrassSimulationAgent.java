import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
* Class that implements the simulation agent for the rabbits grass simulation.
*
* @author
*/

public class RabbitsGrassSimulationAgent implements Drawable {
  private int mX;
  private int mY;
  private int mEnergyLevel;
  private int mStepsToLive;

  public RabbitsGrassSimulationAgent(int minLifespan, int maxLifespan) {
    mX = -1;
    mY = -1;
    mEnergyLevel = 0;
    mStepsToLive =
      (int)(Math.random() * (maxLifespan - minLifespan) + minLifespan);
  }

  public void draw(SimGraphics arg0) {
// TODO Auto-generated method stub

  }

  public int getX() {
// TODO Auto-generated method stub
    return 0;
  }

  public int getY() {
// TODO Auto-generated method stub
    return 0;
  }

  public void setXY(int newX, int newY) {
    mX = newX;
    mY = newY;
  }

}
