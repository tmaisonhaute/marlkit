package environment.observation.wrapperobservationvector;

import learning.policies.PolicyInput;

public interface WrapperPolicyInputVector {
	/**
     * Transforms the given Observation into a vector.
     *
     * @param observation the Observation to transform
     * @return the vector representation of the Observation
     */
    double[] transform(PolicyInput observation);
    
    /**
     * Transforms the given vector into an Observation.
     * @param vector the vector to transform
     * @return the Observation representation of the vector
     */
    PolicyInput transform(double[] vector);
    
}
