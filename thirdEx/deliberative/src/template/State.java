package template;

import logist.topology.Topology.City;

public class State {
  //TODO Determine exactly what's needed to represent a state
  
  private final City mVehiclePosition; 
  private final double mFreeLoad;
  private final Position[] mPackagePositions;
  
  /**
   * 
   * @param vehiclePosition
   * @param freeLoad
   * @param packagePositions
   */
  public State (City vehiclePosition, double freeLoad, Position[] packagePositions) {
    if(packagePositions.length != DeliberativeTemplate.allPackages.length){
      throw new IllegalArgumentException("...");
    }
    
    mFreeLoad = freeLoad;
    mPackagePositions = packagePositions;
    mVehiclePosition = vehiclePosition;
    
    
  }
}
