package marlkit.preyhunter.scheduler;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;
import util.criteria.Criteria;
import util.criteria.ReachTimeCriterion;

public class SchedulerPVH extends MLKScheduler {

    public static final int EPISODE_DURATION = 100;
    public static final int MINIMUM_EPISODES_BEFORE_VIEW = 100_1000;
    public static final int VIEWER_UPDATE_DISPLAY_INTERVAL = 1000;
	public static final int DISPLAYED_EPISODES = 1;
    public static final int PAUSE_VALUE = 50;
	public static final int MAXIMUM_EPISODE_COUNT = 50_000;

	public SchedulerPVH() {
		setCriteriaModule(new SchedulerPVHCriteria());
	}

	class SchedulerPVHCriteria extends SchedulerTimedCriteria {

		public SchedulerPVHCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			setCriteriaEndEpisode(Criteria.or(new ReachTimeCriterion(EPISODE_DURATION), new PreyHunterTerminalCriterion()));
			initStartDisplay(VIEWER_UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW);
			initEndDisplay(VIEWER_UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}
}
