
package reward;

import java.util.ArrayList;
import java.util.List;

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
public abstract class ReactionEvent {

	/**
	 * Creates an event with no parameters.
	 */
	protected ReactionEvent() {
	}


	/**
	 * Converts this event to a {@link Reward} according to the event's logic.
	 * <p>
	 * Must be implemented by concrete subclasses to define how rewards are computed for this event type.
	 * </p>
	 * @return the computed Reward for this event
	 */
	public abstract Reward toReward();
	
	public List<ReactionEvent> toList() {
		List<ReactionEvent> events = new ArrayList<>();
		events.add(this);
		return events;
	}

	
}