package learning;

import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;

/**
 * Represents a single experience tuple (observation, action, reward) in reinforcement learning.
 */
public class Experience {
    private Observation observation;
    private Action action;
    private Reward reward;

	/**
	 * Creates a new experience.
	 *
	 * @param observation the observation received
	 * @param action the action taken
	 * @param reward the reward received
	 */
    public Experience(Observation observation, Action action, Reward reward) {
        this.observation = observation;
        this.action = action;
        this.reward = reward;
    }

	/**
	 * Returns the observation from this experience.
	 *
	 * @return the observation
	 */
    public Observation getObservation() {
        return observation;
    }

	/**
	 * Returns the action from this experience.
	 *
	 * @return the action
	 */
    public Action getAction() {
        return action;
    }

	/**
	 * Returns the reward from this experience.
	 *
	 * @return the reward
	 */
    public Reward getReward() {
        return reward;
    }

	/**
	 * Returns the numeric value of the reward.
	 *
	 * @return the reward value
	 */
	public Double getRewardValue() {
		return reward.getValue();
	}
}