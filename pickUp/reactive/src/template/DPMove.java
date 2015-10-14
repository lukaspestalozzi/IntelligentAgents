package template;

import logist.topology.Topology.City;

public class DPMove extends DPAction {
private final City city;
private final City to;

  public DPMove(City c, City to) {
    super();
    this.city = c;
    this.to = to;
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((to == null) ? 0 : to.hashCode());
    return result;
  }



  @Override
  public boolean equals(Object obj) {
    return obj instanceof DPMove && ((DPMove) obj).getCity().equals(this.city) && ((DPMove) obj).getTo().equals(this.to);
  }

  @Override
  public boolean isMove() {
    return true;
  }

  @Override
  public boolean isDelivery() {
    return false;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Move(")
        .append(this.city.name)
        .append(", ")
        .append(this.to.name)
        .append(")")
        .toString();
  }
  
  public City getCity() {
    return city;
  }
  public City getTo() {
    return to;
  }

  

}
