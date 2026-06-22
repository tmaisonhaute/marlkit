package agent.action;

import java.util.List;

import util.Pair;

/**
 * An action representing a 2D movement with delta-x and delta-y components.
 */
public class Move2DInt implements Action {
	protected Pair<Integer, Integer> value;

	/**
	 * Creates a new 2D movement action.
	 *
	 * @param value a pair containing (delta-x, delta-y) movement values
	 */
	public Move2DInt(Pair<Integer, Integer> value) {
		this.value = value;
	}

	/**
	 * Returns the movement value as a pair of integers.
	 *
	 * @return the (delta-x, delta-y) pair
	 */
	public Pair<Integer, Integer> getValue() {
		return value;
	}

	/**
	 * Sets the movement value.
	 *
	 * @param value the new (delta-x, delta-y) pair
	 */
	public void setValue(Pair<Integer, Integer> value) {
		this.value = value;
	}
	
	/**
	 * Returns the delta-x component of the movement.
	 *
	 * @return the x-axis movement
	 */
	public int getFirst() {
		return getValue().getFirst();
	}

	/**
	 * Returns the delta-y component of the movement.
	 *
	 * @return the y-axis movement
	 */
	public int getSecond() {
		return getValue().getSecond();
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Move2DInt a) {
			return this.getValue().equals(a.value);
		}
		return false;
	}
	
	/**
	 * Adds another 2D movement to this movement, modifying this action.
	 *
	 * @param other the movement to add
	 */
	public void add(Move2DInt other) {
		getValue().setFirst(getFirst() + other.getFirst());
		getValue().setSecond(getSecond() + other.getSecond());
	}
	
	/**
	 * Adds a vector to this movement, modifying this action.
	 *
	 * @param vect the vector to add
	 */
	public void add(Pair<Integer, Integer> vect) {
		getValue().setFirst(getFirst() + vect.getFirst());
		getValue().setSecond(getSecond() + vect.getSecond());
	}
	
	
	@Override
	public String toString() {
		return "Move [dx=" + value.getFirst() + "; dy=" + value.getSecond() + "]";
	}
	
	/**
	 * Creates a new 2D movement by adding two movements.
	 *
	 * @param a the first movement
	 * @param b the second movement
	 * @return a new movement representing the sum
	 */
	public static Move2DInt add(Move2DInt a, Move2DInt b) {
		return new Move2DInt(
				new Pair<>(a.getFirst() + b.getFirst(), a.getSecond() + b.getSecond()));
	}

	/**
	 * Creates an upward movement (0, 1).
	 *
	 * @return an upward movement action
	 */
	public static Move2DInt up() {
        return new Move2DInt(new Pair<>(0, 1));
    }
	
	/**
	 * Creates a downward movement (0, -1).
	 *
	 * @return a downward movement action
	 */
	public static Move2DInt down() {
		return new Move2DInt(new Pair<>(0, -1));
	}
	
	/**
	 * Creates a leftward movement (-1, 0).
	 *
	 * @return a leftward movement action
	 */
	public static Move2DInt left() {
		return new Move2DInt(new Pair<>(-1, 0));
	}
	
	/**
	 * Creates a rightward movement (1, 0).
	 *
	 * @return a rightward movement action
	 */
	public static Move2DInt right() {
		return new Move2DInt(new Pair<>(1, 0));
	}
	
	/**
	 * Creates an idle movement (0, 0).
	 *
	 * @return an idle movement action
	 */
	public static Move2DInt idle() {
		return new Move2DInt(new Pair<>(0, 0));
	}

	/**
	 * Returns a list of Von Neumann neighborhood movements (up, down, left, right).
	 *
	 * @return list of four cardinal direction movements
	 */
	public static List<Action> getVonNeumannMove(){
		return List.of( up(), down(), left(), right() );
	}
	
	@Override
	public Move2DInt copy() {
		return new Move2DInt(new Pair<>(getFirst(), getSecond()));
	}
	
	public Move2DDouble toMoveDouble() {
		return new Move2DDouble(value.getFirst(), value.getSecond());
	}
}
