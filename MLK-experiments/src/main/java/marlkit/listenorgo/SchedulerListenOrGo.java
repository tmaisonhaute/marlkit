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

    /** Number of steps per episode. */
    public static final int EPISODE_DURATION = 5;
    /** Minimum number of episodes that must elapse before the display is activated. */
    public static final int MINIMUM_STEP_BEFORE_VIEW = 100;
    /** Interval (in episodes) between consecutive display updates. */
    public static final int UPDATE_DISPLAY_INTERVAL = 1;
	/** Number of consecutive episodes rendered during each display phase. */
	public static final int DISPLAYED_EPISODES = 1;
    /** Duration in milliseconds of the pause inserted between display updates. */
    public static final int PAUSE_VALUE = 500;
    /** Maximum number of episodes before the simulation terminates. */
    public static final int MAXIMUM_EPISODE_COUNT = 100_000;
    
	public SchedulerListenOrGo() {
        setCriteriaModule(new SchedulerListenOrGoCriteria());
    }

    class SchedulerListenOrGoCriteria extends SchedulerTimedCriteria {

        public SchedulerListenOrGoCriteria() {
            initEpisodeDuration(EPISODE_DURATION);
            initStartDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_STEP_BEFORE_VIEW);
            initEndDisplay(UPDATE_DISPLAY_INTERVAL, MINIMUM_STEP_BEFORE_VIEW, DISPLAYED_EPISODES);
            initEndSimulation(MAXIMUM_EPISODE_COUNT);
            setPauseDisplayValue(PAUSE_VALUE);
        }
	}
}
