
package rewardmodeling;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an event that occurs in the environment, decoupling environment dynamics from reward assignment.
 * <p>
 * An Event consists of a string tag (type) and a set of key-value parameters providing context about the event.
 * This allows the environment to record what happened (e.g., "BLOCK_PUSHED", "AGENT_MOVED") and attach any relevant
 * data (such as counts, positions, or other metrics) without directly assigning rewards. Reward modeling systems can
 * then interpret these events and compute rewards for agents in a flexible, modular way.
 * <p>
 * Events are immutable: adding parameters returns a new Event instance.
 * </p>
 *
 * <b>Example usage:</b>
 * <pre>
 * Event event = new Event("BLOCK_PUSHED").with("count", 2).with("position", new Pair<>(1,2));
 * int count = event.getInt("count");
 * Pair<Integer, Integer> pos = event.get("position", Pair.class);
 * </pre>
 */
public class Event {
	/**
	 * The type or tag of the event.
	 */
	private final String eventTag;
	/**
	 * Parameters providing context for the event.
	 */
	private final Map<String, Object> parameters;

	/**
	 * Creates an event with the given tag and no parameters.
	 *
	 * @param eventTag the type or name of the event
	 */
	public Event(String eventTag) {
		this.eventTag = eventTag;
		this.parameters = Map.of();
	}

	/**
	 * Creates an event with the given tag and parameters.
	 *
	 * @param eventTag the type or name of the event
	 * @param parameters a map of parameter names to values
	 */
	public Event(String eventTag, Map<String, Object> parameters) {
		this.eventTag = eventTag;
		this.parameters = Map.copyOf(parameters);
	}

	/**
	 * Returns a new Event with an additional or replaced parameter.
	 * <p>
	 * This method does not mutate the original event, but returns a new instance with the updated parameters.
	 * </p>
	 *
	 * @param key the parameter name
	 * @param value the parameter value
	 * @return a new Event with the added or replaced parameter
	 */
	public Event with(String key, Object value) {
		Map<String, Object> newParams = new HashMap<>(this.parameters);
		newParams.put(key, value);
		return new Event(this.eventTag, newParams);
	}

	/**
	 * Returns the event tag (type).
	 *
	 * @return the event tag
	 */
	public String getEventTag() { return eventTag; }

	/**
	 * Retrieves a parameter value by key as an Object.
	 * <p>
	 * You may need to cast the result to the expected type.
	 * </p>
	 *
	 * @param key the parameter name
	 * @return the parameter value, or null if not present
	 */
	public Object get(String key) {
		return parameters.get(key);
	}

	/**
	 * Retrieves a parameter value by key and casts it to the specified type.
	 * <p>
	 * This method provides type safety when extracting parameters from the event. It will throw a
	 * {@link ClassCastException} if the value is not of the expected type.
	 * </p>
	 *
	 * <b>Example:</b>
	 * <pre>
	 * int count = event.get("count", Integer.class);
	 * Pair<Integer, Integer> pos = event.get("position", Pair.class);
	 * </pre>
	 *
	 * @param key the parameter name
	 * @param clazz the expected class of the value
	 * @param <T> the type to cast to
	 * @return the parameter value cast to the specified type, or null if not present
	 * @throws ClassCastException if the value is not of the expected type
	 */
	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(parameters.get(key));
	}

	/**
	 * Retrieves a parameter as a double value. Throws if the value is not a Number.
	 *
	 * @param key the parameter name
	 * @return the parameter value as a double
	 * @throws ClassCastException if the value is not a Number
	 */
	public double getDouble(String key) {
		return ((Number) parameters.get(key)).doubleValue();
	}

	/**
	 * Retrieves a parameter as an int value. Throws if the value is not a Number.
	 *
	 * @param key the parameter name
	 * @return the parameter value as an int
	 * @throws ClassCastException if the value is not a Number
	 */
	public int getInt(String key) {
		return ((Number) parameters.get(key)).intValue();
	}
}