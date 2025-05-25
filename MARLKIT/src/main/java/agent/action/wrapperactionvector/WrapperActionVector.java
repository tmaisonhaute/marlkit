package agent.action.wrapperactionvector;

import agent.action.Action;

public interface WrapperActionVector {

	/**
     * Transforms the given Action into a vector.
     *
     * @param Action the Action to transform
     * @return the vector representation of the Action
     */
    double[] transform(Action action);
    
    /**
     * Transforms the given vector into an Action.
     * @param vector the vector to transform
     * @return the Action representation of the vector
     */
    Action transform(double[] vector);
	
}
