package template;

import logist.task.Task;

public abstract class Action {
  public final Task task;
  
  public Action(Task task) {
    this.task = task;
  }
}
