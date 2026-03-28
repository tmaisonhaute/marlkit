package marlkit.uputuc;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;
import util.criteria.AlwaysMet;
import util.criteria.Criteria;
import util.criteria.ReachTimeCriterion;

public class SchedulerProdTransfConso extends MLKScheduler {

	public static final int EPISODE_DURATION = 100;
	public static final int MAXIMUM_EPISODE_COUNT = 1_000;

	public SchedulerProdTransfConso() {
		setCriteriaModule(new SchedulerProdTransfConsoCriteria());
	}

	class SchedulerProdTransfConsoCriteria extends SchedulerTimedCriteria {

		public SchedulerProdTransfConsoCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			setCriteriaStartDisplay(new AlwaysMet());
			setCriteriaEndDisplay(Criteria.not(new AlwaysMet()));
			setCriteriaEndSimulation(new ReachTimeCriterion(MAXIMUM_EPISODE_COUNT));
		}
	}

}
