package experiment.configuration.agentspec;

/**
 * Optional capability for agent specifications that expose the vector input
 * size used by a centralized critic.
 */
public interface AgentSpecCriticActionSize extends AgentSpec {

    /**
     * Returns the vector action size expected by the centralized critic.
     *
     * @return the centralized critic action size
     */
    int getCriticActionSize();
}