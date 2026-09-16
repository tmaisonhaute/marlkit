package experiment.configuration;

import java.lang.reflect.Constructor;
import java.util.Objects;

import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import communication.NoCommunication;
import experiment.configuration.agentspec.AgentSpec;
import learning.Algorithm;
import learning.Policy;


public class AgentModule {

    private final Class<? extends MLKAgent> agentClass;
    private final LearningModule learningModule;
    private final CommunicationModule communicationModule;
    private final ModelOfOthersModule modelOfOthersModule;

    public AgentModule(Class<? extends MLKAgent> agentClass, LearningModule learningModule, CommunicationModule communicationModule, ModelOfOthersModule modelOfOthersModule) {
        this.agentClass = Objects.requireNonNull(agentClass, "agentClass");
        this.learningModule = Objects.requireNonNull(learningModule, "learningModule");
        this.communicationModule = Objects.requireNonNull(communicationModule, "communicationModule");
        this.modelOfOthersModule = Objects.requireNonNull(modelOfOthersModule, "modelOfOthersModule");
    }

    public MLKAgent createAgent(AgentSpec agentSpec) {
        Objects.requireNonNull(agentSpec, "agentSpec");

        LearningComponents learningComponents = learningModule.createLearning(agentSpec);
        CommunicationModel communicationModel = communicationModule.createCommunicationModel();

        MLKAgent agent = instantiateAgent(
                learningComponents.getPolicy(),
                learningComponents.getAlgorithm()
        );

        for (String role : agentSpec.getOtherRoleNames()) {
            agent.addAdditionalRole(role);
        }

        configureCommunication(agent, communicationModel);

        return agent;
    }

    private MLKAgent instantiateAgent(Policy policy, Algorithm algorithm) {
        try {
            Constructor<? extends MLKAgent> constructor =
                    agentClass.getConstructor(Policy.class, Algorithm.class);

            return constructor.newInstance(policy, algorithm);
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


    public Class<? extends MLKAgent> getAgentClass() {
        return agentClass;
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