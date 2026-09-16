package experiment.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import agent.MLKAgent;
import agent.modelofotheragent.MLKAgentModelingOthers;
import agent.modelofotheragent.ModelsManager;

/**
 * Module responsible for creating models manager instances for agents that model other agents.
 * <p>
 * If the provided {@code modelsManagerClass} is {@code null}, this module represents the
 * absence of model of other agents mechanism and returns {@code null}.
 * </p>
 */
public class ModelOfOthersModule {

	private final ModelsManagerFactory modelsManagerFactory;
    private final List<String> targetGroupIds;

    /**
     * Creates a model of others module.
     *
     * @param modelsManagerClass the models manager class to instantiate, or {@code null}
     *                           if no model of others mechanism should be used
     */
    public ModelOfOthersModule(ModelsManagerFactory modelsManagerFactory) {
        this.modelsManagerFactory = modelsManagerFactory;
        this.targetGroupIds = new ArrayList<>();
    }
    
    /**
     * Adds a target group ID to the list of groups that agents will model.
     * 
     * @param groupId the ID of the group to be modeled.
     * @return this module instance for method chaining.
     * @throws NullPointerException if {@code groupId} is null.
     */
    public ModelOfOthersModule targetGroup(String groupId) {
    	Objects.requireNonNull(groupId, "groupId");
    	
    	if (!targetGroupIds.contains(groupId)) {
    		targetGroupIds.add(groupId);
		}
    	return this;
    }


    /**
     * Configures the agents in the source group to model other agents based on the provided configuration.
     * 
     * <p> If {@code targetGroupIds} is empty, all agents in the source group will model all other agents in the same group.
     * </p>
     * 
     * <p> If {@code targetGroupIds} is not empty, only agents in the specified target groups will be modeled.
     * </p>
     * 
     * @param sourceGroup the source agent group configuration
     * @param agentsByGroup a map of agent group configurations to their corresponding agents
     */
    public void configure(AgentGroupConfiguration sourceGroup, Map<AgentGroupConfiguration, List<MLKAgent>> agentsByGroup) {
        if (modelsManagerFactory == null) {
            return;
        }

        List<MLKAgent> agentsToConfigure = agentsByGroup.get(sourceGroup);
        List<MLKAgent> targetedAgents = resolveTargetedAgents(sourceGroup, agentsByGroup);

        for (MLKAgent agent : agentsToConfigure) {
            if (!(agent instanceof MLKAgentModelingOthers modelingAgent)) {
                throw new IllegalStateException("Agent " + agent.getClass().getName() + " does not support modeling other agents.");
            }

            List<MLKAgent> modeledAgents = targetedAgents.stream().filter(target -> target != agent).toList();

            ModelsManager modelsManager = modelsManagerFactory.createModelsManager(agent, modeledAgents);

            modelingAgent.setModelsManager(Objects.requireNonNull(modelsManager, "modelsManager"));
        }
    }
    
    /**
     * Resolves the list of agents that should be modeled by the agents in the source group. 
     * 
     * <p> If {@code targetGroupIds} is empty, all agents in the source group will model all other agents in the same group.
     * </p>
     * <p> If {@code targetGroupIds} is not empty, only agents in the specified target groups will be modeled.
     * </p>
     * 
     * @param sourceGroup the source agent group configuration
     * @param agentsByGroup a map of agent group configurations to their corresponding agents
     * @return the list of agents to be modeled by the agents in the source group
     */
    private List<MLKAgent> resolveTargetedAgents(AgentGroupConfiguration sourceGroup, Map<AgentGroupConfiguration, List<MLKAgent>> agentsByGroup) {
        if (targetGroupIds.isEmpty()) {
            return agentsByGroup.get(sourceGroup);
        }

        List<MLKAgent> targetedAgents = new ArrayList<>();

        for (Map.Entry<AgentGroupConfiguration, List<MLKAgent>> entry : agentsByGroup.entrySet()) {
            String groupId = entry.getKey().getGroupId();

            if (groupId != null && targetGroupIds.contains(groupId)) {
                targetedAgents.addAll(entry.getValue());
            }
        }

        return targetedAgents;
    }

    /**
     * Returns the models manager factory associated with this module.
     * 
     * @return the models manager factory, or {@code null} if no model of others mechanism is configured
     */
    public ModelsManagerFactory getModelsManagerFactory() {
        return modelsManagerFactory;
    }

    /**
     * Checks whether this module disables model of other agents.
     *
     * @return true if no models manager is configured
     */
    public boolean isDisabled() {
        return modelsManagerFactory == null;
    }
}