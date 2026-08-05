package util.grafana;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import agent.MLKAgent;
import experience.Experience;
import util.Pair;

/**
 * Container for data collected during a single simulation step.
 * <p>
 * Encapsulates agent experiences and optional extra metrics for logging
 * and analysis. Used by {@link LearningData} to track episode progress.
 * </p>
 *
 * @see LearningData
 * @see Experience
 * @see Extra
 */
public class StepData {
    private Pair<Map<MLKAgent, Experience>, Optional<List<Extra>>> data;

    /**
     * Creates step data with experiences and optional extras.
     *
     * @param experiences map of agents to their experiences this step
     * @param extra       optional list of extra metrics
     */
    public StepData(Map<MLKAgent, Experience> experiences, Optional<List<Extra>> extra) {
        this(new Pair<>(experiences, extra));
    }

    /**
     * Creates step data with experiences only (no extras).
     *
     * @param experiences map of agents to their experiences this step
     */
    public StepData(Map<MLKAgent, Experience> experiences) {
        this(new Pair<>(experiences, Optional.empty()));
    }

    /**
     * Creates step data from a pre-constructed pair.
     *
     * @param data the pair containing experiences and optional extras
     */
    public StepData(Pair<Map<MLKAgent, Experience>, Optional<List<Extra>>> data) {
        this.data = data;
    }

    /**
     * Returns the raw data pair containing experiences and extras.
     *
     * @return the data pair
     */
    public Pair<Map<MLKAgent, Experience>, Optional<List<Extra>>> getData() {
        return data;
    }

    /**
     * Returns the map of agent experiences for this step.
     *
     * @return map of agents to their experiences
     */
    public Map<MLKAgent, Experience> getExperiences() {
        return data.getFirst();
    }

    /**
     * Returns the experience for a specific agent.
     *
     * @param agent the agent whose experience to retrieve
     * @return the agent's experience for this step
     */
    public Experience getExperience(MLKAgent agent) {
        return data.getFirst().get(agent);
    }

    /**
     * Returns the optional extra metrics for this step.
     *
     * @return optional containing extra metrics if present
     */
    public Optional<List<Extra>> getExtra() {
        return data.getSecond();
    }

    /**
     * Sets the data for this step.
     *
     * @param data the new data pair
     */
    public void setData(Pair<Map<MLKAgent, Experience>, Optional<List<Extra>>> data) {
        this.data = data;
    }
}
