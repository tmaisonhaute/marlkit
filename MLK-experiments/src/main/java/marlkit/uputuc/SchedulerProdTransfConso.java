package marlkit.uputuc;

import simulation.TimedScheduler;
import util.criteria.AlwaysMet;
import util.criteria.Criteria;
import util.criteria.ReachTimeCriterion;

public class SchedulerProdTransfConso extends TimedScheduler {

	public static final int EPISODE_DURATION = 100;
	public static final int MAXIMUM_EPISODE_COUNT = 1_000;

	public SchedulerProdTransfConso() {
		initEpisodeDuration(EPISODE_DURATION);
		setCriteriaStartDisplay(new AlwaysMet());
		setCriteriaEndDisplay(Criteria.not(new AlwaysMet()));
		setCriteriaEndSimulation(new ReachTimeCriterion(MAXIMUM_EPISODE_COUNT));
	}

}
