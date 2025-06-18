package util.criteria;

import java.util.Optional;

import environment.state.State;

public class EpisodeCounterCriterion implements Criterion {
    private int currentEpisodes;
    private int targetEpisodes;
    

    public EpisodeCounterCriterion(int targetEpisodes) {
        if (targetEpisodes <= 0) {
            throw new IllegalArgumentException("Target episodes must be positive");
        }
        this.targetEpisodes = targetEpisodes;
        this.currentEpisodes = 0;
    }
    
    /**
     * Increments the episode counter when an episode ends.
     * This method should be called at the end of each episode.
     * 
     * @param state The current environment state (not used in this implementation)
     */
    @Override
    public void update(Optional<State> state) {
        currentEpisodes++;
    }

    /**
     * Resets the episode counter to zero.
     */
    @Override
    public void reset() {
        currentEpisodes = 0;
    }
    
    /**
     * Checks if the criterion has been met.
     * 
     * @return true if the current number of episodes has reached or exceeded the target, false otherwise
     */
    @Override
    public boolean isMet() {
        return currentEpisodes >= targetEpisodes;
    }

    public int getCurrentEpisodes() {
        return currentEpisodes;
    }

    public int getTargetEpisodes() {
        return targetEpisodes;
    }


    public void setTargetEpisodes(int targetEpisodes) {
        if (targetEpisodes <= 0) {
            throw new IllegalArgumentException("Impossible, target episodes must be positive");
        }
        this.targetEpisodes = targetEpisodes;
    }
} 