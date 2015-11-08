package template;

import logist.task.Task;
import logist.topology.Topology.City;

public abstract class Action {
  public final Task task;
  public final City actionCity;
  
  public Action(Task task, City aCity) {
    this.task = task;
    this.actionCity = aCity;
  }
  
  public boolean isPickup(){
    return false;
  }
  
  public boolean isDelivery(){
    return false;
  }
}

class Pickup extends Action{

  public Pickup(Task task) {
    super(task, task.pickupCity);
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Pickup && ((Pickup)obj).task.equals(this.task);
  }
  
  @Override
  public int hashCode() {
    return this.task.hashCode(); // same hashCode as the task
  }
  
  @Override
  public boolean isPickup() {
    return true;
  }
  
}

class Deliver extends Action{

  public Deliver(Task task) {
    super(task, task.deliveryCity);
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Deliver && ((Deliver)obj).task.equals(this.task);
  }

  @Override
  public int hashCode() {
    return this.task.hashCode() * -1; // the negative hash code of the task. to distinguish it from the Pickup Action.
  }
  
  @Override
  public boolean isDelivery() {
    return true;
  }
  
}
