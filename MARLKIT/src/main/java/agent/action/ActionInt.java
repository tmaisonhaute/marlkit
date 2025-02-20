package agent.action;

import java.util.Objects;

public class ActionInt implements Action {
	protected int value;

	public ActionInt(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

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
