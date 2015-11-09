package template;

import java.util.List;
import java.util.Map.Entry;

import logist.simulation.Vehicle;

public class ObjFunc {

  public double compute(Assignment a) {
    
    double sum = 0;
    
    for (Entry<Vehicle, List<Action>> e : a.vehicleRoutes.entrySet()) {
      Vehicle v = e.getKey();
      List<Action> route = e.getValue();
      if(!route.isEmpty()) {
        sum += distance(v, route.get(0)) * v.costPerKm();
        Action lastAction = route.get(0);
        for(Action act : route) {
          sum += distance(lastAction, act) * v.costPerKm();
        }
      }
    }
   
    return sum;
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
