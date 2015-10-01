package template;

public class Tuple<K, T> {
  private final K mVal1;
  private final T mVal2;
  
  public Tuple(K v1, T v2) {
    this.mVal1 = v1;
    this.mVal2 = v2;
  }
  
  @Override
  public int hashCode() {
    int result = 1;
    final int prime = 31;
    result = prime * result + ((mVal1 == null) ? 0 : mVal1.hashCode());
    result = prime * result + ((mVal2 == null) ? 0 : mVal2.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Tuple) ? this.hashCode() == obj.hashCode() : false;
  }
}
