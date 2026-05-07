package marlkit.gooryield;

import java.util.Objects;

import agent.action.Action;

public class ActionYield implements Action {

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ActionYield;
	}

	@Override
	public int hashCode() {
		return Objects.hash("yield");
	}

	@Override
	public String toString() {
		return "Yield";
	}

	@Override
	public Action copy() {
		return new ActionYield();
	}
}
