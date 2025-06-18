package simulation;

import java.util.ArrayList;

public class LearningData {
    private final ArrayList<ArrayList<StepData>> episodes;

    public LearningData() {
        this.episodes = new ArrayList<>();
    }

    public void addStep(int numEpisodes, StepData step) {
        if (numEpisodes >= episodes.size()) {
            episodes.add(new ArrayList<>());
        }
        episodes.getLast().add(step);
    }

    /**
     * Retrieves the number of episodes recorded in the learning state.
     *
     * @return The number of episodes.
     */
    public int getNumEpisodes() {
        return episodes.size();
    }

    /**
     * Retrieves the data for a specific episode.
     *
     * @param numEpisode The episode number to retrieve (index start from 0).
     * @return An ArrayList of pairs containing a StepData.
     * @throws IndexOutOfBoundsException if the episode number is invalid.
     */
    public ArrayList<StepData> getEpisode(int numEpisode) {
        if (numEpisode < 0 || numEpisode >= episodes.size()) {
            throw new IndexOutOfBoundsException("Invalid episode number: " + numEpisode);
        }
        return episodes.get(numEpisode);
    }

    public StepData getStepOfEpisode(int numEpisode, int step) {
        if (numEpisode < 0 || numEpisode >= episodes.size()) {
            throw new IndexOutOfBoundsException("Invalid episode number: " + numEpisode);
        }
        ArrayList<StepData> episodeData = episodes.get(numEpisode);
        if (step < 0 || step >= episodeData.size()) {
            throw new IndexOutOfBoundsException("Invalid step number: " + step);
        }
        return episodeData.get(step);
    }

    public ArrayList<ArrayList<StepData>> getEpisodes() {
        return episodes;
    }

    public void reset() {
        episodes.clear();
    }
}
