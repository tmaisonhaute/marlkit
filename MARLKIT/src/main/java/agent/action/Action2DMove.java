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
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Action2DMove a) {
			return this.value.equals(a.value);
		}
		return false;
	}
	
}
