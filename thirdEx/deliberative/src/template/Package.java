package template;

public class Package {
  public final int mId;
  
  public static int nextId = 1;
  public final double mWeight;
  
  public Package(double weight) {
    mId = nextId++;
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
