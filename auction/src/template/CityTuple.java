package template;

import logist.topology.Topology.City;

public class CityTuple implements Comparable<CityTuple>{
  public final City from;
  public final City to;
  public final double proba;
  
  public CityTuple(City from, City to, double proba) {
    this.from = from;
    this.to = to;
    this.proba = proba;
  }

  @Override
  public int compareTo(CityTuple other) {
    return -1 * Double.compare(this.proba, other.proba);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + ((to == null) ? 0 : to.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (!(obj instanceof CityTuple)) { return false; }
    CityTuple other = (CityTuple) obj;
    return this.from.equals(other.from) && this.to.equals(other.to);
  }
  
  
}