package util.criteria;

/**
 * Defines a component whose behavior may change during evaluation episodes.
 *
 * <p>The evaluation criterion is managed externally. Implementations may only
 * consult its current state.</p>
 */
public interface EvaluationAware {

    /**
     * Sets the criterion indicating whether the current episode is evaluated.
     *
     * @param evaluationCriterion the read-only evaluation criterion
     */
    void setEvaluationCriterion(ReadOnlyCriterion evaluationCriterion);

    /**
     * Returns the evaluation criterion.
     *
     * @return the criterion, or {@code null} if none has been configured
     */
    ReadOnlyCriterion getEvaluationCriterion();

    /**
     * Indicates whether the current episode is evaluated.
     *
     * @return {@code true} if a criterion is configured and currently met
     */
    default boolean isEvaluated() {
        ReadOnlyCriterion criterion = getEvaluationCriterion();
        return criterion != null && criterion.isMet();
    }
}