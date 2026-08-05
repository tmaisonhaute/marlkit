package learning;

import agent.MLKAgent;
import experience.Experience;

/**
 * Defines a critic used by an actor-critic learning algorithm.
 *
 * <p>A critic estimates the value associated with an agent's experience and
 * provides the learning signal used to update the actor. It may optionally
 * associate an original agent experience with an enriched experience containing
 * additional information required for critic training, such as centralized
 * observations or joint actions.</p>
 */
public interface Critic {

    /**
     * Initializes the critic for the specified agent.
     *
     * @param agent the agent whose learning algorithm uses this critic
     */
    void init(MLKAgent agent);

    /**
     * Associates an original agent experience with an experience enriched with
     * critic-specific information.
     *
     * <p>The enriched experience may, for example, contain centralized
     * observations or joint actions while the original experience retains the
     * local information used by the actor.</p>
     *
     * @param originalExperience the original experience collected by the agent
     * @param enrichedExperience the corresponding critic-specific experience
     */
    void enrichExperience(Experience originalExperience, Experience enrichedExperience);

    /**
     * Returns the critic-specific experience associated with an original
     * experience.
     *
     * @param originalExperience the original agent experience
     * @return the associated enriched experience, or {@code null} if none exists
     */
    Experience getEnrichedExperience(Experience originalExperience);

    /**
     * Removes the critic-specific experience associated with an original
     * experience.
     *
     * @param originalExperience the original agent experience whose association
     *                           must be removed
     */
    void removeEnrichedExperience(Experience originalExperience);

    /**
     * Removes all associations between original and enriched experiences
     * maintained by this critic.
     */
    void clearEnrichedExperiences();
}