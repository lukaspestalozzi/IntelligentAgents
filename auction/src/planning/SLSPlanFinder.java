package planning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;

public class SLSPlanFinder {
	private static final boolean VERBOSE = true;
	private long MAX_COMPUTATION_TIME;
	private List<Task> mTasks;
	private List<Vehicle> mVehicles;
	private ObjFunc mObjFunc;
	private Integer mIter;
	private Comparator<Assignment> mCompare;
	private int mI;
	private Random mRand;
	private double mProba;
	private double mProbaAnn;
	private double mAnnStep;

	/**
	 * We are guaranteed to have between 2 and 5 vehicles
	 */
	public SLSPlanFinder(List<Vehicle> vehicles, Integer numIter, double probability, long bid_timeout) {
		MAX_COMPUTATION_TIME = bid_timeout;
		mTasks = null;
		mVehicles = vehicles;
		mObjFunc = new ObjFunc();
		mIter = numIter;
		mCompare = new Comparator<Assignment>() {
			@Override
			public int compare(Assignment a1, Assignment a2) {
				double cost1 = mObjFunc.compute(a1);
				double cost2 = mObjFunc.compute(a2);
				if (cost1 == cost2) {
					return 0;
				} else {
					return cost1 < cost2 ? -1 : 1;
				}
			}
		};
		mRand = new Random(2015);
		mProba = probability;
		mProbaAnn = mProba;
		mAnnStep = mProba / mIter;
	}
	

	/**
	 * computes the best plan given the tasks in the set
	 * @param tasks
	 * @return
	 */
	public Assignment computeBestPlan(Set<Task> tasks) {
		return computeBestPlan(new LinkedList<Task>(tasks));
	}

	public Assignment computeBestPlan(Task... tasks) {
		return computeBestPlan(Arrays.asList(tasks));
	}
	
	public Assignment computeBestPlan(List<Task> tasks, Task... othertasks) {
		List<Task> l = new ArrayList<Task>(Arrays.asList(othertasks));
		l.addAll(tasks);
		return computeBestPlan(null, l);
	}	

	public Assignment computeBestPlan(Assignment initialAssignment, List<Task> tasks) {
		long startt = System.currentTimeMillis();
		if(tasks == null){
			tasks = new LinkedList<Task>(initialAssignment.vehicles.keySet());
		}
		
		printIfVerbose("computing plan for "+tasks.size()+" tasks (timeout is %d)...", MAX_COMPUTATION_TIME);
		Assignment oldA = initialAssignment == null ? selectInitalSolution(mVehicles, tasks) : initialAssignment;
		if(tasks.isEmpty()){
			return new Assignment(mVehicles);
		}
		mTasks = tasks;
		
		
		
		
		oldA.cost = (long) mObjFunc.compute(oldA);
		Assignment newA = null, bestA = oldA;
		long maxEndTime = System.currentTimeMillis() + MAX_COMPUTATION_TIME - 3000;

		for (mI = 0; mI < mIter && maxEndTime > System.currentTimeMillis(); mI++) {
			newA = updateAssignment(oldA);
			long newCost = mObjFunc.compute(newA);
			if (bestA.cost > newCost) {
				bestA = newA;
				bestA.cost = newCost;
			}

			oldA = newA;
		}
		System.out.printf("it took %d ms to compute.\n", System.currentTimeMillis() - startt);
		printIfVerbose("...Cost of computed plan: "+bestA.computeCost());
		return bestA;
	}

	private Assignment updateAssignment(Assignment oldA) {
		TreeSet<Assignment> nabos = oldA.generateAllNeighbors(mCompare, randomTask(), randomTask(), randomTask());
//		TreeSet<Assignment> nabos = oldA.generateAllNeighbors(mCompare, randomTask());
		
		
		if (nabos.size() == 1) {
			return nabos.first();
		}
		Assignment newA = choose(nabos);
		return newA;
	}

	private Assignment choose(TreeSet<Assignment> nabos) {
		double progress = mI / (double) mIter;
		int maxIndex = (int) Math.floor(nabos.size() * (1 - progress));
		maxIndex += mRand.nextDouble() < mProba ? 1 : 0;
		return chooseSuboptimalNaboGauss(nabos, maxIndex);
	}

	private Assignment chooseSuboptimalNaboGauss(TreeSet<Assignment> nabos, int maxIndex) {
		if (maxIndex < 0) {
			maxIndex = 0;
		} else if (maxIndex == 0) {
			return nabos.first();
		}else if(maxIndex >= nabos.size()){
			maxIndex = nabos.size()-1;
		}
		double rand = mRand.nextGaussian();
		while (rand > 1 || rand < 0) {
			rand = mRand.nextGaussian();
		}

		int index = (int) Math.floor(rand * maxIndex);

		// go to index
		Iterator<Assignment> it = nabos.iterator();
		while (--index >= 0) {
			it.next();
		}
		return it.next();
	}

	private Task randomTask() {
		return mTasks.get(mRand.nextInt(mTasks.size()));
	}
	
	private Vehicle randomVehicle(){
		return mVehicles.get(mRand.nextInt(mVehicles.size()));
	}

	private Assignment selectInitalSolution(List<Vehicle> vehicles, List<Task> tasks) {
		if (tasks == null || tasks.isEmpty() || vehicles == null || vehicles.isEmpty()) {
			return null;
		}
		Map<Vehicle, List<Action>> vehicleRoutes = new HashMap<Vehicle, List<Action>>();
		Map<Task, Vehicle> tv = new HashMap<Task, Vehicle>();
		Map<Action, Integer> indexOf = new HashMap<Action, Integer>();

		// initialize all lists and find vehicle with the biggest capacity.
		double maxC = -1;
		Vehicle maxV = null;
		for (Vehicle v : vehicles) {
			vehicleRoutes.put(v, new LinkedList<Action>());
			if (v.capacity() > maxC) {
				maxC = v.capacity();
				maxV = v;
			}
		}

		// add all actions to the maxV indexOf and vehicles
		List<Action> maxVRoute = vehicleRoutes.get(maxV);
		int index = 0;
		for (Task t : tasks) {
			Pickup pick = new Pickup(t);
			Deliver del = new Deliver(t);

			// add to route
			maxVRoute.add(pick);
			maxVRoute.add(del);

			// add to indexOf
			indexOf.put(pick, index++);
			indexOf.put(del, index++);

			// add to vehicle
			tv.put(t, maxV);
		}

		Assignment a = new Assignment(vehicleRoutes, tv, indexOf);
		if (!Constraints.checkAllConstraints(a, tasks.size())) {
			throw new IllegalStateException("Not all constraints are satisfied!");
		}
		return a;

	}
	
	public void setTimeout(long newTimeout){
		MAX_COMPUTATION_TIME = newTimeout;
	}
	
	public void printIfVerbose(String str, Object...objects){
		printIfVerbose(String.format(str, objects));
	}
	
	
	
	/**
	 * prints s if the VERBOSE flag is set to true: </br>
	 * if(VERBOSE){ System.out.println(s); }
	 * 
	 * @param s
	 */
	public void printIfVerbose(String str) {
		if (VERBOSE) {
			System.out.println(new StringBuilder()
					.append("    ")
					.append("(plan-finder): ")
					.append(str)
					.toString());
			System.out.flush();
		}
	}
}
