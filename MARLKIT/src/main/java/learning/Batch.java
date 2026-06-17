package learning;
import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import learning.policies.PolicyInput;
import reward.Reward;

/**
 * Represents a batch of data used for learning.
 */
public class Batch {
	private List<Experience> experiences;
	
    /**
     * Constructs an empty Batch.
     */
    public Batch() {
    	experiences = new ArrayList<>();
    }

    /**
     * Constructs a Batch with the specified experiences.
     *
     * @param experiences the list of experiences
     */
    public Batch(List<Experience> experiences) {
        this.experiences = new ArrayList<>(experiences);
    }

    // Getters
	/**
	 * Returns the list of experiences.
	 *
	 * @return the list of experiences
	 */
	public List<Experience> getExperiences() {
		return experiences;
	}
	
	/**
     * Adds an experience to the batch.
     *
     * @param input the PolicyInput to add
     * @param act the action to add
     * @param rew the reward to add
     */
    public void addExperience(PolicyInput input, Action act, Reward rew) {
        experiences.add(new Experience(input, act, rew));
    }
    /**
     * Adds an experience to the batch.
     *
     * @param experience the experience
     */
	public void addExperience(Experience experience) {
		experiences.add(experience);
	}
	
	/**
	 * Clears the batch.
	 */
	public void clear() {
		experiences.clear();
	}
	
	/**
	 * Returns the size of the batch.
	 * @return 
	 */
	public int size() {
		return experiences.size();
	}
	
	/**
	 * Returns an array of all inputs in the batch.
	 * @return an array of all inputs
	 */
	public PolicyInput[] getAllInputs() {
		PolicyInput[] inputs = new PolicyInput[experiences.size()];
		for (int i = 0; i < experiences.size(); i++) {
			inputs[i] = experiences.get(i).getInput();
		}
		return inputs;
	}
	/**
	 * Returns an array of all actions in the batch.
	 * @return an array of all actions
	 */
	public Action[] getAllActions() {
		Action[] actions = new Action[experiences.size()];
		for (int i = 0; i < experiences.size(); i++) {
			actions[i] = experiences.get(i).getAction();
		}
		return actions;
	}

	/**
	 * Returns an array of all rewards in the batch.
	 * @return an array of all rewards
	 */
	public Reward[] getAllRewards() {
		Reward[] rewards = new Reward[experiences.size()];
		for (int i = 0; i < experiences.size(); i++) {
			rewards[i] = experiences.get(i).getReward();
		}
		return rewards;
	}
	
	/**
     * Compute cumulative rewards for a all experiences.
     * @param logger the agent logger
     * @param gamma the discount factor
     * @return an array of cumulative rewards
     */
    public double[] computeCumulativeRewards(double gamma) {
    	int experiencesLength = experiences.size();
    	double[] cumulativeRewards = new double[experiencesLength];
    	for (int j = experiencesLength - 1; j >= 0; j--) {
    		cumulativeRewards[j] = experiences.get(j).getRewardValue() + (j + 1 < experiencesLength ? cumulativeRewards[j + 1] * gamma : 0);
    	}
    	return cumulativeRewards;
    }
    
    public double totalRewards() {
		double totalRewards = 0;
		for (Experience experience : experiences) {
			totalRewards += experience.getRewardValue();
		}
		return totalRewards;
    }

	
}


