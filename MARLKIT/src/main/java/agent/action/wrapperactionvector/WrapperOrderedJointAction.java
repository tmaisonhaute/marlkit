package agent.action.wrapperactionvector;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import agent.action.JointAction;
import agent.action.OrderedJointAction;

public class WrapperOrderedJointAction implements WrapperActionVector{
	
	private final WrapperActionVector wrapperActionVector;
	private int nbAgents;
	
	public WrapperOrderedJointAction(WrapperActionVector wrapperActionVector, int nbAgents) {
		this.wrapperActionVector = wrapperActionVector;
		this.nbAgents = nbAgents;
	}

	@Override
	public double[] transform(Action action) {
		JointAction jointAction = (JointAction) action;
		double[] vector;
		int totalSize = 0;
		
		List<double[]> vectorsList = new ArrayList<>();
		for (Action individualAction : jointAction.getActions()) {
			double[] individualVector = wrapperActionVector.transform(individualAction);
			vectorsList.add(individualVector);
			totalSize += individualVector.length;
		}
		vector = new double[totalSize];
		
		int posCopy = 0;
		for (double[] v : vectorsList) {
			System.arraycopy(v, 0, vector, posCopy, v.length);
			posCopy += v.length;
		}
		
		return vector;
	}

	@Override
	public Action transform(double[] vector) {
		if (vector.length % nbAgents != 0) {
			throw new IllegalArgumentException("Vector length must be divisible by the number of agents.");
		}
		OrderedJointAction jointAction = new OrderedJointAction();
		int sizeIndividualAction = vector.length / nbAgents;
		
		for (int i = 0; i < nbAgents; i++) {
			double[] individualVector = new double[sizeIndividualAction];
			Action action = wrapperActionVector.transform(individualVector);
			jointAction.addAction(action);
		}
		return jointAction;
	}

}
