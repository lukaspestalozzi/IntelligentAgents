package template;


public final class DPDelivery extends DPAction {
  
  public DPDelivery() {
    super();
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof DPDelivery;
  }

  @Override
  public boolean isMove() {
    return false;
  }

  @Override
  public boolean isDelivery() {
    return true;
  }

  @Override
  public String toString() {
    return "DeliveryAction";
  }

  

}
