package experiment.configuration.agentspec;

import environment.observation.wrapperobservationvector.WrapperObservationVector;

/**
 * Optional capability for agent specifications that provide the observation
 * wrapper used by a centralized critic.
 */
public interface AgentSpecCriticInputWrapper extends AgentSpec {

    /**
     * Returns the wrapper used to transform centralized critic observations.
     *
     * @return the centralized critic input wrapper
     */
    WrapperObservationVector getCriticInputWrapper();
}