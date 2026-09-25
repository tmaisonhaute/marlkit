package agent.action.wrapperactionvector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import agent.action.Action;
import agent.action.JointAction;
import agent.action.OrderedJointAction;

/**
 * Wrapper that converts a JointAction to/from a vector representation by concatenating the individual action vectors.
 * 
 * <p>
 * This class uses another WrapperActionVector to handle the individual actions within the JointAction.
 * </p>
 * 
 * @see WrapperActionVector
 */
public class WrapperOrderedJointAction implements WrapperActionVector{
	
	private final WrapperActionVector wrapperActionVector;
	private int nbAgents;
	
	/**
	 * Creates a new WrapperOrderedJointAction with the specified individual action wrapper and number of agents.
	 * 
	 * <p>
	 * The wrapperActionVector is used to convert individual actions to and from vector representations.
	 * </p>
	 * 
	 * @param wrapperActionVector the wrapper for individual actions
	 * @param nbAgents the number of agents in the joint action
	 * @throws NullPointerException if wrapperActionVector is null
	 */
	public WrapperOrderedJointAction(WrapperActionVector wrapperActionVector, int nbAgents) {
		Objects.requireNonNull(wrapperActionVector, "wrapperActionVector cannot be null");
		if (nbAgents < 0) {
			throw new IllegalArgumentException("Number of agents (nbAgents) cannot be negative.");
		}
		this.wrapperActionVector = wrapperActionVector;
		this.nbAgents = nbAgents;
		
	}

	@Override
	/**
	 * Transforms a JointAction into a concatenated vector representation using the individual action wrapper.
	 * @throws IllegalArgumentException if the action is not an instance of JointAction
	 */
	public double[] transform(Action action) {
		if (!(action instanceof JointAction jointAction)) {
			throw new ClassCastException("Action must be an instance of JointAction.");
		}
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

	/**
	 * Transforms a concatenated vector representation back into an OrderedJointAction using the individual action wrapper.
	 */
	@Override
	public Action transform(double[] vector) {
	    if (vector.length % nbAgents != 0) {
	        throw new IllegalArgumentException(
	            "Vector length must be divisible by the number of agents."
	        );
	    }

	    OrderedJointAction jointAction = new OrderedJointAction();
	    int sizeIndividualAction = vector.length / nbAgents;

	    for (int i = 0; i < nbAgents; i++) {
	        int fromIndex = i * sizeIndividualAction;
	        int toIndex = fromIndex + sizeIndividualAction;

	        double[] individualVector = Arrays.copyOfRange(vector, fromIndex, toIndex);

	        Action action = wrapperActionVector.transform(individualVector);
	        jointAction.addAction(action);
	    }

	    return jointAction;
	}

}
