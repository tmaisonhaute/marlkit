package util.criteria;

/**
 * A wrapper class for a Criterion that only exposes the isMet() method.
 * This is useful for situations where you want to check if a criterion is met without allowing external code to modify its state.
 */
public class ReadOnlyCriterion {

	private final Criterion criterion;

	/**
	 * Constructs a ReadOnlyCriterion that wraps the given Criterion.
	 * @param criterion the Criterion to wrap
	 */
	public ReadOnlyCriterion(Criterion criterion) {
		this.criterion = criterion;
	}

	/**
	 * Checks if the wrapped criterion is met.
	 * @return true if the criterion is met, false otherwise
	 */
	public boolean isMet() {
		return criterion.isMet();
	}
}
