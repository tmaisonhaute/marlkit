package agent.action;

import java.util.Objects;

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
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Action2DMove a) {
			return this.value.equals(a.value);
		}
		return false;
	}
	
	
	
	
	@Override
	public String toString() {
		return "Move [dx=" + value.getFirst() + "; dy=" + value.getSecond() + "]";
	}

	public static Action2DMove up() {
        return new Action2DMove(new Pair<>(0, 1));
    }
	public static Action2DMove down() {
		return new Action2DMove(new Pair<>(0, -1));
	}
	public static Action2DMove left() {
		return new Action2DMove(new Pair<>(-1, 0));
	}
	public static Action2DMove right() {
		return new Action2DMove(new Pair<>(1, 0));
	}
	public static Action2DMove idle() {
		return new Action2DMove(new Pair<>(0, 0));
	}
	
}
