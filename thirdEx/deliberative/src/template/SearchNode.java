package template;

import java.util.HashSet;

public class SearchNode<S> {
  enum Status {OPEN, CLOSED};
  
  private final S mState;
  private double mG;
  private double mH;
  private double mF;
  private Status mStatus;
  private SearchNode<S> mBestParent;
  private HashSet<SearchNode<S>> mKids;
  
  public SearchNode(S state) {
    mState = state;
    mG  = 0.0;
    mH  = 0.0;
    mF = mG + mH;
    mStatus = null;
    mBestParent = null;
    
  }
  
  /**
   * also recomputes the F value.
   * @param g
   */
  public void setG(double g) {
    mG = g;
    mF = mG+mH;
  }
  
  public double getG() {
    return mG;
  }
  
  /**
   * also recomputes the F value.
   * @param h
   */
  public void setH(double h) {
    mH = h;
    mF = mG+mH;
  }
  
  public double getH() {
    return mH;
  }
  
  public double getF() {
    return mF;
  }
  
  public void setStatusOpen() {
    mStatus = Status.OPEN;
  }
  
  public void setStatusClosed() {
    mStatus = Status.CLOSED;
  }
  
  public Status getStatus() {
    return mStatus;
  }
  
  public void addKid(SearchNode<S> k){
    mKids.add(k);
  }
  
  public HashSet<SearchNode<S>> getKids() {
    return mKids;
  }
  
  public void setBestParent(SearchNode<S> bestParent) {
    mBestParent = bestParent;
  }
  
  public SearchNode<S> getBestParent() {
    return mBestParent;
  }
  
  public S getState() {
    return mState;
  }

  @Override
  public int hashCode() {
    return mState.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof SearchNode && ((SearchNode)obj).getState().equals(mState);
  }
  
  
}
