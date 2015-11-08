package template;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;

public class ObjFunc {
  
  public double compute(Assignment a){
    // TODO  compute the distance of each vehicle
    for(Vehicle v : a.getFirstAction().keySet()){
      Action nextA = a.getFirstAction().get(v);
      City currC = v.getCurrentCity();
      double dist = 0;
      while(nextA != null){
        dist += currC.distanceTo(nextA.)
      }
    }
    
    // TODO find cost of each vehicle
    // TODO return sum of all costs
  }

}
