package environment.observation.wrapperobservationvector;

import environment.observation.Observation;

public interface WrapperObservationVector {
	/**
     * Transforms the given Observation into a vector.
     *
     * @param observation the Observation to transform
     * @return the vector representation of the Observation
     */
    double[] transform(Observation observation);
    
    /**
     * Transforms the given vector into an Observation.
     * @param vector the vector to transform
     * @return the Observation representation of the vector
     */
    Observation transform(double[] vector);
    
}
