package learning.valuefunction;


import java.util.HashMap;
import java.util.Map;

/**
 * Generic base class for tabular value functions in reinforcement learning.
 * <p>
 * Provides a table-based storage mapping keys to numeric values,
 * with a configurable default value for unseen keys.
 * </p>
 *
 * @param <K> the type of key (e.g., Observation for V(s), Pair&lt;Observation, Action&gt; for Q(s,a))
 * @see QTable
 * @see VTable
 */
public class ValueFunction<K> {
    protected Map<K, Double> tableValue;
	protected final double defaultValue;

	/**
	 * Creates a value function with the specified default value.
	 *
	 * @param defaultValue the value returned for unseen keys
	 */
	public ValueFunction(double defaultValue) {
		this.tableValue = new HashMap<>();
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Resets the value function by clearing all stored values.
	 */
	public void reset() {
		tableValue.clear();
	}

	/**
	 * Returns the value associated with the given key.
	 *
	 * @param key the key to look up
	 * @return the stored value, or defaultValue if the key has not been seen
	 */
	public Double getValue(K key) {
        return tableValue.getOrDefault(key, getDefaultValue());
    }

	/**
	 * Sets the value for the given key.
	 *
	 * @param key   the key to store
	 * @param value the value to associate with the key
	 */
	public void setValue(K key, double value) {
        tableValue.put(key, value);
    }

	/**
	 * Returns the default value used for unseen keys.
	 *
	 * @return the default value
	 */
	public double getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Returns the number of entries in the value function.
	 *
	 * @return the number of stored key-value pairs
	 */
	public int size() {
		return tableValue.size();
	}
	
}