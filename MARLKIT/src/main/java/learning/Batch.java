package learning;
import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import environment.reward.Reward;
import learning.policy.PolicyInput;

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
}


