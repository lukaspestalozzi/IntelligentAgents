package template;

import logist.plan.ActionHandler;
import logist.task.Task;
import logist.topology.Topology.City;

public class ActionGetter implements ActionHandler<City>{

  @Override
  public City moveTo(City city) {
    return city;
  }

  @Override
  public City pickup(Task task) {
    return task.deliveryCity;
  }

  @Override
  public City deliver(Task task) {
    return task.deliveryCity;
  }

}
