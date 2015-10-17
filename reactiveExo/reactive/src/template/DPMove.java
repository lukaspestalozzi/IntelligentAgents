package template;

import logist.topology.Topology.City;

public class DPMove extends DPAction {
private final City from;
private final City to;

  public DPMove(City c, City to) {
    super();
    this.from = c;
    this.to = to;
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
    return obj instanceof DPMove && ((DPMove) obj).getFrom().equals(this.from) && ((DPMove) obj).getTo().equals(this.to);
  }

  @Override
  public boolean isMove() {
    return true;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Move(")
        .append(this.from.name)
        .append(", ")
        .append(this.to.name)
        .append(")")
        .toString();
  }
  
  public City getFrom() {
    return from;
  }
  public City getTo() {
    return to;
  }

  

}
