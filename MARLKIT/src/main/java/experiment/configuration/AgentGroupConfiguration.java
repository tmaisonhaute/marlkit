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
 *   <li>the model of others module used for this group.</li>
 * </ul>
 * <p>
 * This makes it possible to define experiments with several agent populations,
 * for example hunters and preys, each with different learning, communication,
 * or modeling settings.
 * </p>
 */
public class AgentGroupConfiguration {

    private final int numberOfAgents;
    private final AgentModule agentModule;
    private final AgentSpec agentSpec;
    private final String groupId;

    public AgentGroupConfiguration(int numberOfAgents, AgentModule agentModule, AgentSpec agentSpec, String groupId) {
        if (numberOfAgents <= 0) {
            throw new IllegalArgumentException("numberOfAgents must be > 0.");
        }

        this.numberOfAgents = numberOfAgents;
        this.agentModule = Objects.requireNonNull(agentModule, "agentModule");
        this.agentSpec = Objects.requireNonNull(agentSpec, "agentSpec");
        this.groupId = groupId; 
    }
    
    public AgentGroupConfiguration(int numberOfAgents, AgentModule agentModule, AgentSpec agentSpec) {
        this(numberOfAgents, agentModule, agentSpec, null);
    }

    public int getNumberOfAgents() {
        return numberOfAgents;
    }

    public AgentModule getAgentModule() {
        return agentModule;
    }

    public AgentSpec getAgentSpec() {
        return agentSpec;
    }
    
    /**
     * Return the id of the group. This id can be used to identify a group of agents that should be treated differently, especially model by other agents.
     * This id can be null, in which case the group is not identifiable.
     * @return the id of the group, or null if the group is not identifiable.
     */
	public String getGroupId() {
		return groupId;
	}
}