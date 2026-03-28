package marlkit.teamsurround;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;
import util.criteria.Criteria;
import util.criteria.ReachTimeCriterion;

/**
 * Scheduler configuration for TeamSurround.
 */
public class SchedulerTeamSurround extends MLKScheduler {

	public static final int EPISODE_DURATION = 100;
	public static final int MINIMUM_STEP_BEFORE_VIEW = 500;
	public static final int UPDATE_INTERVAL = 100;
	public static final int DISPLAYED_EPISODES = 1;
	public static final int PAUSE_VALUE = 100;
	public static final int MAXIMUM_EPISODE_COUNT = 50_000;

	public SchedulerTeamSurround() {
		setCriteriaModule(new SchedulerTeamSurroundCriteria());
	}

	class SchedulerTeamSurroundCriteria extends SchedulerTimedCriteria {

		public SchedulerTeamSurroundCriteria() {
			initEpisodeDuration(EPISODE_DURATION);
			setCriteriaEndEpisode(Criteria.or(new ReachTimeCriterion(EPISODE_DURATION), new TeamSurroundTerminalCriterion()));
			initStartDisplay(UPDATE_INTERVAL, MINIMUM_STEP_BEFORE_VIEW);
			initEndDisplay(UPDATE_INTERVAL, MINIMUM_STEP_BEFORE_VIEW, DISPLAYED_EPISODES);
			initEndSimulation(MAXIMUM_EPISODE_COUNT);
			setPauseDisplayValue(PAUSE_VALUE);
		}
	}
}
