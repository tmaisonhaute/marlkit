package experience;

import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import reward.Reward;

/**
 * Represents a single experience. 
 */
public interface Experience {



	/**
	 * Returns the observation received during this experience.
	 *
	 * @return the observation
	 */
    public Observation getObservation();

	/**
	 * Returns the action from this experience.
	 *
	 * @return the action
	 */
    public Action getAction();

	/**
	 * Returns the reward from this experience.
	 *
	 * @return the reward
	 */
    public Reward getReward();
	/**
	 * Returns the numeric value of the reward.
	 *
	 * @return the reward value
	 */
	public Double getRewardValue();
	
	/**
	 * Returns a copy of this experiment but with a different action. 
	 * @param newAction the new action to use
	 * @return a new Experience object with the same observation and reward but a different action
	 */
	public Experience withAction(Action newAction);
	
	/**
	 * Returns a copy of this experiment but with a different observation and action.
	 * @param newObservation the new observation to use
	 * @param newAction the new action to use
	 * @return a new Experience object with the same reward but a different observation and action
	 */
	public Experience withObservationAction(Observation newObservation, Action newAction);
	
	/**
	 * Creates a centralized experience from an ordered map of agent experiences.
	 *
	 * @param experiencesByAgent the ordered map associating each agent with its experience
	 * @param centralReward the reward assigned to the centralized experience
	 * @return the centralized experience
	 */
	public Experience createCentralizedExperience(Map<MLKAgent, Experience> experiencesByAgent, Reward centralReward);
}

