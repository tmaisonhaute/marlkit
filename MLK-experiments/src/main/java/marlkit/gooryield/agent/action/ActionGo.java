package marlkit.gooryield.agent.action;

import java.util.Objects;

import agent.action.Action;

public class ActionGo implements Action {

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ActionGo;
	}

	@Override
	public int hashCode() {
		return Objects.hash("go");
	}

	@Override
	public String toString() {
		return "Go";
	}

	@Override
	public Action copy() {
		return new ActionGo();
	}
}
