package environment.observation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import agent.MLKAgent;

/**
 * Represents an ordered collection of observations associated with agents.
 *
 * <p>The insertion order of the agents is preserved and defines the order used
 * when converting the joint observation into a vector.</p>
 */
public class MappedJointObservation implements Observation {

    protected Map<MLKAgent, Observation> agentObservations;

    /**
     * Creates an empty mapped joint observation.
     */
    public MappedJointObservation() {
        this.agentObservations = new LinkedHashMap<>();
    }

    /**
     * Creates a mapped joint observation from the specified associations.
     *
     * @param agentObservations the observations associated with the agents
     */
    public MappedJointObservation(Map<MLKAgent, Observation> agentObservations) {
        this.agentObservations = new LinkedHashMap<>(agentObservations);
    }

    /**
     * Creates a mapped joint observation from the specified associations.
     *
     * @param agentObservations the observations associated with the agents
     * @return the mapped joint observation
     */
    public static MappedJointObservation of(Map<MLKAgent, Observation> agentObservations) {
        return new MappedJointObservation(agentObservations);
    }

    /**
     * Adds or replaces the observation associated with an agent.
     *
     * @param agent the agent associated with the observation
     * @param observation the observation to associate with the agent
     */
    public void addObservation(MLKAgent agent, Observation observation) {
        agentObservations.put(Objects.requireNonNull(agent, "agent"), Objects.requireNonNull(observation, "observation"));
    }

    /**
     * Creates a new mapped joint observation with the specified observation
     * associated with the specified agent.
     *
     * <p>If the agent already has an observation, it is replaced in the new
     * instance.</p>
     *
     * @param agent the agent associated with the observation
     * @param observation the observation to associate with the agent
     * @return a new mapped joint observation
     */
    public MappedJointObservation withObservation(MLKAgent agent, Observation observation) {
        MappedJointObservation newJointObservation = new MappedJointObservation(agentObservations);
        newJointObservation.addObservation(agent, observation);
        return newJointObservation;
    }

    /**
     * Returns the ordered map associating agents with observations.
     *
     * @return an immutable view of the agent-observation associations
     */
    public Map<MLKAgent, Observation> getMappedObservations() {
        return Map.copyOf(agentObservations);
    }

    /**
     * Returns the observations in agent insertion order.
     *
     * @return the ordered observations
     */
    public List<Observation> getObservations() {
        return List.copyOf(agentObservations.values());
    }

    /**
     * Returns the observation associated with an agent.
     *
     * @param agent the agent whose observation is requested
     * @return the associated observation, or {@code null} if absent
     */
    public Observation getObservation(MLKAgent agent) {
        return agentObservations.get(agent);
    }

    /**
     * Checks whether an observation is associated with an agent.
     *
     * @param agent the agent to check
     * @return {@code true} if the agent has an associated observation
     */
    public boolean containsAgent(MLKAgent agent) {
        return agentObservations.containsKey(agent);
    }

    /**
     * Removes the observation associated with an agent.
     *
     * @param agent the agent whose observation must be removed
     */
    public void removeObservation(MLKAgent agent) {
        agentObservations.remove(agent);
    }

    /**
     * Returns the number of agent-observation associations.
     *
     * @return the number of observations
     */
    public int size() {
        return agentObservations.size();
    }

    /**
     * Returns whether this joint observation is empty.
     *
     * @return {@code true} if no observation is stored
     */
    public boolean isEmpty() {
        return agentObservations.isEmpty();
    }

    /**
     * Adds the observations from another mapped joint observation to this one.
     *
     * <p>If the supplied observation is mapped, all its associations are added
     * or replaced. Otherwise, the operation cannot determine the agent
     * associated with the observation and fails.</p>
     *
     * @param other the observation to combine with this one
     * @return the combined mapped joint observation
     */
    @Override
    public void add(Observation other) {
        if (!(other instanceof MappedJointObservation otherMapped)) {
            throw new IllegalArgumentException("MappedJointObservation can only be added to another MappedJointObservation.");
        }


        for (Map.Entry<MLKAgent, Observation> entry : otherMapped.agentObservations.entrySet()) {
            this.addObservation(entry.getKey(), entry.getValue());
        }

    }

    /**
     * Creates a deep copy of this mapped joint observation.
     *
     * @return an independent copy
     */
    @Override
    public MappedJointObservation copy() {
        MappedJointObservation copy = new MappedJointObservation();

        for (Map.Entry<MLKAgent, Observation> entry : agentObservations.entrySet()) {
            copy.addObservation(entry.getKey(), entry.getValue().copy());
        }

        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MappedJointObservation other)) {
            return false;
        }

        return Objects.equals(agentObservations, other.agentObservations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentObservations);
    }
}