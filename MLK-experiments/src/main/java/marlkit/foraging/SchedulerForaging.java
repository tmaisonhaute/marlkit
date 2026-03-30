package marlkit.foraging;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;
import util.criteria.Criteria;
import util.criteria.ReachTimeCriterion;

public class SchedulerForaging extends MLKScheduler {
	
	public static final int EPISODE_DURATION = 100;
	public static final int MINIMUM_STEP_BEFORE_VIEW = 100_000;
	public static final int UPDATE_DISPLAY_INTERVAL = 50_000;
	public static final int DISPLAYED_EPISODES = 2;
	public static final int PAUSE_VALUE = 300;
	public static final int MAXIMUM_EPISODE_COUNT = 2000_000;

	public SchedulerForaging() {
		setCriteriaModule(new SchedulerForagingCriteria());
	}

	class SchedulerForagingCriteria extends SchedulerTimedCriteria {

		public SchedulerForagingCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			setCriteriaEndEpisode(Criteria.or(new ReachTimeCriterion(EPISODE_DURATION), new AllCollectedCriterion()));
			initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_STEP_BEFORE_VIEW);
			initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_STEP_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}

}
