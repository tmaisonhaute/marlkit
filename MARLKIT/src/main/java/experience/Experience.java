package experience;

import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import learning.policies.PolicyInput;
import reward.Reward;

/**
 * Represents a single experience. 
 */
public interface Experience {



	/**
	 * Returns the input receive during this experience.
	 *
	 * @return the input
	 */
    public PolicyInput getInput();

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
	 * @return a new Experience object with the same input and reward but a different action
	 */
	public Experience withAction(Action newAction);
	
	/**
	 * Returns a copy of this experiment but with a different input and action.
	 * @param newInput the new input to use
	 * @param newAction the new action to use
	 * @return
	 */
	public Experience withInputAction(PolicyInput newInput, Action newAction);
	
	/**
	 * Creates a centralized experience from an ordered map of agent experiences.
	 *
	 * <p>The centralized experience contains a joint observation, a mapped joint
	 * action and the specified reward.</p>
	 *
	 * @param experiencesByAgent the ordered map associating each agent with its experience
	 * @param centralReward the reward assigned to the centralized experience
	 * @return the centralized experience
	 */
	public Experience createCentralizedExperience(Map<MLKAgent, Experience> experiencesByAgent, Reward centralReward);
}

