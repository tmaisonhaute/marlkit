package util.grafana;

/**
 * Interface for custom metrics to be tracked during learning.
 * <p>
 * Extras allow users to log additional data beyond standard rewards.
 * </p>
 *
 * @see StepData
 * @see LearningData
 */
public interface Extra {
    /**
     * Returns the name of this extra metric.
     *
     * @return a string identifier for this metric
     */
    public String toString();

    /**
     * Returns the numeric value of this extra metric.
     *
     * @return the metric value as a double
     */
    public double toDouble();
}
