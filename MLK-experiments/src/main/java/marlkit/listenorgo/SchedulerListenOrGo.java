package marlkit.listenorgo;

import simulation.MLKScheduler;
import util.criteria.Criteria;
import util.criteria.Criterion;
import util.criteria.ModuloTimeCriterion;
import util.criteria.ReachTimeCriterion;

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
    public static final int UPDATE_INTERVAL = 1;
	/** Number of consecutive episodes rendered during each display phase. */
	public static final int DISPLAYED_EPISODES = 1;
    /** Duration in milliseconds of the pause inserted between display updates. */
    public static final int PAUSE_DISPLAY_VALUE = 500;
    /** Maximum number of episodes before the simulation terminates. */
    public static final int MAXIMUM_EPISODE_COUNT = 100_000;
    
    private final Criterion criteriaEndEpisode = new ReachTimeCriterion(EPISODE_DURATION);
    private final Criterion criteriaStartDisplay = Criteria.and(
        new ModuloTimeCriterion(UPDATE_INTERVAL), 
        new ReachTimeCriterion(MINIMUM_STEP_BEFORE_VIEW)
    );
    private final Criterion criteriaEndDisplay = Criteria.and(
        new ModuloTimeCriterion(UPDATE_INTERVAL, DISPLAYED_EPISODES), 
        new ReachTimeCriterion(MINIMUM_STEP_BEFORE_VIEW)
    );
    private final Criterion criteriaEndSimulation = new ReachTimeCriterion(MAXIMUM_EPISODE_COUNT);
    
    /**
     * Returns the criterion that triggers the end of an episode.
     * The episode ends after {@link #EPISODE_DURATION} steps.
     *
     * @return the end-of-episode criterion.
     */
    @Override
    public Criterion getCriteriaEndEpisode() {
        return criteriaEndEpisode;
    }
    
    /**
     * Returns the criterion that activates the graphical display.
     * The display starts after {@link #MINIMUM_STEP_BEFORE_VIEW} episodes,
     * then updates every {@link #UPDATE_INTERVAL} episodes.
     *
     * @return the start-display criterion.
     */
    @Override
    public Criterion getCriteriaStartDisplay() {
        return criteriaStartDisplay;
    }
    
    /**
     * Returns the criterion that deactivates the graphical display after each
     * rendering phase of {@link #DISPLAYED_EPISODES} episodes.
     *
     * @return the end-display criterion.
     */
    @Override
    public Criterion getCriteriaEndDisplay() {
        return criteriaEndDisplay;
    }
    
    /**
     * Returns the criterion that terminates the simulation.
     * The simulation stops after {@link #MAXIMUM_EPISODE_COUNT} episodes.
     *
     * @return the end-of-simulation criterion.
     */
    @Override
    public Criterion getCriteriaEndSimulation() {
        return criteriaEndSimulation;
    }
    
    /**
     * Returns the duration in milliseconds of the pause inserted between
     * display updates.
     *
     * @return {@link #PAUSE_DISPLAY_VALUE}.
     */
    @Override
    protected int getPauseDisplayValue() {
        return PAUSE_DISPLAY_VALUE;
    }
}
