package marlkit.trade2d;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;

/**
 * Scheduler for Trade2D experiment episodes and display timing.
 */
public class SchedulerTrade2D extends MLKScheduler {
	public static final int EPISODE_DURATION = 30;
	public static final int MINIMUM_STEP_BEFORE_VIEW = 500;
	public static final int UPDATE_DISPLAY_INTERVAL = 100;
	public static final int DISPLAYED_EPISODES = 1;
	public static final int PAUSE_VALUE = 10;
	public static final int MAXIMUM_EPISODE_COUNT = 5_000;

	/**
	 * Create a scheduler with Trade2D timing criteria.
	 */
	public SchedulerTrade2D() {
		setCriteriaModule(new SchedulerTrade2DCriteria());
	}

	/**
	 * Criteria configuration for Trade2D timing and display rules.
	 */
	class SchedulerTrade2DCriteria extends SchedulerTimedCriteria {

		/**
		 * Initialize all timing and display constraints.
		 */
		public SchedulerTrade2DCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_STEP_BEFORE_VIEW);
			initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_STEP_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}
}
