package marlkit.listenorgo;

/**
 * Represents the possible door choices in the ListenOrGo environment.
 * <p>
 * Agents must ultimately commit to either {@link #LEFT} or {@link #RIGHT}.
 * {@link #NONE} indicates that no choice has been made yet for the current episode.
 * </p>
 */
public enum Choice {
    /** The left door. */
    LEFT,
    /** The right door. */
    RIGHT,
    /** No choice has been made yet; the agent has not committed to a direction. */
    NONE
}
