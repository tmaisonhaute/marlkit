package environment.observation.wrapperobservationvector;

import java.util.List;
import java.util.Objects;

import environment.observation.JointObservation;
import environment.observation.MappedJointObservation;
import environment.observation.Observation;
import learning.policies.PolicyInput;

/**
 * Converts a {@link JointObservation} into a fixed-size vector by transforming
 * each local observation separately and concatenating the resulting vectors.
 *
 * <p>The order of the local observations is preserved. Consequently, the same
 * stable observation ordering must be used whenever joint observations are
 * created.</p>
 *
 * <p>All local observations are transformed with the same local observation
 * wrapper and must produce vectors of the same size.</p>
 */
public class WrapperJointObservation implements WrapperPolicyInputVector {

    private final WrapperPolicyInputVector localObservationWrapper;
    private final int localObservationVectorSize;
    private final int numberOfObservations;

    /**
     * Creates a wrapper for joint observations.
     *
     * @param localObservationWrapper the wrapper used to transform each local observation
     * @param localObservationVectorSize the vector size produced for one local observation
     * @param numberOfObservations the expected number of local observations
     * @throws NullPointerException if {@code localObservationWrapper} is {@code null}
     * @throws IllegalArgumentException if {@code localObservationVectorSize} or
     *                                  {@code numberOfObservations} is not strictly positive
     */
    public WrapperJointObservation(WrapperPolicyInputVector localObservationWrapper, int localObservationVectorSize, int numberOfObservations) {
        this.localObservationWrapper = Objects.requireNonNull(localObservationWrapper, "localObservationWrapper");

        if (localObservationVectorSize <= 0) {
            throw new IllegalArgumentException("localObservationVectorSize must be strictly positive.");
        }

        if (numberOfObservations <= 0) {
            throw new IllegalArgumentException("numberOfObservations must be strictly positive.");
        }

        this.localObservationVectorSize = localObservationVectorSize;
        this.numberOfObservations = numberOfObservations;
    }

    /**
     * Transforms a joint observation into a vector by concatenating the vector
     * representation of each local observation.
     *
     * @param input the joint observation to transform
     * @return the concatenated observation vector
     * @throws IllegalArgumentException if {@code input} is not a
     *                                  {@link JointObservation} or a {@link MappedJointObservation}, if it does not
     *                                  contain the expected number of local
     *                                  observations, or if a local vector has
     *                                  an unexpected size
     */
    @Override
    public double[] transform(PolicyInput input) {
        List<Observation> observations = getObservations(input);

        if (observations.size() != numberOfObservations) {
            throw new IllegalArgumentException("Expected " + numberOfObservations + " local observations but got " + observations.size() + ".");
        }

        double[] jointVector = new double[getVectorSize()];

        for (int i = 0; i < observations.size(); i++) {
            double[] localVector = localObservationWrapper.transform(observations.get(i));

            if (localVector.length != localObservationVectorSize) {
                throw new IllegalArgumentException("Expected local vector size " + localObservationVectorSize + " but got " + localVector.length + ".");
            }

            System.arraycopy(localVector, 0, jointVector, i * localObservationVectorSize, localObservationVectorSize);
        }

        return jointVector;
    }
    
    private List<Observation> getObservations(PolicyInput input) {
        if (input instanceof MappedJointObservation mappedJointObservation) {
            return mappedJointObservation.getObservations();
        }

        if (input instanceof JointObservation jointObservation) {
            return jointObservation.getObservations();
        }

        throw new IllegalArgumentException("Expected JointObservation or MappedJointObservation.");
    }

    /**
     * Reconstructs a joint observation from a concatenated vector.
     *
     * <p>Each vector segment is converted back into a local observation by the
     * local observation wrapper.</p>
     *
     * @param vector the concatenated joint-observation vector
     * @return the reconstructed joint observation
     * @throws NullPointerException if {@code vector} is {@code null}
     * @throws IllegalArgumentException if the vector size does not match the
     *                                  expected joint vector size or if the local
     *                                  wrapper does not reconstruct an observation
     */
    @Override
    public JointObservation transform(double[] vector) {
        Objects.requireNonNull(vector, "vector");

        if (vector.length != getVectorSize()) {
            throw new IllegalArgumentException("Expected joint vector size " + getVectorSize() + " but got " + vector.length + ".");
        }

        JointObservation jointObservation = new JointObservation();

        for (int i = 0; i < numberOfObservations; i++) {
            double[] localVector = new double[localObservationVectorSize];
            System.arraycopy(vector, i * localObservationVectorSize, localVector, 0, localObservationVectorSize);

            PolicyInput localInput = localObservationWrapper.transform(localVector);

            if (!(localInput instanceof Observation observation)) {
                throw new IllegalArgumentException("The local wrapper must reconstruct an Observation.");
            }

            jointObservation.addObservation(observation);
        }

        return jointObservation;
    }

    /**
     * Returns the size of the vector produced for a joint observation.
     *
     * @return the local observation vector size multiplied by the expected
     *         number of observations
     */
    public int getVectorSize() {
        return localObservationVectorSize * numberOfObservations;
    }

    /**
     * Returns the expected number of local observations.
     *
     * @return the expected number of observations
     */
    public int getNumberOfObservations() {
        return numberOfObservations;
    }

    /**
     * Returns the vector size produced for one local observation.
     *
     * @return the local observation vector size
     */
    public int getLocalObservationVectorSize() {
        return localObservationVectorSize;
    }

    /**
     * Returns the wrapper used for individual local observations.
     *
     * @return the local observation wrapper
     */
    public WrapperPolicyInputVector getLocalObservationWrapper() {
        return localObservationWrapper;
    }
}