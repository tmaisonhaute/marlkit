package agent.action.wrapperactionvector;

import agent.action.Action;

/**
 * Interface for converting actions to and from vector representations.
 * Useful for neural network based learning algorithms.
 */
public interface WrapperActionVector {

	/**
     * Transforms the given Action into a vector.
     *
     * @param action the Action to transform
     * @return the vector representation of the Action
     */
    double[] transform(Action action);
    
    /**
     * Transforms the given vector into an Action.
     * 
     * @param vector the vector to transform
     * @return the Action representation of the vector
     */
    Action transform(double[] vector);
	
}
