package marlkit.gooryield;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;

public class SchedulerGoOrYield extends MLKScheduler {

	public static final int EPISODE_DURATION = 1;
	public static final int MINIMUM_EPISODES_BEFORE_VIEW = 0;
	public static final int UPDATE_DISPLAY_INTERVAL = 1;
	public static final int DISPLAYED_EPISODES = 1;
	public static final int PAUSE_VALUE = 0;
	public static final int MAXIMUM_EPISODE_COUNT = 100_000;

	public SchedulerGoOrYield() {
		setCriteriaModule(new SchedulerGoOrYieldCriteria());
	}

	class SchedulerGoOrYieldCriteria extends SchedulerTimedCriteria {
		public SchedulerGoOrYieldCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW);
			initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			initEvaluationCriterion();
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}
}
