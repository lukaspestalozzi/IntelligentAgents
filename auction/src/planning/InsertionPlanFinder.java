package planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class InsertionPlanFinder {
	private final List<Vehicle> mVehicles;
	private InsertionAssignment mCurrentAssigment;
	private final HashSet<Task> mTasks;
	private final SLSPlanFinder mSlsPlaner;
	
	public InsertionPlanFinder(List<Vehicle> vehicles, int bid_timeout) {
		mVehicles = new ArrayList<Vehicle>(vehicles);
		mTasks = new HashSet<Task>(100);
		
		// init assignment
		Map<Vehicle, LinkedList<Action>> vehicleRoutes = new HashMap<Vehicle, LinkedList<Action>>(vehicles.size());
		Map<Action, Integer> indexOf = new HashMap<Action, Integer>(100);
		Map<Task, Vehicle> vehiclesMap = new HashMap<Task, Vehicle>(50);
		//   fill maps
		for(Vehicle v : vehicles){
			vehicleRoutes.put(v, new LinkedList<Action>());
		}
		mSlsPlaner = new SLSPlanFinder(vehicles, 40000, 0.5, bid_timeout);
		mCurrentAssigment = new InsertionAssignment(vehicleRoutes, vehiclesMap, indexOf);
	}
	
	public boolean addTask(Task t){
		mTasks.add(t);
		return mCurrentAssigment.insertTask(t);
	}
	
	public InsertionAssignment getAssignment(){
		return  mCurrentAssigment;
	}
	
	public long getCost(){
		return mCurrentAssigment.getCost();
	}
	
	/**
	 * Does NOT change the state of the Assignment
	 * @param t
	 * @return the cost of the plan with t.
	 */
	public long costWithTask(Task t){
		mCurrentAssigment.printIfVerbose("Testing task(%d) with length %.2f... ", t.id, t.pathLength());
		mCurrentAssigment.insertTask(t);
		long cost = mCurrentAssigment.getCost();
		
		
		
		
		
		mCurrentAssigment.remove(t);
		mCurrentAssigment.printIfVerbose("...Testing task(%d) done.", t.id);
		return cost;
	}

	public List<Plan> generatePlans(List<Vehicle> vehicles) {
		return mCurrentAssigment.generatePlans(vehicles);
	}
	
	public List<Plan> computeBestPlans(List<Vehicle> vehicles, TaskSet tasks){
		Assignment a = mSlsPlaner.computeBestPlan(mCurrentAssigment.toSlsAssignment(), new LinkedList<Task>(tasks));
		return a.computeCost() < mCurrentAssigment.getCost() ? a.generatePlans(vehicles) : mCurrentAssigment.generatePlans(vehicles);
	}
	
	public void setTimeout(int newTimeout){
		mSlsPlaner.setTimeout(newTimeout);
	}
}
 