import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

/**
 * Class that implements the simulation agent for the rabbits grass simulation.
 *
 * @author
 */

public class RabbitsGrassSimulationAgent
    implements Drawable {
  private int mX;
  private int mY;
  private int mEnergyLevel;
  private int mBirthTreshold;
  private static int msIDNumber = 0;
  private int mID;
  
  public RabbitsGrassSimulationAgent(int startEnergy,
      int birtThreshold) {
    mX = -1;
    mY = -1;
    mEnergyLevel = startEnergy;
    mBirthTreshold = birtThreshold;
    mID = ++msIDNumber;
  }
  
  @Override
  public void draw(SimGraphics target) {
    target.drawFastRoundRect(Color.red);
  }
  
  public void looseEnergy(int energyLoss) {
    this.mEnergyLevel -= energyLoss;
  }
  
  @Override
  public int getX() {
    return mX;
  }
  
  @Override
  public int getY() {
    return mY;
  }
  
  public void setXY(int newX, int newY) {
    mX = newX;
    mY = newY;
  }
  
  public String getID() {
    return "A-" + mID;
  }
  
  public int getEnergyLevel() {
    return mEnergyLevel;
  }
  
  public int getBirthTreshold() {
    return mBirthTreshold;
  }
  
  public void report() {
    System.out.println(this.toString());
  }
  
  @Override
  public String toString() {
    return getID() + " at (" + mX + ", " + mY + ") has "
        + getEnergyLevel() + " energy.";
  }
  
}
