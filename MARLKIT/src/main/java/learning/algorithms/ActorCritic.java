package learning.algorithms;

import agent.MLKAgent;
import learning.Algorithm;
import learning.Critic;
import learning.policies.ActorNetwork;

/**
 * Defines an actor-critic learning algorithm composed of an actor and a critic.
 *
 * <p>The actor represents the policy used by the agent to select actions,
 * whereas the critic estimates a value used to guide the actor's learning.
 * Implementations determine how the critic's estimates are computed and used
 * to update the actor.</p>
 *
 * <p>The actor is expected to be the policy returned by
 * {@link #getPolicy()} and is initialized separately by the agent. Consequently,
 * the default {@link #init(MLKAgent)} implementation initializes only the
 * algorithm-agent association and the critic, avoiding a second initialization
 * of the actor.</p>
 */
public interface ActorCritic extends Algorithm {

    /**
     * Returns the actor used to select actions.
     *
     * <p>The returned actor is expected to be the same policy instance as the
     * one returned by {@link #getPolicy()}.</p>
     *
     * @return the actor used by this algorithm
     */
    ActorNetwork getActor();

    /**
     * Returns the critic used to estimate values and provide a learning signal
     * to the actor.
     *
     * @return the critic used by this algorithm
     */
    Critic getCritic();

    /**
     * Initializes this actor-critic algorithm for the specified agent.
     *
     * <p>This method associates the algorithm with the agent and initializes
     * the critic. It does not initialize the actor because the actor is expected
     * to be initialized separately as the agent's policy.</p>
     *
     * @param agent the agent using this actor-critic algorithm
     */
    @Override
    default void init(MLKAgent agent) {
        setAgent(agent);
        getCritic().init(agent);
    }
}
