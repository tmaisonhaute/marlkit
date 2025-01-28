package learning;
import java.util.ArrayList;
import java.util.List;

import agent.Action;
import environment.*;

/**
 * Represents a batch of data used for learning.
 */
public class Batch {
	private List<Observation> observations;
	private List<Action> actions;
	private List<Reward> rewards;
	
    /**
     * Constructs an empty Batch.
     */
    public Batch() {
        observations = new ArrayList<Observation>();
        actions = new ArrayList<Action>();
        rewards = new ArrayList<Reward>();
    }

    /**
     * Constructs a Batch with the specified observations, actions, and rewards.
     * 
     * @param observations the list of observations
     * @param actions the list of actions
     * @param rewards the list of rewards
     */
	public Batch(List<Observation> observations, List<Action> actions, List<Reward> rewards) {
        this.observations = observations;
        this.actions = actions;
        this.rewards = rewards;
    }

    // Getters
    /**
     * Returns the list of observations.
     * 
     * @return the list of observations
     */
    public List<Observation> getObservations() {
        return observations;
    }

    /**
     * Returns the list of actions.
     * 
     * @return the list of actions
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Returns the list of rewards.
     * 
     * @return the list of rewards
     */
    public List<Reward> getRewards() {
        return rewards;
    }
}
