package template;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;

public abstract class Position {
  
  public boolean isInDelivery(){
    return false;
  }
  
  public boolean isDelivered(){
    return false;
  }
  
  public boolean isWaiting(){
    return false;
  }

}

class InDelivery extends Position {
  
  public final Vehicle vehicle;

  public InDelivery(Vehicle vehicle) {
    super();
    this.vehicle = vehicle;
  }
  
  @Override
  public boolean isInDelivery() {
    return true;
  }

}

class Delivered extends Position {
  public final City city;

  public Delivered(City city) {
    super();
    this.city = city;
  }
  
  @Override
  public boolean isDelivered() {
    return true;
  }

}

class Waiting extends Position {
  public final City city;

  public Waiting(City city) {
    super();
    this.city = city;
  }
  
  @Override
  public boolean isWaiting() {
    return true;
  }

}