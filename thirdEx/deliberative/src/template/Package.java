package template;

public class Package {
  public final int id;
  
  public static int nextId = 0;
  public Package() {
    this.id = nextId++;
    
  }
}
