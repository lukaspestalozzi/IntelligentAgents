package template;

import java.util.Set;

public class Variable<T> {
  public final T value;
  public final Set<T> domain;
  
  public Variable(T value, Set<T> domain) {
    this.value = value;
    this.domain = domain;
  }
}
