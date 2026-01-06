package agent.action;

import java.util.Objects;

/**
 * An action represented by an integer value.
 */
public class ActionInt implements Action {
	protected int value;

	/**
	 * Creates a new integer action with the specified value.
	 *
	 * @param value the integer value of the action
	 */
	public ActionInt(int value) {
		this.value = value;
	}

	/**
	 * Returns the integer value of this action.
	 *
	 * @return the action value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the integer value of this action.
	 *
	 * @param value the new action value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionInt other = (ActionInt) obj;
		return value == other.value;
	}
	
	
	
}
