package experiment.configuration.agentspec;

/**
 * Optional capability for agent specifications that expose a vector input size.
 */
public interface AgentSpecInputSize extends AgentSpec {

    /**
     * Returns the expected vector input size for learning components.
     *
     * @return the input vector size
     */
    int getInputSize();
}