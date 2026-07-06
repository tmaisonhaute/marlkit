package experiment.configuration;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import communication.NoCommunication;
import learning.Algorithm;
import learning.Policy;

/**
 * Internal module responsible for creating agents from agent group configurations.
 * <p>
 * This class instantiates agents using the standard constructor:
 * </p>
 * <pre>
 * Agent(Policy policy, Algorithm algorithm)
 * </pre>
 * <p>
 * If a communication model different from {@link NoCommunication} is provided, the
 * created agent must implement {@link MLKAgentCommunicating}. The communication model
 * is then assigned after construction through
 * {@link MLKAgentCommunicating#setCommunicationModel(CommunicationModel)}.
 * </p>
 * <p>
 * Model-of-other-agents configuration is intentionally not handled yet.
 * </p>
 */
public class AgentModule {

    /**
     * Creates all agents for the given agent group.
     *
     * @param groupConfiguration the group configuration
     * @return the created agents
     */
    public List<MLKAgent> createAgents(AgentGroupConfiguration groupConfiguration) {
        Objects.requireNonNull(groupConfiguration, "groupConfiguration");

        List<MLKAgent> agents = new ArrayList<>();

        for (int i = 0; i < groupConfiguration.getNumberOfAgents(); i++) {
            LearningComponents learning = groupConfiguration
                    .getLearningModule()
                    .createLearning(groupConfiguration.getAgentSpec());

            CommunicationModel communicationModel = groupConfiguration
                    .getCommunicationModule()
                    .createCommunicationModel();

            MLKAgent agent = instantiateAgent(
                    groupConfiguration.getAgentClass(),
                    learning.getPolicy(),
                    learning.getAlgorithm(),
                    groupConfiguration.getAgentSpec().getOtherRoleNames()
            );

            configureCommunication(agent, communicationModel);

            // TODO configure model of others.

            agents.add(agent);
        }

        return agents;
    }

    private MLKAgent instantiateAgent(
            Class<? extends MLKAgent> agentClass,
            Policy policy,
            Algorithm algorithm,
            List<String> otherRoleNames
    ) {
        try {
            Constructor<? extends MLKAgent> constructor = agentClass.getConstructor(Policy.class, Algorithm.class);
            
            MLKAgent agent = constructor.newInstance(policy, algorithm);
			for (String role : otherRoleNames) {
				agent.addAdditionalRole(role);
			}
            
            return agent;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot instantiate agent: " + agentClass.getName()
                            + ". Expected constructor: Agent(Policy, Algorithm).",
                    e
            );
        }
    }

    private void configureCommunication(MLKAgent agent, CommunicationModel communicationModel) {
        if (communicationModel == null || communicationModel instanceof NoCommunication) {
            return;
        }

        if (!(agent instanceof MLKAgentCommunicating communicatingAgent)) {
            throw new IllegalStateException(
                    "Agent " + agent.getClass().getName()
                            + " does not support communication but communication model "
                            + communicationModel.getClass().getName()
                            + " was provided."
            );
        }

        communicatingAgent.setCommunicationModel(communicationModel);
    }
}