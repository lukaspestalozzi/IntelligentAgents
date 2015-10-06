package template;

public class Triple<K, L> {
  public final K _1;
  public final L _2;
  public final K _3;
  
  public Triple(K o1, L o2, K o3) {
    _1 = o1;
    _2 = o2; 
    _3 = o3;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((_1 == null) ? 0 : _1.hashCode());
    result = prime * result
        + ((_2 == null) ? 0 : _2.hashCode());
    result = prime * result
        + ((_3 == null) ? 0 : _3.hashCode());
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Triple)){
      return false;
    }else{
      Triple<?, ?> t = (Triple<?, ?>)obj;
      return _1.equals(t._1) && _2.equals(t._2) && _3.equals(t._3);
    }
  }
}
