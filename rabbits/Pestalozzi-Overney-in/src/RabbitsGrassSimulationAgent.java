import java.awt.Color;

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
  private static int msIDNumber = 0;
  private int mID;

  public RabbitsGrassSimulationAgent(int minLifespan, int maxLifespan) {
    mX = -1;
    mY = -1;
    mEnergyLevel = 0;
    mStepsToLive =
      (int)(Math.random() * (maxLifespan - minLifespan) + minLifespan);
    mID = ++msIDNumber;
  }

  public void draw(SimGraphics target) {
    target.drawFastRoundRect(Color.red);
  }

  public int getX() {
    return mX;
  }

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

  public int getStepsToLive() {
    return mStepsToLive;
  }

  public void report() {
    System.out.println(getID() + " at " + mX + ", " + mY + " has " +
      getEnergyLevel() + " energy and " + getStepsToLive() + " steps to live.");
  }

}
