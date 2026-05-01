package environment.observation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an observation in the form of a one-hot or averaged one-hot encoded vector.
 *
 * This class is typically used to represent discrete actions or averaged distributions
 * over actions in a numerical form suitable for learning algorithms.
 */
public class ObservationOneHotEncoding implements Observation {

    private List<Double> OHE;

    /**
     * Default constructor. Initializes with a null vector.
     */
    public ObservationOneHotEncoding() {
        this.OHE = null;
    }

    /**
     * Constructs an observation using the given one-hot encoded vector.
     *
     * @param action the vector representing the one-hot or averaged encoding
     */
    public ObservationOneHotEncoding(List<Double> action) {
        this.OHE = action;
    }

    /**
     * Returns the internal one-hot encoded vector.
     *
     * @return the list of values representing the encoding
     */
    public List<Double> getAction() {
        return OHE;
    }

    /**
     * Sets the one-hot encoded vector.
     *
     * @param action the new encoding to assign
     */
    public void setAction(List<Double> action) {
        this.OHE = action;
    }

    /**
     * Adds another observation to this one.
     *
     * @param obs the observation to add
     * @return this observation after modification
     * @throws IllegalArgumentException if the input is not an ObservationOneHotEncoding
     *                                  or the vector sizes don't match
     */
    @Override
    public Observation add(Observation obs) {
        if (!(obs instanceof ObservationOneHotEncoding)) {
            throw new IllegalArgumentException(
                    "Cannot add a non-ObservationOneHotEncoding instance.");
        }

        List<Double> otherOHE = ((ObservationOneHotEncoding) obs).OHE;

        if (this.OHE == null) {
            this.OHE = new ArrayList<>(otherOHE);
        } else if (this.OHE.size() != otherOHE.size()) {
            throw new IllegalArgumentException(
                    "Cannot add ObservationOneHotEncodings with different sizes.");
        } else {
            List<Double> newOHE = new ArrayList<>(OHE.size());
            for (int i = 0; i < OHE.size(); i++) {
                newOHE.add(this.OHE.get(i) + otherOHE.get(i));
            }
            this.OHE = newOHE;
        }
        return this;
    }

    /**
     * Divides all elements in the encoding vector by the given number.
     *
     * @param nb the number to divide by
     * @return this observation after division
     * @throws ArithmeticException if nb is zero
     */
    public Observation divide(int nb) {
        if (nb == 0) {
            throw new ArithmeticException("Cannot divide by zero.");
        }

        List<Double> newOHE = new ArrayList<>(OHE.size());
        for (double val : OHE) {
            newOHE.add(val / nb);
        }
        this.OHE = newOHE;
        return this;
    }

    public ObservationOneHotEncoding copy() {
        return new ObservationOneHotEncoding(new ArrayList<>(this.OHE));
    }
}
