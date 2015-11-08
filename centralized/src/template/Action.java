package template;

import logist.task.Task;

public abstract class Action {
  public final Task task;
  
  public Action(Task task) {
    this.task = task;
  }
  
  public Task getTask() {
    return task;
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
    super(task);
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Pickup && ((Pickup)obj).getTask().equals(this.getTask());
  }
  
  @Override
  public int hashCode() {
    return getTask().hashCode(); // same hashCode as the task
  }
  
  @Override
  public boolean isPickup() {
    return true;
  }
  
}

class Deliver extends Action{

  public Deliver(Task task) {
    super(task);
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Deliver && ((Deliver)obj).getTask().equals(this.getTask());
  }

  @Override
  public int hashCode() {
    return getTask().hashCode() * -1; // the negative hash code of the task. to distinguish it from the Pickup Action.
  }
  
  @Override
  public boolean isDelivery() {
    return true;
  }
  
}
