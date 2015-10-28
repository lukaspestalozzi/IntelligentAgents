package template;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;

public abstract class Position {
  private City mGoal;
  
  public Position(City goal) {
    mGoal = goal;
  }
  
  public City getGoal() {
    return mGoal;
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
  
  
  
  @Override
  public abstract int hashCode();
  
  @Override
  public abstract boolean equals(Object other);

}

class InDelivery extends Position {
  
  public final Vehicle vehicle;

  public InDelivery(Vehicle vehicle, City goal) {
    super(goal);
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
    return obj instanceof InDelivery && this.vehicle.equals(((InDelivery)obj).vehicle) && this.getGoal().equals(((Position)obj).getGoal());
  }
  
  @Override
  public int hashCode() {
    return (17 * 31 + vehicle.hashCode()) * 31 + super.getGoal().hashCode();
  }

}

/**
 * A stationary position. eg. waiting or delivered.
 */
abstract class Stationary extends Position{
  public final City city;

  public Stationary(City city, City goal) {
    super(goal);
    this.city = city;
  }
  
  @Override
  public int hashCode() {
    return (17 * 31 + this.city.hashCode()) * 31 + super.getGoal().hashCode();
  }
}

class Delivered extends Stationary {

  public Delivered(City city, City goal) {
    super(city, goal);
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
    return other instanceof Delivered && this.city.equals(((Delivered)other).city)
        && this.getGoal().equals(((Position)other).getGoal());
  }

}

class Waiting extends Stationary {

  public Waiting(City city, City goal) {
    super(city, goal);
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
    return other instanceof Waiting && this.city.equals(((Waiting)other).city)
        && this.getGoal().equals(((Position)other).getGoal());
  }

}