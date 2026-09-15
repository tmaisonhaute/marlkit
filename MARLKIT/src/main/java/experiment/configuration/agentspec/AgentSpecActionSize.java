package experiment.configuration.agentspec;

public interface AgentSpecActionSize extends AgentSpec {

    /**
     * Returns the expected vector action size for learning components.
     *
     * @return the action vector size
     */
    int getActionSize();
}