
package rewardmodeling;

import java.util.ArrayList;
import java.util.List;

import environment.reward.Reward;

/**
 * Abstract base class for events that occur in the environment.
 * <p>
 * An Event consists of a set of key-value parameters providing context about the event. Concrete subclasses 
 * should implement the {@link #toReward()} method to specify how rewards are computed for each event type.
 *
 * <b>Example usage:</b>
 * <pre>
 * Event event = new BlockPushedEvent().get("count", Integer.class);
 * Pair<Integer, Integer> pos = event.get("position", Pair.class);
 * Reward reward = event.toReward();
 * event.addParameters(Map.of("newParam", value));
 * </pre>
 */
public abstract class Event {
//	/**
//	 * Parameters providing context for the event. Immutable after construction.
//	 */
//	private final Map<String, Object> parameters;

	/**
	 * Creates an event with no parameters.
	 */
	protected Event() {
//		this.parameters = Map.of();
	}

//	/**
//	 * Creates an event with the given parameters.
//	 *
//	 * @param parameters a map of parameter names to values
//	 */
//	protected Event(Map<String, Object> parameters) {
//		this.parameters = Map.copyOf(parameters);
//	}

//	/**
//	 * Adds or replaces parameters in this event. (Note: this implementation mutates the parameters map, so subclasses should override to ensure immutability.)
//	 * <p>
//	 * In a truly immutable event, this method should return a new instance with updated parameters.
//	 * </p>
//	 *
//	 * @param params a map of parameter names to values to add or replace
//	 * @return a new Event with the added or replaced parameters (subclasses should override for immutability)
//	 */
//	public void addParameters(Map<String, Object> params) {
//		this.parameters.putAll(params);
//	}

	/**
	 * Converts this event to a {@link Reward} according to the event's logic.
	 * <p>
	 * Must be implemented by concrete subclasses to define how rewards are computed for this event type.
	 * </p>
	 * @return the computed Reward for this event
	 */
	public abstract Reward toReward();
	
	public List<Event> toList() {
		List<Event> events = new ArrayList<>();
		events.add(this);
		return events;
	}

//	
//	/**
//	 * Retrieves a parameter value by key as an Object.
//	 * <p>
//	 * You may need to cast the result to the expected type.
//	 * </p>
//	 *
//	 * @param key the parameter name
//	 * @return the parameter value, or null if not present
//	 */
//	public Object get(String key) {
//		return parameters.get(key);
//	}
//
//	/**
//	 * Retrieves a parameter value by key and casts it to the specified type.
//	 * <p>
//	 * This method provides type safety when extracting parameters from the event. It will throw a
//	 * {@link ClassCastException} if the value is not of the expected type.
//	 * </p>
//	 *
//	 * <b>Example:</b>
//	 * <pre>
//	 * int count = event.get("count", Integer.class);
//	 * Pair<Integer, Integer> pos = event.get("position", Pair.class);
//	 * </pre>
//	 *
//	 * @param key the parameter name
//	 * @param clazz the expected class of the value
//	 * @param <T> the type to cast to
//	 * @return the parameter value cast to the specified type, or null if not present
//	 * @throws ClassCastException if the value is not of the expected type
//	 */
//	public <T> T get(String key, Class<T> clazz) {
//		return clazz.cast(parameters.get(key));
//	}

}