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
  
  
  
  @Override
  public abstract int hashCode();
  
  @Override
  public abstract boolean equals(Object other);

}

class InDelivery extends Position {
  
  public final Vehicle vehicle;

  public InDelivery(Vehicle vehicle) {
    super();
    this.vehicle = vehicle;
  }
  
  @Override
  public String toString() {
    return "InDelivery";
  }
  
  @Override
  public boolean isInDelivery() {
    return true;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof InDelivery && this.vehicle.equals(((InDelivery)obj).vehicle);
  }
  
  @Override
  public int hashCode() {
    return vehicle.hashCode();
  }

}

/**
 * A stationary position. eg. waiting or delivered.
 */
abstract class Stationary extends Position{
  public final City city;

  public Stationary(City city) {
    super();
    this.city = city;
  }
  
  @Override
  public int hashCode() {
    return this.city.hashCode();
  }
}

class Delivered extends Stationary {

  public Delivered(City city) {
    super(city);
  }
  
  @Override
  public String toString() {
    return "Delivered";
  }
  
  @Override
  public boolean isDelivered() {
    return true;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Delivered && this.city.equals(((Delivered)other).city);
  }

}

class Waiting extends Stationary {

  public Waiting(City city) {
    super(city);
  }
  @Override
  public String toString() {
    return "Waiting";
  }
  
  @Override
  public boolean isWaiting() {
    return true;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Waiting && this.city.equals(((Waiting)other).city);
  }

}