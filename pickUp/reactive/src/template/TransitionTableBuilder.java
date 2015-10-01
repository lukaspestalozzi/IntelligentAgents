package template;

import java.util.HashMap;
import java.util.HashSet;

public class TransitionTableBuilder {
  
    private HashSet<State> mStates;
    private HashSet<Action> mActions;
    private HashMap<Tuple<State, Action>, Double> mTable;
    
    public TransitionTableBuilder() {
      mStates = new HashSet<>();
      mActions = new HashSet<>();
    }
    
    public void addEntry(State s, Action a, double result){
      mStates.add(s);
      mActions.add(a);
      mTable.put(new Tuple<State, Action>(s, a), result);
    }
    
    public TransitionTable build(){
      return new TransitionTable(mStates.toArray(new State[mStates.size()]), mActions.toArray(new Action[mActions.size()]), mTable);
    }
    
    
    
    
}
