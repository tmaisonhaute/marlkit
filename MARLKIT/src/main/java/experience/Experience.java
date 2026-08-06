package experience;

import agent.action.Action;
import learning.policies.PolicyInput;
import reward.Reward;

/**
 * Represents a single experience tuple (observation, action, reward) in reinforcement learning.
 */
public class Experience {
    protected PolicyInput input;
    protected Action action;
    protected Reward reward;

	/**
	 * Creates a new experience.
	 *
	 * @param input the PolicyInput received
	 * @param action the action taken
	 * @param reward the reward received
	 */
    public Experience(PolicyInput input, Action action, Reward reward) {
        this.input = input;
        this.action = action;
        this.reward = reward;
    }

	/**
	 * Returns the input receive during this experience.
	 *
	 * @return the input
	 */
    public PolicyInput getInput() {
        return input;
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
	
	/**
	 * Returns a copy of this experiment but with a different action. 
	 * @param newAction the new action to use
	 * @return a new Experience object with the same input and reward but a different action
	 */
	public Experience withAction(Action newAction) {
	    return new Experience(input, newAction, reward);
	}
}