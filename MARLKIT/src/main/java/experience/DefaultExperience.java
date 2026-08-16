package experience;

import java.util.Map;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import environment.observation.MappedJointObservation;
import environment.observation.Observation;
import learning.policies.PolicyInput;
import reward.Reward;

/**
 * Represents a single experience tuple (observation, action, reward) in reinforcement learning.
 */
public class DefaultExperience implements Experience{
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
    public DefaultExperience(PolicyInput input, Action action, Reward reward) {
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
	@Override
	public DefaultExperience withAction(Action newAction) {
	    return new DefaultExperience(input, newAction, reward);
	}
	
	/**
	 * Returns a copy of this experiment but with a different input and action.
	 * @param newInput the new input to use
	 * @param newAction the new action to use
	 * @return
	 */
	@Override
	public DefaultExperience withInputAction(PolicyInput newInput, Action newAction) {
		return new DefaultExperience(newInput, newAction, reward);
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NullPointerException if an argument is {@code null}
	 * @throws IllegalArgumentException if an experience input is not an observation
	 */
	@Override
	public DefaultExperience createCentralizedExperience(Map<MLKAgent, Experience> experiencesByAgent, Reward centralReward) {
	    Objects.requireNonNull(experiencesByAgent, "experiencesByAgent");
	    Objects.requireNonNull(centralReward, "centralReward");

	    MappedJointObservation jointObservation = new MappedJointObservation();
	    MappedJointAction jointAction = new MappedJointAction();

	    for (Map.Entry<MLKAgent, Experience> entry : experiencesByAgent.entrySet()) {
	        MLKAgent agent = entry.getKey();
	        Experience experience = entry.getValue();

	        if (!(experience.getInput() instanceof Observation observation)) {
	            throw new IllegalArgumentException("Centralized experiences require Observation inputs.");
	        }

	        jointObservation.addObservation(agent, observation);
	        jointAction.addAction(agent, experience.getAction());
	    }

	    return new DefaultExperience(jointObservation, jointAction, centralReward);
	}
}