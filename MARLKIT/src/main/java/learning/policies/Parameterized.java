package learning.policies;

/**
 * Represents an object whose internal trainable parameters can be exported and replaced
 * as a flat array of {@code double} values.
 * <p>
 * This interface does not define the semantics of the parameters. They may correspond
 * to neural network weights, biases, linear model coefficients, table values, or any
 * other numeric representation used internally by the implementing object.
 * </p>
 * <p>
 * Implementations are responsible for preserving a stable parameter ordering. In other
 * words, the array returned by {@link #getParameters()} must be readable by
 * {@link #setParameters(double[])} on a compatible object with the same internal
 * structure.
 * </p>
 * <p>
 * This interface is mainly intended for operations such as parameter copying,
 * synchronization, interpolation, averaging, or communication between compatible
 * agents or models.
 * </p>
 */
public interface Parameterized {

    /**
     * Returns the current parameters as a flat array of {@code double} values.
     * <p>
     * The returned array should represent all parameters required to restore the
     * current parameter state of this object through {@link #setParameters(double[])}.
     * The order of values must remain stable across calls for the same implementation
     * and internal structure.
     * </p>
     *
     * @return a flat array containing the current parameters
     */
    double[] getParameters();

    /**
     * Replaces the current parameters using the provided flat array of {@code double}
     * values.
     * <p>
     * The provided array is expected to follow the same ordering and size convention
     * as the array returned by {@link #getParameters()} for a compatible object.
     * Implementations should reject arrays with an incompatible length or structure,
     * typically by throwing an {@link IllegalArgumentException}.
     * </p>
     *
     * @param parameters the flat array of parameters to set
     * @throws NullPointerException if {@code parameters} is {@code null}
     * @throws IllegalArgumentException if {@code parameters} does not match the
     * expected parameter size or structure
     */
    void setParameters(double[] parameters);
}