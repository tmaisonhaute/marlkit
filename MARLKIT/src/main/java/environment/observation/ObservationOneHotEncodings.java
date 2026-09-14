package environment.observation;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for storing and aggregating multiple one-hot encoded observations.
 *
 * This class is primarily used for computing the mean (average) observation from
 * a set of one-hot encoded observations, which is useful in mean field methods
 * and other forms of action distribution modeling in multi-agent systems.
 */
public class ObservationOneHotEncodings implements Observation {

    private List<ObservationOneHotEncoding> oneHotEncodings;

    /**
     * Constructs an empty container for one-hot encoded observations.
     */
    public ObservationOneHotEncodings() {
        this.oneHotEncodings = new ArrayList<>();
    }

    /**
     * Constructs the container with an existing list of one-hot encodings.
     *
     * @param oneHotEncodings list of one-hot encoded observations
     */
    public ObservationOneHotEncodings(List<ObservationOneHotEncoding> oneHotEncodings) {
        this.oneHotEncodings = oneHotEncodings;
    }

    /**
     * Returns the list of stored one-hot encoded observations.
     *
     * @return list of one-hot encodings
     */
    public List<ObservationOneHotEncoding> getOneHotEncodings() {
        return oneHotEncodings;
    }

    /**
     * Replaces the list of stored one-hot encoded observations.
     *
     * @param oneHotEncodings new list of encodings
     */
    public void setOneHotEncodings(List<ObservationOneHotEncoding> oneHotEncodings) {
        this.oneHotEncodings = oneHotEncodings;
    }

    /**
     * Adds a new one-hot encoded observation to the container.
     *
     * @param obs an ObservationOneHotEncoding to add
     * @return this container after modification
     * @throws IllegalArgumentException if the observation is not of type ObservationOneHotEncoding
     */
    @Override
    public void add(Observation obs) {
        if (!(obs instanceof ObservationOneHotEncoding)) {
            throw new IllegalArgumentException(
                    "Cannot add a non-ObservationOneHotEncoding instance.");
        }
        oneHotEncodings.add((ObservationOneHotEncoding) obs);
    }

    /**
     * Computes the average (mean) of all stored one-hot encoded observations.
     *
     * @return a single Observation representing the averaged encoding
     * @throws IllegalStateException if no encodings are stored
     */
    public Observation getAverage() {
        if (oneHotEncodings.isEmpty()) {
            throw new IllegalStateException("Cannot compute average of empty encoding list.");
        }

        ObservationOneHotEncoding avg = new ObservationOneHotEncoding();
        for (ObservationOneHotEncoding ohe : oneHotEncodings) {
            avg.add(ohe);
        }
        avg.divide(oneHotEncodings.size());
        return avg;
    }
    
    @Override
	public ObservationOneHotEncodings copy() {
		List<ObservationOneHotEncoding> copiedEncodings = new ArrayList<>();
		for (ObservationOneHotEncoding ohe : oneHotEncodings) {
			copiedEncodings.add(ohe.copy());
		}
		return new ObservationOneHotEncodings(copiedEncodings);
	}
}
