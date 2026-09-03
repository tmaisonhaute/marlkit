package marlkit.listenorgo;

import simulation.MLKScheduler;
import simulation.SchedulerTimedCriteria;

/**
 * Scheduler for the ListenOrGo simulation.
 * <p>
 * Defines the timing criteria that govern episode resets, display activation,
 * and the end-of-simulation condition. All timing constants are exposed as
 * {@code public static final} fields for external configuration.
 * </p>
 */
public class SchedulerListenOrGo extends MLKScheduler {

    public static final int EPISODE_DURATION = 5;
    public static final int MINIMUM_EPISODES_BEFORE_VIEW = 100;
    public static final int UPDATE_DISPLAY_INTERVAL = 1;
	public static final int DISPLAYED_EPISODES = 1;
    public static final int PAUSE_VALUE = 500;
    public static final int MAXIMUM_EPISODE_COUNT = 100_000;
    
	public SchedulerListenOrGo() {
        setCriteriaModule(new SchedulerListenOrGoCriteria());
    }

    class SchedulerListenOrGoCriteria extends SchedulerTimedCriteria {

        public SchedulerListenOrGoCriteria() {
            initEpisodeDuration(EPISODE_DURATION);
            initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW);
            initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_EPISODES_BEFORE_VIEW, DISPLAYED_EPISODES);
            initEndSimulation(MAXIMUM_EPISODE_COUNT);
            initEvaluationCriterion();
            setPauseDisplayValue(PAUSE_VALUE);
        }
	}
}
