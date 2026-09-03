package environment.observation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an ordered collection of local observations used as a joint
 * observation.
 *
 * <p>Each local observation remains a distinct block. The order of these blocks
 * must remain stable so that a wrapper can produce a consistent vector
 * representation.</p>
 */
public class JointObservation implements Observation {

    private final List<Observation> observations;

    /**
     * Creates an empty joint observation.
     */
    public JointObservation() {
        observations = new ArrayList<>();
    }

    /**
     * Creates a joint observation containing the specified observations in
     * their iteration order.
     *
     * @param observations the ordered local observations
     * @throws NullPointerException if the list or one of its observations is
     *                              {@code null}
     */
    public JointObservation(List<? extends Observation> observations) {
        Objects.requireNonNull(observations, "observations");
        this.observations = new ArrayList<>();

        for (Observation observation : observations) {
            addObservation(observation);
        }
    }

    /**
     * Adds a local observation at the end of this joint observation.
     *
     * @param observation the local observation to add
     * @throws NullPointerException if {@code observation} is {@code null}
     */
    public void addObservation(Observation observation) {
        observations.add(Objects.requireNonNull(observation, "observation"));
    }

    /**
     * Returns the local observation at the specified index.
     *
     * @param index the observation index
     * @return the local observation at the specified index
     * @throws IndexOutOfBoundsException if the index is outside the valid range
     */
    public Observation getObservation(int index) {
        return observations.get(index);
    }

    /**
     * Returns an immutable view of the ordered local observations.
     *
     * @return the ordered local observations
     */
    public List<Observation> getObservations() {
        return List.copyOf(observations);
    }

    /**
     * Returns the number of local observations.
     *
     * @return the number of observations
     */
    public int size() {
        return observations.size();
    }

    /**
     * Returns a new joint observation with the specified observation appended.
     *
     * <p>If {@code other} is also a joint observation, all its local
     * observations are appended individually while preserving their order.</p>
     *
     * @param other the observation to append
     * @return a new joint observation containing both observations
     * @throws NullPointerException if {@code other} is {@code null}
     */
    @Override
    public JointObservation add(Observation other) {
        Objects.requireNonNull(other, "other");

        JointObservation result = copy();

        if (other instanceof JointObservation jointObservation) {
            for (Observation observation : jointObservation.observations) {
                result.addObservation(observation);
            }
        } else {
            result.addObservation(other);
        }

        return result;
    }

    /**
     * Creates a deep copy of this joint observation.
     *
     * @return an independent copy of this joint observation
     */
    @Override
    public JointObservation copy() {
        JointObservation copy = new JointObservation();

        for (Observation observation : observations) {
            copy.addObservation(observation.copy());
        }

        return copy;
    }
}
         