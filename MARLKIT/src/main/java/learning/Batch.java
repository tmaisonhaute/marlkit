package learning;
import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;

/**
 * Represents a batch of data used for learning.
 */
public class Batch {
//	private List<Observation> observations;
//	private List<Action> actions;
//	private List<Reward> rewards;
	private List<Experience> experiences;
	
    /**
     * Constructs an empty Batch.
     */
    public Batch() {
    	experiences = new ArrayList<>();
//        observations = new ArrayList<Observation>();
//        actions = new ArrayList<Action>();
//        rewards = new ArrayList<Reward>();
    }

    /**
     * Constructs a Batch with the specified experiences.
     *
     * @param experiences the list of experiences
     */
    public Batch(List<Experience> experiences) {
        this.experiences = new ArrayList<>(experiences);
    }
    
//    /**
//     * Constructs a Batch with the specified observations, actions, and rewards.
//     * 
//     * @param observations the list of observations
//     * @param actions the list of actions
//     * @param rewards the list of rewards
//     */
//	public Batch(List<Observation> observations, List<Action> actions, List<Reward> rewards) {
//        this.observations = new ArrayList<>(observations);
//        this.actions = new ArrayList<>(actions);
//        this.rewards = new ArrayList<>(rewards);
//    }

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
	 * Adds an observation to the batch.
	 * 
	 * @param obs the observation to add
	 */
	
	
	
	/**
     * Adds an experience to the batch.
     *
     * @param obs the observation to add
     * @param act the action to add
     * @param rew the reward to add
     */
    public void addExperience(Observation obs, Action act, Reward rew) {
        experiences.add(new Experience(obs, act, rew));
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


