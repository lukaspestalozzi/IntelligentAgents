package template;

import java.util.List;
import java.util.PriorityQueue;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class DistributionTable {
  
  private final Topology mTopology;
  private final TaskDistribution mDistribution;
  private final PriorityQueue<CityTuple> mSortedCities;
  private final CityTuple[] mCityTuples;
  
  public DistributionTable(Topology topology, TaskDistribution distribution) {
    mTopology = topology;
    mDistribution = distribution;
    
    
    List<City> cities = mTopology.cities();
    mCityTuples = new CityTuple[cities.size()*cities.size()];
    mSortedCities = new PriorityQueue<CityTuple>(mCityTuples.length+1);
    
    // fill mSortedCities and CityTuples
    int index = 0;
    for(int i = 0; i < cities.size(); i++){
      for(int j = 0; j < cities.size(); j++){
        City from = cities.get(i);
        City to = cities.get(j);
        CityTuple ct = new CityTuple(from, to, mDistribution.probability(from, to));
        mCityTuples[index] = ct;
        mSortedCities.add(ct);
      }
    }
  }
  
  public CityTuple[] getmCityTuples() {
    // TODO return clone?
    return mCityTuples;
  }
  
  public PriorityQueue<CityTuple> getmSortedCities() {
    // TODO return clone?
    return mSortedCities;
  }
  
  
  // Function wrappers from TaskDistribution:
  double probability(City from, City to){
    return mDistribution.probability(from, to);
  }

  
  int reward(City from, City to){
    return mDistribution.reward(from, to);
  }

  
  int weight(City from, City to){
    return mDistribution.weight(from, to);
  }
  
  
}

class CityTuple implements Comparable<CityTuple>{
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
}