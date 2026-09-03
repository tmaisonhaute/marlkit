package trainingexecutionstrategy;

import java.util.Collection;

import madkit.kernel.Activator;

/**
 * Defines how agent-related training and execution operations are performed
 * during a simulation.
 *
 * <p>A training-execution strategy determines how agents register their
 * observations, collect experiences, update their policies, and handle the end
 * of an episode. These operations may be performed independently by each agent
 * or involve centralized components, depending on the implementation.</p>
 *
 * <p>The scheduler remains responsible for determining when each operation is
 * performed. The strategy determines how the operation is carried out and
 * provides the MaDKit activators required for its implementation.</p>
 */
public interface TrainingExecutionStrategy {

    /**
     * Initializes the strategy and creates the activators required to operate
     * on the specified model group.
     *
     * <p>This method must be called before any of the strategy operations or
     * {@link #getActivators()} are used.</p>
     *
     * @param modelGroup the MaDKit group containing the agents involved in the
     *                   strategy
     */
    void activate(String modelGroup);

    /**
     * Performs the observation registration phase for the agents managed by
     * this strategy.
     */
    void agentsMakeObservation();
    
	/**
	 * Performs the action execution phase for the agents managed by this strategy.
	 */
    void agentsAct();

    /**
     * Performs the experience collection phase for the agents managed by this
     * strategy.
     */
    void agentsCollectExperience();

    /**
     * Performs the policy update phase for the agents managed by this strategy.
     *
     * @param simulationStep the index of the current simulation step
     */
    void agentsUpdatePolicy(int simulationStep);

    /**
     * Performs the agent-specific processing required at the end of an episode.
     */
    void agentsEndEpisode();

    /**
     * Returns the MaDKit activators used by this strategy.
     *
     * <p>The returned activators are intended to be registered with the
     * simulation scheduler after the strategy has been initialized.</p>
     *
     * @return the activators required by this strategy
     */
    Collection<Activator> getActivators();
}