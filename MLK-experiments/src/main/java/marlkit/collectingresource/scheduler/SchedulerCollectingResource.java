package marlkit.collectingresource.scheduler;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;

/**
 * Scheduler for Trade2D experiment episodes and display timing.
 */
public class SchedulerCollectingResource extends MLKScheduler {
	public static final int EPISODE_DURATION = 30;
	public static final int MINIMUM_EPISODES_BEFORE_VIEW = 500;
	public static final int UPDATE_DISPLAY_INTERVAL = 100;
	public static final int DISPLAYED_EPISODES = 1;
	public static final int PAUSE_VALUE = 10;
	public static final int MAXIMUM_EPISODE_COUNT = 5_000;

	/**
	 * Create a scheduler with Trade2D timing criteria.
	 */
	public SchedulerCollectingResource() {
		setCriteriaModule(new SchedulerCollectingResourceCriteria());
	}

	/**
	 * Criteria configuration for Trade2D timing and display rules.
	 */
	class SchedulerCollectingResourceCriteria extends SchedulerTimedCriteria {

		/**
		 * Initialize all timing and display constraints.
		 */
		public SchedulerCollectingResourceCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW);
			initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}
}
