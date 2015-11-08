package template;

import logist.simulation.Vehicle;

public class ObjFunc {

  public double compute(Assignment a) {
    // Sum for each task
    double actionSum = 0;
    for (Action act : a.nextAction.keySet()) {
      Action nextA = a.nextAction.get(act);
      // Length is not useful as we have split our task in two actions.
      actionSum += (distance(act, nextA))
          * a.vehicles.get(act.task).costPerKm();
    }

    // Sum for each vehicle
    double vehicleSum = 0;
    for (Vehicle v : a.firstAction.keySet()) {
      Action firstA = a.firstAction.get(v);
      vehicleSum += distance(v, firstA) * v.costPerKm();
    }
    return actionSum + vehicleSum;
  }

  private double distance(Vehicle v, Action a) {
    return (v == null || a == null) ? 0 : v.getCurrentCity().distanceTo(
        a.actionCity);
  }

  private double distance(Action act, Action nextA) {
    return (act == null || nextA == null) ? 0 : act.actionCity
        .distanceTo(nextA.actionCity);
  }
}
