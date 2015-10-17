package template;

public class Package {
  public final int mId;
  
  public final double mWeight;
  
  public Package(double weight, int id) {
    mId = id;
    mWeight = weight;
  }

  @Override
  public int hashCode() {
    return mId;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Package && this.hashCode() == obj.hashCode();
  }
}
