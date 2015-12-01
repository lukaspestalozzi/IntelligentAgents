package planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.topology.Topology.City;

public class InsertionAssignment extends AbstractAssignment{
	private static boolean VERBOSE = true;
	/** The route each vehicle takes */
	public final Map<Vehicle, LinkedList<Action>> vehicleRoutes;
	/** The vehicle that does the task */
	public final Map<Task, Vehicle> vehicles;
	/**
	 * the index of a Action in a route, the same as the times array in the
	 * project description
	 */
	public final Map<Action, Integer> indexOf;
	
	/** the cost of the current routes */
	private long mCost = 0;
	
	/**
	 * @param vehicleRoutes
	 * @param vehicles
	 * @param indexOf
	 */
	public InsertionAssignment(Map<Vehicle, LinkedList<Action>> vehicleRoutes, Map<Task, Vehicle> vehicles,
	    Map<Action, Integer> indexOf) {
		this.vehicleRoutes = vehicleRoutes;
		this.vehicles = vehicles;
		this.indexOf = indexOf;
	}
	
	public InsertionAssignment(Assignment a){
		this.vehicleRoutes = new HashMap<>();
		for(Entry<Vehicle, List<Action>> e : a.vehicleRoutes.entrySet()){
			this.vehicleRoutes.put(e.getKey(), toLinkedList(e.getValue()));
		}
		this.vehicles = a.vehicles;
		this.indexOf = a.indexOf;
		
		
		
	}
	
	private LinkedList<Action> toLinkedList(List<Action> l){
		return new LinkedList<Action>(l);
	}

	/**
	 * 
	 * @param vehics
	 * @return
	 */
	public List<Plan> generatePlans(List<Vehicle> vehics) {
		List<Plan> plans = new ArrayList<Plan>();
		
		for (Vehicle v : vehics) {
			Plan p = new Plan(v.getCurrentCity());
			if (!vehicleRoutes.get(v).isEmpty()) {
				// the vehicle has at least one action to do (actually two since it has
				// at least to pickup and deliver a package)
				City currentCity = v.getCurrentCity();
				for (Action nextA : vehicleRoutes.get(v)) {
					for (City miniGoal : currentCity.pathTo(nextA.actionCity)) {
						p.appendMove(miniGoal);
					}
					currentCity = nextA.actionCity;
					
					if (nextA.isDelivery()) {
						p.appendDelivery(nextA.task);
					} else if (nextA.isPickup()) {
						p.appendPickup(nextA.task);
					} else {
						throw new RuntimeException("Should never happen");
					}
				}
			}
			plans.add(p);
		}
		
		return plans;
		
	}
	
	/**
	 * 
	 * @param t
	 * @return true iff the task was added, false otherwise
	 */
	public boolean insertTask(Task t) {
		return insertActions(new Pickup(t), new Deliver(t));
	}
	
	/**
	 * 
	 * @param pick
	 * @param del
	 * @return true iff the two Actions were added, false otherwise.
	 */
	public boolean insertActions(Pickup pick, Deliver del) {
		printIfVerbose("Inserting... T:"+pick.task.id);
		// TODO find efficient way to calculate the cost
		// TODO -> only re-calculate cost of current route in the second while
		// TODO -> only calculate the added cost when inserting the delivery
		
		if (!pick.task.equals(del.task)) { throw new IllegalArgumentException("The Actions must belong to the same task"); }
		if (allTasks()
		    .contains(pick.task)) { throw new IllegalArgumentException("A task can not be added at moste one time!"); }
				
		// min variables
		long minCost = Long.MAX_VALUE;
		int minidxPick = -1;
		int minidxDel = -1;
		Vehicle minV = null;
		
		// iteration variables
		long cost = 0;
		
		for (Vehicle v : allVehicles()) {
			printIfVerbose("Looking at vehicle v: "+v.toString());
			if (pick.task.weight > v.capacity()) {
				continue;
			}
			
			LinkedList<Action> route = routeOf(v);
			if (route.isEmpty()) {
				
				// route is empty -> only one possibility
				// add both at the beginning and calculate cost
				route.addFirst(pick);
				route.addLast(del);
				cost = computeCost();
				
				// update min if smaller
				if (cost < minCost) {
					minCost = cost;
					minidxPick = 0;
					minidxDel = 1;
					minV = v;
				}
				// remove again
				route.clear();
				
			} else {
				ListIterator<Action> itpick = routeOf(v).listIterator();
				ListIterator<Action> itdel = null;
				
				int freeCapacity = v.capacity(); // to keep track of capacity
				Action ppos = null;
				Action dpos = null;
				
				while (itpick.hasNext()) {
					ppos = itpick.next();
					// update capacity
					freeCapacity += ppos.isDelivery() ? ppos.task.weight : -ppos.task.weight;
					if (freeCapacity < 0) {
						// vehicle can not pickup anything at this point
						freeCapacity += ppos.task.weight;
						continue;
					}
					
					// add the pickup
					itpick.add(pick);
					
					// check weight constraint and revert if violated
					if (!checkWeightConstraint(v)) {
						itpick.previous();
						itpick.remove();
						continue;
					}
					
					// init the deliver iterator
					// itdel must iterate over a copy of route. or else concurrent access exception
					itdel = new LinkedList<Action>(routeOf(v)).listIterator(itpick.nextIndex());
					while (itdel.hasNext()) {
						dpos = itdel.next();
						
						// add and calc cost
						itdel.add(del);
						cost = computeCost();
						
						// update min if smaller
						if (cost < minCost) {
							minCost = cost;
							minidxPick = itpick.previousIndex(); // TODO not sure if correct
							minidxDel = itdel.previousIndex(); // TODO not sure if correct
							minV = v;
						}
						
						// remove delivery again
						itdel.previous();
						itdel.remove();
					}
					// remove pickup
					itpick.previous();
					itpick.remove();
				}
			}
		}
		// check if min was found
		if (minV == null || minCost == Long.MAX_VALUE) { return false; }
		
		// actually insert the actions
		routeOf(minV).add(minidxPick, pick);
		routeOf(minV).add(minidxDel, del);
		
		// update cost
		mCost = minCost; // TODO test if correct
		
		// update indexOf
		updateIndexes(minV);
		
		// update vehicles map
		vehicles.put(pick.task, minV);
		
		printIfVerbose("Task(%d) inserted, at pos %d and %d in vehicle(%d) -> cost: %d", pick.task.id, minidxPick, minidxDel, minV.id(), mCost);
		
		return true;
		
	}
	
	/**
	 * 
	 * @param v
	 * @return false iff the constraint is violated
	 */
	private boolean checkWeightConstraint(Vehicle v) {
		LinkedList<Action> route = routeOf(v);
		int free = v.capacity();
		for (Action a : route) {
			free += a.isDelivery() ? a.task.weight : -a.task.weight;
			if (free < 0) { return false; }
		}
		return true;
	}
	
	/**
	 * 
	 * @param v
	 * @return cost of the route of the vehicle
	 */
	private long computeCost(Vehicle v) {
		List<Action> route = routeOf(v);
		long cost = 0;
		if (!route.isEmpty()) {
			cost += distance(v, route.get(0)) * v.costPerKm();
			Action lastAction = route.get(0);
			for (Action act : route) {
				cost += distance(lastAction, act) * v.costPerKm();
				lastAction = act;
			}
		}
		return cost;
	}
	
	private long computeCost() {
		
		double cost = 0;
		for (Vehicle v : allVehicles()) {
			cost += computeCost(v);
		}
		mCost = (long) cost;
		return mCost;
	}
	
	private double distance(Vehicle v, Action a) {
		return (v == null || a == null) ? 0 : v.getCurrentCity().distanceTo(a.actionCity);
	}
	
	private double distance(Action act, Action nextA) {
		return (act == null || nextA == null) ? 0 : act.actionCity.distanceTo(nextA.actionCity);
	}
	
	private Set<Vehicle> allVehicles() {
		return vehicleRoutes.keySet();
	}
	
	private Set<Action> allActions() {
		return indexOf.keySet();
	}
	
	public Set<Task> allTasks() {
		return vehicles.keySet();
	}
	
	private LinkedList<Action> routeOf(Vehicle v) {
		return vehicleRoutes.get(v);
	}
	
	private int indexOf(Action a) {
		return indexOf.get(a);
	}
	
	private Vehicle vehicleDoingTask(Task t) {
		return vehicles.get(t);
	}
	
	public long getCost() {
		return mCost;
	}
	
	/**
	 * removes the task from its route and from the other maps
	 * 
	 * @param t
	 * @return the vehicle the task belonged to
	 */
	public Vehicle remove(Task t) {
		Vehicle v = vehicles.get(t);
		Pickup pick = new Pickup(t);
		Deliver del = new Deliver(t);
		
		boolean rem = vehicleRoutes.get(v).remove(del);
		rem = rem & vehicleRoutes.get(v).remove(pick);
		
		vehicles.remove(t);
		updateIndexes(v);
		
		if (!rem) { throw new IllegalStateException("The routes are inconsistent with the vehicles map"); }
		
		return v;
		
	}
	
	/**
	 * updates the times for the given vehicles plans (Times is 0 indexed, so the
	 * first action happens on time 0)
	 * 
	 * @param a
	 * @param vs
	 */
	private void updateIndexes(Vehicle... vs) {
		for (Vehicle v : vs) {
			int time = 0;
			for (Action act : vehicleRoutes.get(v)) {
				this.indexOf.put(act, time++);
			}
		}
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append("Assignment: \n    vehicRoutes: ").append(vehicleRoutsString())
		    .append("\n    indexOf: ").append(indexOf.toString()).append("\n\n").toString();
	}
	
	public String vehicleRoutsString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Vehicle, LinkedList<Action>> e : vehicleRoutes.entrySet()) {
			sb.append("vehicle ");
			sb.append(e.getKey().id());
			sb.append(": \n    ");
			sb.append(vehicleRoutes.get(e.getKey()).toString());
			sb.append("\n    ");
		}
		return sb.toString();
		
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
					.append("      ")
					.append("(insertion-assignment): ")
					.append(str)
					.toString());
			System.out.flush();
		}
	}
	
	public void enablePrinting(){
		VERBOSE = true;
		printIfVerbose("printing enabled...");
	}
	
	public void disablePrinting(){
		printIfVerbose("...printing disabled.");
		VERBOSE = false;
	}
	
	public InsertionAssignment copy(){
		return new InsertionAssignment(this.copyVehicleRoutes(), this.copyVehicles(), this.copyIndexOf());
		
	}
	
	public Assignment toSlsAssignment(){
		return new Assignment(this.copyVehicleRoutesList(), this.copyVehicles(), this.copyIndexOf());
	}
	
	private Map<Vehicle, LinkedList<Action>> copyVehicleRoutes(){
		Map<Vehicle, LinkedList<Action>> vr = new HashMap<Vehicle, LinkedList<Action>>(vehicleRoutes.size()*2);
		for(Entry<Vehicle, LinkedList<Action>> e : vehicleRoutes.entrySet()){
			vr.put(e.getKey(), new LinkedList<Action>(e.getValue()));
		}
		return vr;
	}
	
	private Map<Task, Vehicle> copyVehicles(){
		Map<Task, Vehicle> vhs = new HashMap<Task, Vehicle>(vehicles.size()*2);
		for(Entry<Task, Vehicle> e : vehicles.entrySet()){
			vhs.put(e.getKey(), e.getValue());
		}
		return vhs;
	}
	
	private Map<Action, Integer> copyIndexOf(){
		Map<Action, Integer> idxOf = new HashMap<Action, Integer>(indexOf.size()*2);
		for(Entry<Action, Integer> e : indexOf.entrySet()){
			idxOf.put(e.getKey(), e.getValue());
		}
		return idxOf;
	}
	
	private Map<Vehicle, List<Action>> copyVehicleRoutesList(){
		Map<Vehicle, List<Action>> vr = new HashMap<Vehicle, List<Action>>(vehicleRoutes.size()*2);
		for(Entry<Vehicle, LinkedList<Action>> e : vehicleRoutes.entrySet()){
			vr.put(e.getKey(), new LinkedList<Action>(e.getValue()));
		}
		return vr;
	}
	
	
}
