package agent.action.wrapperactionvector;

import agent.action.Action;
import agent.action.Action2DMove;
import util.Pair;

public class WrapperAction2DMoveVector implements WrapperActionVector {

	@Override
	public double[] transform(Action action) {
		Action2DMove actionMove = (Action2DMove) action;
		double[] vector = new double[2];
		vector[0] = actionMove.getValue().getFirst();
		vector[1] = actionMove.getValue().getSecond();
		return vector;
	}

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
