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
    final int prime = 31;
    int result = 17;
    result = prime * result + task.id;
    result = prime * result + task.pickupCity.id;
    return result;
  }
  
  @Override
  public boolean isPickup() {
    return true;
  }
  
  @Override
  public String toString() {
    return "Pickup("+task.id+")";
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
    final int prime = 31;
    int result = 17;
    result = prime * result + task.id;
    result = prime * result + task.deliveryCity.id;
    return result;
  }
  
  @Override
  public boolean isDelivery() {
    return true;
  }
  
  @Override
  public String toString() {
    return "Deliver("+task.id+")";
  }
  
}
