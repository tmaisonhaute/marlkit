package experiment.configuration.agentspec;

import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;

/**
 * Optional capability for agent specifications that provide a policy input wrapper.
 * <p>
 * This interface should be implemented by {@link AgentSpec} implementations when
 * the associated learning module needs to transform policy inputs into vector
 * representations, for example for neural network policies or critics.
 * </p>
 */
public interface AgentSpecInputWrapper extends AgentSpec {

    /**
     * Returns the wrapper used to transform policy inputs into vectors.
     *
     * @return the policy input wrapper
     */
    WrapperPolicyInputVector getInputWrapper();
}