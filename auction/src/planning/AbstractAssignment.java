package planning;

import java.util.List;

import logist.plan.Plan;
import logist.simulation.Vehicle;

public abstract class AbstractAssignment {
	public abstract List<Plan> generatePlans(List<Vehicle> vehics);
}
