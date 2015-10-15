package template;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;

public class Position {
  public final City city;
  
  public Position(City city) {
    this.city = city;
  }
  
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
    super(null);
    this.vehicle = vehicle;
  }
  
  @Override
  public boolean isInDelivery() {
    return true;
  }

}

class Delivered extends Position {

  public Delivered(City city) {
    super(city);
  }
  
  @Override
  public boolean isDelivered() {
    return true;
  }

}

class Waiting extends Position {

  public Waiting(City city) {
    super(city);
  }
  
  @Override
  public boolean isWaiting() {
    return true;
  }

}