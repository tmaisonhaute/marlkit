package agent.action;

import util.Pair;

public class Action2DMove implements Action {
	protected Pair<Integer, Integer> value;

	public Action2DMove(Pair<Integer, Integer> value) {
		this.value = value;
	}

	public Pair<Integer, Integer> getValue() {
		return value;
	}

	public void setValue(Pair<Integer, Integer> value) {
		this.value = value;
	}
	
}
