package marlkit.foraging;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;
import util.criteria.Criteria;
import util.criteria.ReachTimeCriterion;

public class SchedulerForaging extends MLKScheduler {
	
	public static final int EPISODE_DURATION = 100;
	public static final int MINIMUM_EPISODES_BEFORE_VIEW = 100_000;
	public static final int UPDATE_DISPLAY_INTERVAL = 10_000;
	public static final int DISPLAYED_EPISODES = 1;
	public static final int PAUSE_VALUE = 100;
	public static final int MAXIMUM_EPISODE_COUNT = 300_000;

	public SchedulerForaging() {
		setCriteriaModule(new SchedulerForagingCriteria());
	}

	class SchedulerForagingCriteria extends SchedulerTimedCriteria {

		public SchedulerForagingCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			setCriteriaEndEpisode(Criteria.or(new ReachTimeCriterion(EPISODE_DURATION), new AllCollectedCriterion()));
			initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW);
			initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}

}
