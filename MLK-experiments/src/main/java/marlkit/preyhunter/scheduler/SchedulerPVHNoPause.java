package marlkit.preyhunter.scheduler;

import simulation.SchedulerTimedCriteria;
import util.criteria.AlwaysMet;
import util.criteria.Criteria;

public class SchedulerPVHNoPause extends SchedulerPVH {

	public SchedulerPVHNoPause() {
		super();
		SchedulerTimedCriteria schedulerCriteria = (SchedulerTimedCriteria) getCriteriaModule();
		schedulerCriteria.setCriteriaStartDisplay(Criteria.not(new AlwaysMet()));
		schedulerCriteria.setCriteriaEndDisplay(new AlwaysMet());
	}
}
