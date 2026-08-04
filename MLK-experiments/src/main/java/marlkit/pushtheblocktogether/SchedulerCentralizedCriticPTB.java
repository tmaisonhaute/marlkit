package marlkit.pushtheblocktogether;

import centralizedtraining.CentralizedCriticTrainingExecutionStrategy;
import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;

public class SchedulerCentralizedCriticPTB extends MLKScheduler{
	public static final int EPISODE_DURATION = 100;
	public static final int MINIMUM_EPISODES_BEFORE_VIEW = 5_000;
	public static final int UPDATE_DISPLAY_INTERVAL = 1000;
	public static final int DISPLAYED_EPISODES = 1;
	public static final int PAUSE_VALUE = 50;
	public static final int MAXIMUM_EPISODE_COUNT = 100_000;

	public SchedulerCentralizedCriticPTB() {
		setCriteriaModule(new SchedulerPTBCriteria());
		setTrainingExecutionStrategy(new CentralizedCriticTrainingExecutionStrategy());
	}

	class SchedulerPTBCriteria extends SchedulerTimedCriteria {

		public SchedulerPTBCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW);
			initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}
}
