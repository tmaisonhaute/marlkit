package experiment.configuration;

import java.util.Objects;

import experiment.configuration.agentspec.AgentSpec;

/**
 * Describes one homogeneous group of agents within an experiment configuration.
 * <p>
 * An {@code AgentGroupConfiguration} defines:
 * </p>
 * <ul>
 *   <li>the concrete agent class to instantiate;</li>
 *   <li>the number of agents in the group;</li>
 *   <li>the agent specification used to create learning components;</li>
 *   <li>the learning module used for this group;</li>
 *   <li>the communication module used for this group;</li>
 *   <li>the model-of-others module used for this group.</li>
 * </ul>
 * <p>
 * This makes it possible to define experiments with several agent populations,
 * for example hunters and preys, each with different learning, communication,
 * or modeling settings.
 * </p>
 */
public class AgentGroupConfiguration {

    private final int numberOfAgents;
    private final AgentModule agentFactory;
    private final AgentSpec agentSpec;

    public AgentGroupConfiguration(int numberOfAgents, AgentModule agentFactory, AgentSpec agentSpec) {
        if (numberOfAgents <= 0) {
            throw new IllegalArgumentException("numberOfAgents must be > 0.");
        }

        this.numberOfAgents = numberOfAgents;
        this.agentFactory = Objects.requireNonNull(agentFactory, "agentFactory");
        this.agentSpec = Objects.requireNonNull(agentSpec, "agentSpec");
    }

    public int getNumberOfAgents() {
        return numberOfAgents;
    }

    public AgentModule getAgentFactory() {
        return agentFactory;
    }

    public AgentSpec getAgentSpec() {
        return agentSpec;
    }
}