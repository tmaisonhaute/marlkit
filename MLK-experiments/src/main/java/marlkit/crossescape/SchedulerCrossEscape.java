package marlkit.crossescape;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;
import util.criteria.Criteria;
import util.criteria.ReachTimeCriterion;

/**
 * Scheduler configuration for the CrossEscape experiment.
 */
public class SchedulerCrossEscape extends MLKScheduler {

	public static final int EPISODE_DURATION = 80;
	public static final int MINIMUM_EPISODES_BEFORE_VIEW = 0*1_000;
	public static final int UPDATE_DISPLAY_INTERVAL = 1_000;
	public static final int DISPLAYED_EPISODES = 1;
	public static final int PAUSE_VALUE = 100;
	public static final int MAXIMUM_EPISODE_COUNT = 100_000;

	public SchedulerCrossEscape() {
		setCriteriaModule(new SchedulerCrossEscapeCriteria());
		
	}

	class SchedulerCrossEscapeCriteria extends SchedulerTimedCriteria {
		public SchedulerCrossEscapeCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			setCriteriaEndEpisode(Criteria.or(new ReachTimeCriterion(EPISODE_DURATION), new CrossEscapeTerminalCriterion()));
			initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW);
			initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			initEvaluationCriterion();
			setPauseDisplayValue(PAUSE_VALUE);
		}
			
	}
}
