package experiment.configuration.agentspec;

/**
 * Optional capability for agent specifications that expose the vector input
 * size used by a centralized critic.
 */
public interface AgentSpecCriticInputSize extends AgentSpec {

    /**
     * Returns the vector input size expected by the centralized critic.
     *
     * @return the centralized critic input size
     */
    int getCriticInputSize();
}