package learning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import agent.action.Action;
import environment.observation.Observation;
import experience.Experience;
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
	 * Returns an array of all observations in the batch.
	 * @return an array of all observations
	 */
	public Observation[] getAllObservations() {
		Observation[] inputs = new Observation[experiences.size()];
		for (int i = 0; i < experiences.size(); i++) {
			inputs[i] = experiences.get(i).getObservation();
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
    
    /**
     * Computes the total rewards of all experiences in the batch.
     * @return the total rewards
     */
    public double totalRewards() {
		double totalRewards = 0;
		for (Experience experience : experiences) {
			totalRewards += experience.getRewardValue();
		}
		return totalRewards;
    }

    
    /**
     * Returns a batch containing experiences sampled randomly from this batch.
     *
     * <p>The sampled experiences are not removed from the current batch.</p>
     *
     * @param sampleSize the number of experiences to sample
     * @param randomGenerator the random generator used for sampling
     * @return a new batch containing the sampled experiences
     */
    public Batch sample(int sampleSize, RandomGenerator randomGenerator) {
        if (sampleSize > experiences.size()) {
            throw new IllegalArgumentException("sampleSize must not exceed the batch size.");
        }

        List<Experience> shuffledExperiences = new ArrayList<>(experiences);
        Collections.shuffle(shuffledExperiences, new Random(randomGenerator.nextLong()));

        return new Batch(shuffledExperiences.subList(0, sampleSize));
    }
    
    /**
     * Retains only the most recent experiences and returns the removed ones.
     *
     * @param maximumSize the maximum number of experiences to retain
     * @return the experiences removed from the buffer
     */
    public List<Experience> retainLatest(int maximumSize) {
        if (maximumSize < 0) {
            throw new IllegalArgumentException("maximumSize must be non-negative.");
        }

        int count = experiences.size() - maximumSize;
        List<Experience> removedExperiences = new ArrayList<>(Math.max(count, 0));

        for (int i = 0; i < count; i++) {
            removedExperiences.add(experiences.removeFirst());
        }

        return removedExperiences;
    }
    
	
}


