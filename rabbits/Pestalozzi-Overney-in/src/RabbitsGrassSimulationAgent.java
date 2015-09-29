import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

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
  private static Image msRabbitImg = null;
  
  private static Random random = new Random();
  
  private RabbitsGrassSimulationSpace mSpace;
  
  public RabbitsGrassSimulationAgent(int startEnergy,
      RabbitsGrassSimulationSpace space) {
    mX = -1;
    mY = -1;
    mEnergyLevel = startEnergy;
    mID = ++msIDNumber;
    mSpace = space;
    if (msRabbitImg == null) {
      try {
        msRabbitImg = ImageIO
            .read(new File("src/resources/rabbitImg.png"));
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  
  @Override
  public void draw(SimGraphics target) {
    // target.drawFastRoundRect(Color.red);
    target.drawImageToFit(msRabbitImg);
  }
  
  /**
   * 
   * @return true if the rabbit moved, false if something was in the way and/or
   *         the rabbit staid on the same place.
   */
  public boolean move(int worldX, int worldY) {
    int newX = mX;
    int newY = mY;
    int direction = random.nextBoolean() ? 1 : -1;
    if (random.nextBoolean()) {
      newX = (mX + direction);
      if(newX < 0){
        newX = worldX-1;
      }else if(newX == worldX){
        newX = 0;
      }
    } else {
      newY = (mY + direction);
      if(newY < 0){
        newY = worldY-1;
      }else if(newY == worldY){
        newY = 0;
      }
    }
    
    if (mSpace.moveAgent(mX, mY, newX, newY)) {
      mX = newX;
      mY = newY;
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * 
   * @param threshold
   * @param startEnergy
   * @param energyLoss
   * @return the newly created (and added) rabbit or null if none was created
   *         (or added).
   */
  public RabbitsGrassSimulationAgent reproduce(
      int threshold, int startEnergy, int energyLoss) {
    if (mEnergyLevel > threshold) {
      // make sure energyLoss < threshold
      if (energyLoss >= threshold) {
        energyLoss = threshold - 1;
      }
      
      RabbitsGrassSimulationAgent baby = new RabbitsGrassSimulationAgent(
          startEnergy, mSpace);
      if (mSpace.addAgent(baby)) {
        looseEnergy(energyLoss);
        return baby;
      }
    }
    return null;
  }
  
  public void eat(int max, int energyPerGrass) {
    int amountEaten = Math.min(max,
        mSpace.getGrassAt(mX, mY));
    mSpace.removeGrassAt(mX, mY, amountEaten);
    gainEnergy(amountEaten * energyPerGrass);
    // System.out.println(String.format("%d ate %d grass and has now %d
    // energy.", getID(), amountEaten, mEnergyLevel));
  }
  
  public boolean hasToDie() {
    return mEnergyLevel <= 0;
  }
  
  public void looseEnergy(int energyLoss) {
    mEnergyLevel -= energyLoss;
  }
  
  public void gainEnergy(int amountGained) {
    mEnergyLevel += amountGained;
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
