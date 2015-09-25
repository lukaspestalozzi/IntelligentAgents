import java.awt.Color;
import java.util.Random;

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
  
  private static Random random = new Random();
  
  private RabbitsGrassSimulationSpace mSpace;
  
  public RabbitsGrassSimulationAgent(int startEnergy, RabbitsGrassSimulationSpace space) {
    mX = -1;
    mY = -1;
    mEnergyLevel = startEnergy;
    mID = ++msIDNumber;
    mSpace = space;
  }
  
  @Override
  public void draw(SimGraphics target) {
    target.drawFastRoundRect(Color.red);
  }
  
  /**
   * 
   * @return true if the rabbit  moved, false if something was in the way and the rabbit staid on the same place.
   */
  public boolean move(){
    int newX = mX;
    int newY = mY;
    do{
      int direction = random.nextBoolean() ? 1 : -1;
      if (random.nextBoolean()) {
        newX = mX + direction;
      } else {
        newY = mY + direction;
      }
    }while(!mSpace.isInField(newX, newY)); // TODO may hang if all 4 directions are not in the field for some reason
    
    if(mSpace.moveAgent(mX, mY, newX, newY)){
      mX = newX;
      mY = newY;
      return true;
    }else{
      return false;
    }
  }
  
  public RabbitsGrassSimulationAgent reproduce(int threshold, int startEnergy, int energyLoss){
    if(mEnergyLevel > threshold){
   // TODO make sure energyLoss < threshold ?
      RabbitsGrassSimulationAgent baby = new RabbitsGrassSimulationAgent(startEnergy, mSpace);
      if(mSpace.addAgent(baby)){
        looseEnergy(energyLoss);
        return baby;
      }
    }
    return null;
  }
  
  public void eat(int max, int energyPerGrass){
    int amountEaten = Math.min(max, mSpace.getGrassAt(mX, mY));
    mSpace.removeGrassAt(mX, mY, amountEaten);
    gainEnergy(amountEaten*energyPerGrass);
//    System.out.println(String.format("%d ate %d grass and has now %d energy.", getID(), amountEaten, mEnergyLevel));
  }
  
  public boolean hasToDie(){
    return mEnergyLevel <= 0;
  }
  
  public void looseEnergy(int energyLoss) {
    mEnergyLevel -= energyLoss;
  }
  
  public void gainEnergy(int amountGained){
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
