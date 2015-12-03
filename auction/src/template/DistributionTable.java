package template;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class DistributionTable {
  
  private final Topology mTopology;
  private final TaskDistribution mDistribution;
  private final CityTuple[] mCityTuples;
  public final CityTuple[] sortedCities;
  public final double mNbrCities;
  
  public DistributionTable(Topology topology, TaskDistribution distribution) {
    mTopology = topology;
    mDistribution = distribution;
    mNbrCities = topology.cities().size();
    
    
    List<City> cities = mTopology.cities();
    mCityTuples = new CityTuple[cities.size()*cities.size()];
    PriorityQueue<CityTuple> sortedQueue = new PriorityQueue<CityTuple>(mCityTuples.length+1);
    
    // fill mSortedCities and CityTuples
    int index = 0;
    for(int i = 0; i < cities.size(); i++){
      for(int j = 0; j < cities.size(); j++){
        City from = cities.get(i);
        City to = cities.get(j);
        CityTuple ct = new CityTuple(from, to, mDistribution.probability(from, to) / mNbrCities);
        mCityTuples[index] = ct;
        sortedQueue.add(ct);
      }
    }
    
    sortedCities = new CityTuple[sortedQueue.size()];
    index = 0;
    while(! sortedQueue.isEmpty()){
      sortedCities[index++] = sortedQueue.remove();
    }
    
  }
  
  /**
   * 
   * @return a (not sorted) array of all CityTuples.
   */
  public CityTuple[] getAllCityTuples() {
    // TODO return clone?
    return mCityTuples;
  }
  
  /**
   * 
   * @param n number of tuples
   * @return
   */
  public CityTuple[] getMostProbable(int n){
    return Arrays.copyOfRange(new CityTuple[n], 0, Math.min(n, sortedCities.length));
  }

  
  
  // Function wrappers from TaskDistribution:
  public double probability(City from, City to){
    return mDistribution.probability(from, to);
  }

  
  public int reward(City from, City to){
    return mDistribution.reward(from, to);
  }

  
  public int weight(City from, City to){
    return mDistribution.weight(from, to);
  }
  
  
}

