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
	
	public int getFirst() {
		return getValue().getFirst();
	}

	public int getSecond() {
		return getValue().getSecond();
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
			return this.getValue().equals(a.value);
		}
		return false;
	}
	
	public void add(Action2DMove other) {
		getValue().setFirst(getFirst() + other.getFirst());
		getValue().setSecond(getSecond() + other.getSecond());
	}
	
	public void add(Pair<Integer, Integer> vect) {
		getValue().setFirst(getFirst() + vect.getFirst());
		getValue().setSecond(getSecond() + vect.getSecond());
	}
	
	
	@Override
	public String toString() {
		return "Move [dx=" + value.getFirst() + "; dy=" + value.getSecond() + "]";
	}
	
	public static Action2DMove add(Action2DMove a, Action2DMove b) {
		return new Action2DMove(
				new Pair<>(a.getFirst() + b.getFirst(), a.getSecond() + b.getSecond()));
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
	
}
