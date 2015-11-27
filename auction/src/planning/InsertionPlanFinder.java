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
import logist.topology.Topology.City;

public class InsertionPlanFinder {
	private final List<Vehicle> mVehicles;
	private InsertionAssignment mCurrentAssigment;
	private final HashSet<Task> mTasks;
	
	public InsertionPlanFinder(List<Vehicle> vehicles) {
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
}
