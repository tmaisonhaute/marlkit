package marlkit.trade;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;

public class SchedulerTrade extends MLKScheduler {
	
	public static final int EPISODE_DURATION = 20;
	public static final int MINIMUM_STEP_BEFORE_VIEW = 500;
	public static final int UPDATE_DISPLAY_INTERVAL = 50;
	public static final int DISPLAYED_EPISODES = 1;
	public static final int PAUSE_VALUE = 50;
	public static final int MAXIMUM_EPISODE_COUNT = 2_000;

	public SchedulerTrade() {
		setCriteriaModule(new SchedulerTradeCriteria());
	}

	class SchedulerTradeCriteria extends SchedulerTimedCriteria {

		public SchedulerTradeCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_STEP_BEFORE_VIEW);
			initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_STEP_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}

}
