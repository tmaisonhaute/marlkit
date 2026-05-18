package evaluation;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import reward.ReactionEvent;
/**
 * Computes and stores system-level evaluation measures during an episode.
 * <p>
 * A {@code SystemEvaluator} is called at each environment step through
 * {@link #evaluate(Map)}. Implementations may use these calls to accumulate
 * intermediate information from reaction events.
 * </p>
 * <p>
 * At the end of an episode, {@link #onEpisodeEnd()} is called before
 * {@link #getEpisodeMeasures()}. This allows implementations to perform
 * final computations that require the whole episode history, such as averages,
 * success rates, convergence indicators, or global coordination metrics.
 * </p>
 * <p>
 * After the episode measures have been collected by the environment,
 * {@link #reset()} is called to clear the internal state and prepare the
 * evaluator for the next episode.
 * </p>
 */
public interface SystemEvaluator {

    /**
     * Updates the evaluator with the reaction events produced during one
     * environment step.
     * <p>
     * This method is intended for accumulating information over the episode.
     * It should not reset episode-level state.
     * </p>
     *
     * @param reactionEvents the reaction events generated for each agent at the current step
     */
    public void evaluate(Map<MLKAgent, List<ReactionEvent>> reactionEvents);

    /**
     * Performs optional final computations at the end of an episode.
     * <p>
     * This method is called after the last step of the episode and before
     * {@link #getEpisodeMeasures()}. Implementations can use it to compute
     * final episode-level measures from data accumulated during
     * {@link #evaluate(Map)}.
     * </p>
     * <p>
     * This method should not clear the evaluator state. Cleanup should be done
     * in {@link #reset()}.
     * </p>
     */
    default void onEpisodeEnd() {}

    /**
     * Returns the measures computed for the completed episode.
     * <p>
     * This method is called after {@link #onEpisodeEnd()} and before
     * {@link #reset()}. The returned measures are logged as episode-level
     * extras.
     * </p>
     *
     * @return the list of measures computed for the completed episode
     */
    public List<Measure> getEpisodeMeasures();

    /**
     * Returns the names of the measures produced by this evaluator, in the
     * order in which they should appear in logs.
     * <p>
     * Each name should match the {@code toString()} value of the corresponding
     * {@link Measure} returned by {@link #getEpisodeMeasures()}.
     * </p>
     *
     * @return the ordered list of measure names
     */
    public List<String> getMeasureNames();

    /**
     * Clears the internal state of this evaluator.
     * <p>
     * This method is called after episode measures have been collected and the
     * episode has been finalized. Implementations should use it to prepare the
     * evaluator for the next episode.
     * </p>
     */
    public void reset();

    /**
     * Called when the simulation ends.
     * <p>
     * This hook can be used to release resources or perform final cleanup.
     * It is not intended to compute per-episode measures.
     * </p>
     */
    default void onSimulationEnd() {}
}