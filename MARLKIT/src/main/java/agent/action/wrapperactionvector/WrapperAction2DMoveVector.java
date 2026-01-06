package agent.action.wrapperactionvector;

import agent.action.Action;
import agent.action.Action2DMove;
import util.Pair;

/**
 * Wrapper that converts Action2DMove to/from a 2-dimensional vector representation.
 */
public class WrapperAction2DMoveVector implements WrapperActionVector {

	/**
	 * Converts a 2D movement action to a 2-element vector [dx, dy].
	 *
	 * @param action the Action2DMove to convert
	 * @return a 2-element array containing the movement components
	 */
	@Override
	public double[] transform(Action action) {
		Action2DMove actionMove = (Action2DMove) action;
		double[] vector = new double[2];
		vector[0] = actionMove.getValue().getFirst();
		vector[1] = actionMove.getValue().getSecond();
		return vector;
	}

	/**
	 * Converts a 2-element vector to an Action2DMove.
	 *
	 * @param vector a 2-element array representing [dx, dy]
	 * @return the corresponding Action2DMove
	 * @throws IllegalArgumentException if vector length is not 2
	 */
	@Override
	public Action transform(double[] vector) {
		if (vector.length != 2) {
			throw new IllegalArgumentException("Action2DMove must have a vector of size 2");
		}
		double dx = vector[0];
		double dy = vector[1];
		return new Action2DMove(new Pair<>((int) dx, (int) dy));
	}

}
