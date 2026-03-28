package marlkit.preyVsHunter;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;

public class SchedulerPVH extends MLKScheduler {

    public static final int EPISODE_DURATION = 100;
    public static final int MINIMUM_STEP_BEFORE_VIEW = 1000;
    public static final int VIEWER_UPDATE_INTERVAL = 100;
	public static final int DISPLAYED_EPISODES = 1;
    public static final int PAUSE_VALUE = 50;
	public static final int MAXIMUM_EPISODE_COUNT = 10_000;

	public SchedulerPVH() {
		setCriteriaModule(new SchedulerPVHCriteria());
	}

	class SchedulerPVHCriteria extends SchedulerTimedCriteria {

		public SchedulerPVHCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			initStartDisplay(VIEWER_UPDATE_INTERVAL, MINIMUM_STEP_BEFORE_VIEW);
			initEndDisplay(VIEWER_UPDATE_INTERVAL, MINIMUM_STEP_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}
}
