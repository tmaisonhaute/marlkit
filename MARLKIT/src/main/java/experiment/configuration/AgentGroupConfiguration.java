package experiment.configuration;

import java.util.Objects;

import agent.MLKAgent;
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

    private final Class<? extends MLKAgent> agentClass;
    private final int numberOfAgents;
    private final AgentSpec agentSpec;

    private final LearningModule learningModule;
    private final CommunicationModule communicationModule;
    private final ModelOfOthersModule modelOfOthersModule;

    /**
     * Creates an agent group configuration.
     *
     * @param agentClass the concrete agent class to instantiate
     * @param numberOfAgents the number of agents to create
     * @param agentSpec the agent specification used by the learning module
     * @param learningModule the learning module for this agent group
     * @param communicationModule the communication module for this agent group
     * @param modelOfOthersModule the model-of-others module for this agent group
     */
    public AgentGroupConfiguration(
            Class<? extends MLKAgent> agentClass,
            int numberOfAgents,
            AgentSpec agentSpec,
            LearningModule learningModule,
            CommunicationModule communicationModule,
            ModelOfOthersModule modelOfOthersModule
    ) {
        this.agentClass = Objects.requireNonNull(agentClass, "agentClass");
        this.agentSpec = Objects.requireNonNull(agentSpec, "agentSpec");
        this.learningModule = Objects.requireNonNull(learningModule, "learningModule");
        this.communicationModule = Objects.requireNonNull(communicationModule, "communicationModule");
        this.modelOfOthersModule = Objects.requireNonNull(modelOfOthersModule, "modelOfOthersModule");

        if (numberOfAgents <= 0) {
            throw new IllegalArgumentException("numberOfAgents must be > 0.");
        }

        this.numberOfAgents = numberOfAgents;
    }

    public Class<? extends MLKAgent> getAgentClass() {
        return agentClass;
    }

    public int getNumberOfAgents() {
        return numberOfAgents;
    }

    public AgentSpec getAgentSpec() {
        return agentSpec;
    }

    public LearningModule getLearningModule() {
        return learningModule;
    }

    public CommunicationModule getCommunicationModule() {
        return communicationModule;
    }

    public ModelOfOthersModule getModelOfOthersModule() {
        return modelOfOthersModule;
    }
}