package experience;

import java.util.Map;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import environment.observation.MappedJointObservation;
import environment.observation.Observation;
import reward.Reward;

/**
 * Represents a single experience tuple (observation, action, reward) in reinforcement learning.
 */
public class DefaultExperience implements Experience{
    protected Observation observation;
    protected Action action;
    protected Reward reward;

	/**
	 * Creates a new experience.
	 *
	 * @param observation the Observation received
	 * @param action the action taken
	 * @param reward the reward received
	 */
    public DefaultExperience(Observation observation, Action action, Reward reward) {
        this.observation = observation;
        this.action = action;
        this.reward = reward;
    }

	/**
	 * Returns the observation received during this experience.
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
	
	/**
	 * Returns a copy of this experiment but with a different action. 
	 * @param newAction the new action to use
	 * @return a new Experience object with the same observation and reward but a different action
	 */
	@Override
	public DefaultExperience withAction(Action newAction) {
	    return new DefaultExperience(observation, newAction, reward);
	}
	
	/**
	 * Returns a copy of this experiment but with a different observation and action.
	 * @param newObservation the new observation to use
	 * @param newAction the new action to use
	 * @return 
	 */
	@Override
	public DefaultExperience withObservationAction(Observation newObservation, Action newAction) {
		return new DefaultExperience(newObservation, newAction, reward);
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NullPointerException if an argument is {@code null}
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

	        jointObservation.addObservation(agent, observation);
	        jointAction.addAction(agent, experience.getAction());
	    }

	    return new DefaultExperience(jointObservation, jointAction, centralReward);
	}
}