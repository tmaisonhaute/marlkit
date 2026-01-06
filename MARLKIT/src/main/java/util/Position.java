package util;

/**
 * A 2D position with x and y coordinates.
 */
public class Position {
    public double x;
    public double y;

	/**
	 * Creates a new position.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

	/**
	 * Computes the Euclidean distance to another position.
	 *
	 * @param other the other position
	 * @return the distance
	 */
    public double distancePoint(Position other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

	/**
	 * Creates a copy of this position.
	 *
	 * @return a new position with the same coordinates
	 */
    public Position copy(){
        return new Position(x, y);
    }
}
