package experiment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import agent.AgentStandard;
import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import agent.modelofotheragent.MLKAgentModelingOthers;
import agent.modelofotheragent.ModelsManager;
import communication.CommunicationModel;
import experiment.configuration.AgentGroupConfiguration;
import experiment.configuration.AgentModule;
import experiment.configuration.CommunicationModule;
import experiment.configuration.EnvironmentModule;
import experiment.configuration.ExperimentConfiguration;
import experiment.configuration.LearningComponents;
import experiment.configuration.LearningModule;
import experiment.configuration.ModelOfOthersModule;
import experiment.configuration.ModelsManagerFactory;
import experiment.configuration.RewardModelModule;
import experiment.configuration.SchedulerModule;
import experiment.configuration.SystemEvaluatorModule;
import experiment.configuration.agentspec.DefaultAgentSpec;
import learning.Algorithm;
import learning.Policy;

/**
 * Integration tests for the declarative MARL agent-creation mechanism.
 *
 * <p>These tests deliberately use the real configuration modules and real agent
 * construction path. Mockito is only used for leaf framework contracts whose
 * internal behaviour is unrelated to configuration.</p>
 */
public class ExperimentConfigurationIntegrationTest {

    private static final String OBSERVER_ROLE = "observer";
    private static final String COORDINATOR_ROLE = "coordinator";
    private static final String PREDATOR_GROUP = "predators";
    private static final String PREY_GROUP = "preys";

    @Test
    public void givenFullyConfiguredAgentGroup_whenCreateAgents_thenEveryAgentReceivesFreshRequestedComponents() {
        // Given
        RecordingLearningCreator learningCreator = new RecordingLearningCreator();
        RecordingCommunicationModule communicationModule = new RecordingCommunicationModule();
        RecordingModelsManagerFactory modelsManagerFactory = new RecordingModelsManagerFactory();
        ModelOfOthersModule modelOfOthersModule = new ModelOfOthersModule(modelsManagerFactory);
        AgentModule agentModule = new AgentModule(ConfiguredTestAgent.class, new LearningModule(learningCreator), communicationModule, modelOfOthersModule);
        DefaultAgentSpec agentSpec = new DefaultAgentSpec(List.of(), List.of(OBSERVER_ROLE, COORDINATOR_ROLE));
        AgentGroupConfiguration group = new AgentGroupConfiguration(3, agentModule, agentSpec, PREDATOR_GROUP);
        ExperimentConfiguration configuration = configurationNamed("complete-agent-configuration").agentGroup(group).build();

        // When
        List<MLKAgent> agents = configuration.createAgents();

        // Then
        assertThat(agents).hasSize(3).allSatisfy(agent -> assertThat(agent).isInstanceOf(ConfiguredTestAgent.class));
        assertThat(learningCreator.receivedSpecs).containsExactly(agentSpec, agentSpec, agentSpec);
        assertThat(communicationModule.createdModels).hasSize(3).doesNotHaveDuplicates();
        assertThat(modelsManagerFactory.createdManagers).hasSize(3).doesNotHaveDuplicates();

        for (int i = 0; i < agents.size(); i++) {
            ConfiguredTestAgent agent = (ConfiguredTestAgent) agents.get(i);
            assertThat(agent.getPolicy()).isSameAs(learningCreator.createdPolicies.get(i));
            assertThat(agent.getAlgorithm()).isSameAs(learningCreator.createdAlgorithms.get(i));
            assertThat(agent.getCommunicationModel()).isSameAs(communicationModule.createdModels.get(i));
            assertThat(agent.getModelsManager()).isSameAs(modelsManagerFactory.createdManagers.get(i));
            assertThat(agent.getAdditionalRole()).containsExactly(OBSERVER_ROLE, COORDINATOR_ROLE);
        }

        assertThat(learningCreator.createdPolicies).doesNotHaveDuplicates();
        assertThat(learningCreator.createdAlgorithms).doesNotHaveDuplicates();
    }

    @Test
    public void givenModelOfOthersWithoutTargetGroup_whenCreateAgents_thenEachAgentModelsOnlyOtherAgentsFromItsOwnGroup() {
        // Given
        RecordingModelsManagerFactory modelsManagerFactory = new RecordingModelsManagerFactory();
        AgentModule agentModule = agentModule(new ModelOfOthersModule(modelsManagerFactory));
        AgentGroupConfiguration group = new AgentGroupConfiguration(3, agentModule, new DefaultAgentSpec(List.of()), PREDATOR_GROUP);
        ExperimentConfiguration configuration = configurationNamed("intra-group-modeling").agentGroup(group).build();

        // When
        List<MLKAgent> agents = configuration.createAgents();

        // Then
        assertThat(modelsManagerFactory.calls).hasSize(3);
        for (MLKAgent predictingAgent : agents) {
            List<MLKAgent> expectedModeledAgents = agents.stream().filter(agent -> agent != predictingAgent).toList();
            assertThat(modelsManagerFactory.modeledAgentsByPredictor.get(predictingAgent)).containsExactlyElementsOf(expectedModeledAgents);
            assertThat(modelsManagerFactory.modeledAgentsByPredictor.get(predictingAgent)).doesNotContain(predictingAgent);
        }
    }

    @Test
    public void givenTwoGroupsAndTargetGroup_whenCreateAgents_thenSourceAgentsModelOnlyAgentsFromTargetGroup() {
        // Given
        RecordingModelsManagerFactory predatorManagerFactory = new RecordingModelsManagerFactory();
        ModelOfOthersModule predatorModelOfOthers = new ModelOfOthersModule(predatorManagerFactory).targetGroup(PREY_GROUP);
        AgentGroupConfiguration predators = new AgentGroupConfiguration(2, agentModule(predatorModelOfOthers), new DefaultAgentSpec(List.of()), PREDATOR_GROUP);
        AgentGroupConfiguration preys = new AgentGroupConfiguration(3, agentModule(new ModelOfOthersModule(null)), new DefaultAgentSpec(List.of()), PREY_GROUP);
        ExperimentConfiguration configuration = configurationNamed("targeted-modeling").agentGroup(predators).agentGroup(preys).build();

        // When
        List<MLKAgent> agents = configuration.createAgents();

        // Then
        List<MLKAgent> createdPredators = agents.subList(0, 2);
        List<MLKAgent> createdPreys = agents.subList(2, 5);
        assertThat(agents).hasSize(5);
        for (MLKAgent predator : createdPredators) {
            assertThat(predatorManagerFactory.modeledAgentsByPredictor.get(predator)).containsExactlyElementsOf(createdPreys);
            assertThat(predatorManagerFactory.modeledAgentsByPredictor.get(predator)).doesNotContainAnyElementsOf(createdPredators);
        }
    }

    @Test
    public void givenUnknownTargetGroup_whenCreateAgents_thenSourceAgentsReceiveAnEmptyModeledAgentList() {
        // Given
        RecordingModelsManagerFactory modelsManagerFactory = new RecordingModelsManagerFactory();
        ModelOfOthersModule modelOfOthersModule = new ModelOfOthersModule(modelsManagerFactory).targetGroup("unknown-group");
        AgentGroupConfiguration group = new AgentGroupConfiguration(2, agentModule(modelOfOthersModule), new DefaultAgentSpec(List.of()), PREDATOR_GROUP);
        ExperimentConfiguration configuration = configurationNamed("unknown-target").agentGroup(group).build();

        // When
        List<MLKAgent> agents = configuration.createAgents();

        // Then
        for (MLKAgent agent : agents) {
            assertThat(modelsManagerFactory.modeledAgentsByPredictor.get(agent)).isEmpty();
        }
    }

    @Test
    public void givenConfiguration_whenCreateAgentsTwice_thenTheTwoAgentGraphsShareNoStatefulComponents() {
        // Given
        RecordingLearningCreator learningCreator = new RecordingLearningCreator();
        RecordingCommunicationModule communicationModule = new RecordingCommunicationModule();
        RecordingModelsManagerFactory modelsManagerFactory = new RecordingModelsManagerFactory();
        AgentModule agentModule = new AgentModule(ConfiguredTestAgent.class, new LearningModule(learningCreator), communicationModule, new ModelOfOthersModule(modelsManagerFactory));
        AgentGroupConfiguration group = new AgentGroupConfiguration(2, agentModule, new DefaultAgentSpec(List.of()), PREDATOR_GROUP);
        ExperimentConfiguration configuration = configurationNamed("fresh-runs").agentGroup(group).build();

        // When
        List<MLKAgent> firstRunAgents = configuration.createAgents();
        List<MLKAgent> secondRunAgents = configuration.createAgents();

        // Then
        assertThat(firstRunAgents).doesNotContainAnyElementsOf(secondRunAgents);
        assertThat(firstRunAgents).allSatisfy(first -> assertThat(secondRunAgents).allSatisfy(second -> {
            assertThat(first.getPolicy()).isNotSameAs(second.getPolicy());
            assertThat(first.getAlgorithm()).isNotSameAs(second.getAlgorithm());
            assertThat(((ConfiguredTestAgent) first).getCommunicationModel()).isNotSameAs(((ConfiguredTestAgent) second).getCommunicationModel());
            assertThat(((ConfiguredTestAgent) first).getModelsManager()).isNotSameAs(((ConfiguredTestAgent) second).getModelsManager());
        }));
    }

    private static AgentModule agentModule(ModelOfOthersModule modelOfOthersModule) {
        return new AgentModule(ConfiguredTestAgent.class, new LearningModule(new RecordingLearningCreator()), new RecordingCommunicationModule(), modelOfOthersModule);
    }

    private static ExperimentConfiguration.Builder configurationNamed(String name) {
        return ExperimentConfiguration.named(name)
                .environment(mock(EnvironmentModule.class))
                .rewardModel(mock(RewardModelModule.class))
                .scheduler(mock(SchedulerModule.class))
                .systemEvaluator(mock(SystemEvaluatorModule.class));
    }

    public static class ConfiguredTestAgent extends AgentStandard implements MLKAgentCommunicating, MLKAgentModelingOthers {

        private CommunicationModel communicationModel;
        private ModelsManager modelsManager;

        public ConfiguredTestAgent(Policy policy, Algorithm algorithm) {
            super(policy, algorithm);
        }

        @Override
        public CommunicationModel getCommunicationModel() {
            return communicationModel;
        }

        @Override
        public void setCommunicationModel(CommunicationModel communicationModel) {
            this.communicationModel = communicationModel;
        }

        @Override
        public ModelsManager getModelsManager() {
            return modelsManager;
        }

        @Override
        public void setModelsManager(ModelsManager modelsManager) {
            this.modelsManager = modelsManager;
        }

        @Override
        public void updateModelsOfOtherAgents() {
            // Not relevant to configuration creation.
        }
    }

    private static class RecordingLearningCreator extends experiment.configuration.LearningComponentsCreator {

        private final List<experiment.configuration.agentspec.AgentSpec> receivedSpecs = new ArrayList<>();
        private final List<Policy> createdPolicies = new ArrayList<>();
        private final List<Algorithm> createdAlgorithms = new ArrayList<>();

        @Override
        public LearningComponents createLearning(experiment.configuration.agentspec.AgentSpec agentSpec) {
            receivedSpecs.add(agentSpec);
            Policy policy = mock(Policy.class, "policy-" + createdPolicies.size());
            Algorithm algorithm = mock(Algorithm.class, "algorithm-" + createdAlgorithms.size());
            createdPolicies.add(policy);
            createdAlgorithms.add(algorithm);
            return new LearningComponents(policy, algorithm);
        }
    }

    private static class RecordingCommunicationModule extends CommunicationModule {

        private final List<CommunicationModel> createdModels = new ArrayList<>();

        private RecordingCommunicationModule() {
            super(CommunicationModel.class);
        }

        @Override
        public CommunicationModel createCommunicationModel() {
            CommunicationModel model = mock(CommunicationModel.class, "communication-model-" + createdModels.size());
            createdModels.add(model);
            return model;
        }
    }

    private static class RecordingModelsManagerFactory implements ModelsManagerFactory {

        private final List<ModelsManager> createdManagers = new ArrayList<>();
        private final List<FactoryCall> calls = new ArrayList<>();
        private final Map<MLKAgent, List<MLKAgent>> modeledAgentsByPredictor = new IdentityHashMap<>();
        private final AtomicInteger managerIndex = new AtomicInteger();

        @Override
        public ModelsManager createModelsManager(MLKAgent predictingAgent, List<MLKAgent> modeledAgents) {
            ModelsManager manager = mock(ModelsManager.class, "models-manager-" + managerIndex.getAndIncrement());
            List<MLKAgent> modeledAgentsSnapshot = List.copyOf(modeledAgents);
            createdManagers.add(manager);
            calls.add(new FactoryCall(predictingAgent, modeledAgentsSnapshot, manager));
            modeledAgentsByPredictor.put(predictingAgent, modeledAgentsSnapshot);
            return manager;
        }
    }

    private record FactoryCall(MLKAgent predictingAgent, List<MLKAgent> modeledAgents, ModelsManager manager) {
    }
}