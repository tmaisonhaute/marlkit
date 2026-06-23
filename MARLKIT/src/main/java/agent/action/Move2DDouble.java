package agent.action;

import java.util.ArrayList;
import java.util.List;

import util.Pair;

/**
 * An action representing a 2D continuous movement with delta-x and delta-y components.
 */
public class Move2DDouble implements Action {

    protected Pair<Double, Double> value;

    /**
     * Creates a new continuous 2D movement action.
     *
     * @param value a pair containing dx and dy movement values
     */
    public Move2DDouble(Pair<Double, Double> value) {
        this.value = value;
    }
    
	public Move2DDouble(double dx, double dy) {
		this.value = new Pair<>(dx, dy);
	}

    /**
     * Returns the movement value.
     *
     * @return movement vector
     */
    public Pair<Double, Double> getValue() {
        return value;
    }

    /**
     * Sets the movement value.
     *
     * @param value new movement vector
     */
    public void setValue(Pair<Double, Double> value) {
        this.value = value;
    }

    public double getFirst() {
        return value.getFirst();
    }

    public double getSecond() {
        return value.getSecond();
    }

    /**
     * Adds the movement values of another Move2DDouble to this one.
     * @param other
     */
    public void add(Move2DDouble other) {
        value.setFirst(getFirst() + other.getFirst());
        value.setSecond(getSecond() + other.getSecond());
    }

    /**
     * Adds the movement values of a Pair<Double, Double> to this Move2DDouble.
     * @param vect
     */
    public void add(Pair<Double, Double> vect) {
        value.setFirst(getFirst() + vect.getFirst());
        value.setSecond(getSecond() + vect.getSecond());
    }
    
	public void multiply(double scalar) {
		value.setFirst(getFirst() * scalar);
		value.setSecond(getSecond() * scalar);
	}

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Move2DDouble move) {
            return this.value.equals(move.value);
        }
        return false;
    }
    
    
    /**
     * Returns the Euclidean norm of this movement vector.
     *
     * @return vector norm
     */
    public double norm() {
        return Math.sqrt(getFirst() * getFirst() + getSecond() * getSecond());
    }

    /**
     * Normalizes this movement vector in place.
     * If the norm is zero, the vector is left unchanged.
     */
    public void normalize() {
        double norm = norm();

        if (norm == 0.0) {
            return;
        }

        value.setFirst(getFirst() / norm);
        value.setSecond(getSecond() / norm);
    }

    /**
     * Returns a normalized copy of this movement vector.
     *
     * @return normalized movement
     */
    public Move2DDouble normalized() {
        Move2DDouble copy = copy();
        copy.normalize();
        return copy;
    }

    /**
     * Creates a movement from an angle and a speed.
     *
     * @param angle angle in radians
     * @param speed movement speed
     * @return movement vector
     */
    public static Move2DDouble fromAngle(double angle, double speed) {
        return new Move2DDouble(
                Math.cos(angle) * speed,
                Math.sin(angle) * speed
        );
    }

    /**
     * Creates a movement from a vector and normalizes it to the given speed.
     *
     * @param dx x component
     * @param dy y component
     * @param speed desired speed
     * @return movement vector
     */
    public static Move2DDouble fromVector(double dx, double dy, double speed) {
        Move2DDouble move = new Move2DDouble(dx, dy);

        if (move.norm() == 0.0) {
            return idle();
        }

        move.normalize();
        move.multiply(speed);
        return move;
    }

    /**
     * Generates evenly distributed directional actions.
     *
     * @param numberOfDirections number of directions
     * @param speed movement speed
     * @return list of directional actions
     */
    public static List<Action> getDirectionalMoves(int numberOfDirections, double speed) {
        if (numberOfDirections <= 0) {
            throw new IllegalArgumentException("numberOfDirections must be strictly positive.");
        }

        ArrayList<Action> actions = new ArrayList<>();

        for (int i = 0; i < numberOfDirections; i++) {
            double angle = 2.0 * Math.PI * i / numberOfDirections;
            actions.add(fromAngle(angle, speed));
        }

        return actions;
    }

    /**
     * Generates evenly distributed directional actions with idle.
     *
     * @param numberOfDirections number of directions
     * @param speed movement speed
     * @return list of directional actions plus idle
     */
    public static List<Action> getDirectionalMovesWithIdle(int numberOfDirections, double speed) {
        ArrayList<Action> actions = new ArrayList<>(
                getDirectionalMoves(numberOfDirections, speed)
        );

        actions.add(idle());

        return actions;
    }
    

    @Override
    public String toString() {
        return "Move2DDouble [dx=" + value.getFirst() + "; dy=" + value.getSecond() + "]";
    }

    public static Move2DDouble up() {
        return new Move2DDouble(new Pair<>(0.0, 1.0));
    }

    public static Move2DDouble down() {
        return new Move2DDouble(new Pair<>(0.0, -1.0));
    }

    public static Move2DDouble left() {
        return new Move2DDouble(new Pair<>(-1.0, 0.0));
    }

    public static Move2DDouble right() {
        return new Move2DDouble(new Pair<>(1.0, 0.0));
    }

    public static Move2DDouble idle() {
        return new Move2DDouble(new Pair<>(0.0, 0.0));
    }

    public static List<Action> getVonNeumannMove() {
        return List.of(up(), down(), left(), right());
    }

    public static List<Action> getVonNeumannMoveWithIdle() {
        return List.of(up(), down(), left(), right(), idle());
    }

    public static Move2DDouble add(Move2DDouble a, Move2DDouble b) {
        return new Move2DDouble(
                new Pair<>(
                        a.getFirst() + b.getFirst(),
                        a.getSecond() + b.getSecond()
                )
        );
    }

    @Override
    public Move2DDouble copy() {
        return new Move2DDouble(new Pair<>(getFirst(), getSecond()));
    }
}