package experiment.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import agent.MLKAgent;
import environment.MLKEnvironment;
import evaluation.SystemEvaluator;
import experience.ExperienceBuilder;
import experiment.configuration.agentspec.AgentSpec;
import reward.RewardModel;
import simulation.MLKScheduler;

public class ExperimentConfigurationTest {

    private static final String CONFIGURATION_NAME = "TestConfiguration";

    @Test
    public void givenValidBuilder_whenBuild_thenConfigurationNameIsStored() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();

        // When
        ExperimentConfiguration configuration = components.build();

        // Then
        assertThat(configuration.getName()).isEqualTo(CONFIGURATION_NAME);
    }

    @Test
    public void givenNullName_whenNamed_thenNullPointerExceptionIsThrown() {
        // Given & When & Then
        assertThatThrownBy(() -> ExperimentConfiguration.named(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name");
    }

    @Test
    public void givenNoSeedIndex_whenBuild_thenEmptyOptionalIntIsReturned() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();

        // When
        ExperimentConfiguration configuration = components.build();
        OptionalInt seedIndex = configuration.getSeedIndex();

        // Then
        assertThat(seedIndex).isNotNull();
        assertThat(seedIndex).isEmpty();
    }

    @Test
    public void givenSeedIndex_whenBuild_thenSeedIndexIsStored() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();

        // When
        ExperimentConfiguration configuration = components.builder.seedIndex(42).build();

        // Then
        assertThat(configuration.getSeedIndex()).isPresent();
        assertThat(configuration.getSeedIndex().getAsInt()).isEqualTo(42);
    }

    @Test
    public void givenNegativeSeedIndex_whenBuild_thenSeedIndexIsStored() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();

        // When
        ExperimentConfiguration configuration = components.builder.seedIndex(-1).build();

        // Then
        assertThat(configuration.getSeedIndex()).isPresent();
        assertThat(configuration.getSeedIndex().getAsInt()).isEqualTo(-1);
    }

    @Test
    public void givenMissingEnvironmentModule_whenBuild_thenNullPointerExceptionIsThrown() {
        // Given
        RewardModelModule rewardModelModule = mock(RewardModelModule.class);
        SchedulerModule schedulerModule = mock(SchedulerModule.class);
        SystemEvaluatorModule systemEvaluatorModule = mock(SystemEvaluatorModule.class);
        AgentGroupConfiguration agentGroup = mock(AgentGroupConfiguration.class);

        ExperimentConfiguration.Builder builder = ExperimentConfiguration.named(CONFIGURATION_NAME)
                .rewardModel(rewardModelModule)
                .scheduler(schedulerModule)
                .agentGroup(agentGroup)
                .systemEvaluator(systemEvaluatorModule);

        // When & Then
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("environmentModule");
    }

    @Test
    public void givenMissingRewardModelModule_whenBuild_thenNullPointerExceptionIsThrown() {
        // Given
        EnvironmentModule environmentModule = mock(EnvironmentModule.class);
        SchedulerModule schedulerModule = mock(SchedulerModule.class);
        SystemEvaluatorModule systemEvaluatorModule = mock(SystemEvaluatorModule.class);
        AgentGroupConfiguration agentGroup = mock(AgentGroupConfiguration.class);

        ExperimentConfiguration.Builder builder = ExperimentConfiguration.named(CONFIGURATION_NAME)
                .environment(environmentModule)
                .scheduler(schedulerModule)
                .agentGroup(agentGroup)
                .systemEvaluator(systemEvaluatorModule);

        // When & Then
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("rewardModelModule");
    }

    @Test
    public void givenMissingSchedulerModule_whenBuild_thenNullPointerExceptionIsThrown() {
        // Given
        EnvironmentModule environmentModule = mock(EnvironmentModule.class);
        RewardModelModule rewardModelModule = mock(RewardModelModule.class);
        SystemEvaluatorModule systemEvaluatorModule = mock(SystemEvaluatorModule.class);
        AgentGroupConfiguration agentGroup = mock(AgentGroupConfiguration.class);

        ExperimentConfiguration.Builder builder = ExperimentConfiguration.named(CONFIGURATION_NAME)
                .environment(environmentModule)
                .rewardModel(rewardModelModule)
                .agentGroup(agentGroup)
                .systemEvaluator(systemEvaluatorModule);

        // When & Then
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("schedulerModule");
    }

    @Test
    public void givenMissingSystemEvaluatorModule_whenBuild_thenNullPointerExceptionIsThrown() {
        // Given
        EnvironmentModule environmentModule = mock(EnvironmentModule.class);
        RewardModelModule rewardModelModule = mock(RewardModelModule.class);
        SchedulerModule schedulerModule = mock(SchedulerModule.class);
        AgentGroupConfiguration agentGroup = mock(AgentGroupConfiguration.class);

        ExperimentConfiguration.Builder builder = ExperimentConfiguration.named(CONFIGURATION_NAME)
                .environment(environmentModule)
                .rewardModel(rewardModelModule)
                .scheduler(schedulerModule)
                .agentGroup(agentGroup);

        // When & Then
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("systemEvaluatorModule");
    }

    @Test
    public void givenNoAgentGroup_whenBuild_thenIllegalArgumentExceptionIsThrown() {
        // Given
        EnvironmentModule environmentModule = mock(EnvironmentModule.class);
        RewardModelModule rewardModelModule = mock(RewardModelModule.class);
        SchedulerModule schedulerModule = mock(SchedulerModule.class);
        SystemEvaluatorModule systemEvaluatorModule = mock(SystemEvaluatorModule.class);

        ExperimentConfiguration.Builder builder = ExperimentConfiguration.named(CONFIGURATION_NAME)
                .environment(environmentModule)
                .rewardModel(rewardModelModule)
                .scheduler(schedulerModule)
                .systemEvaluator(systemEvaluatorModule);

        // When & Then
        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one agent group must be defined.");
    }

    @Test
    public void givenNullAgentGroup_whenAgentGroup_thenNullPointerExceptionIsThrown() {
        // Given
        ExperimentConfiguration.Builder builder = ExperimentConfiguration.named(CONFIGURATION_NAME);

        // When & Then
        assertThatThrownBy(() -> builder.agentGroup(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("agentGroupConfiguration");
    }

    @Test
    public void givenConfiguration_whenCreateRewardModel_thenRewardModelModuleResultIsReturned() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        RewardModel rewardModel = mock(RewardModel.class);

        when(components.rewardModelModule.createRewardModel()).thenReturn(rewardModel);

        ExperimentConfiguration configuration = components.build();

        // When
        RewardModel createdRewardModel = configuration.createRewardModel();

        // Then
        assertThat(createdRewardModel).isSameAs(rewardModel);
        verify(components.rewardModelModule).createRewardModel();
    }

    @Test
    public void givenConfiguration_whenCreateRewardModelTwice_thenModuleIsCalledTwice() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        RewardModel firstRewardModel = mock(RewardModel.class);
        RewardModel secondRewardModel = mock(RewardModel.class);

        when(components.rewardModelModule.createRewardModel()).thenReturn(firstRewardModel, secondRewardModel);

        ExperimentConfiguration configuration = components.build();

        // When
        RewardModel firstResult = configuration.createRewardModel();
        RewardModel secondResult = configuration.createRewardModel();

        // Then
        assertThat(firstResult).isSameAs(firstRewardModel);
        assertThat(secondResult).isSameAs(secondRewardModel);
        verify(components.rewardModelModule, times(2)).createRewardModel();
    }

    @Test
    public void givenConfiguration_whenCreateEnvironment_thenFreshRewardModelIsPassedToEnvironmentModule() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        RewardModel rewardModel = mock(RewardModel.class);
        MLKEnvironment environment = mock(MLKEnvironment.class);

        when(components.rewardModelModule.createRewardModel()).thenReturn(rewardModel);
        when(components.environmentModule.createEnvironment(rewardModel)).thenReturn(environment);

        ExperimentConfiguration configuration = components.build();

        // When
        MLKEnvironment createdEnvironment = configuration.createEnvironment();

        // Then
        assertThat(createdEnvironment).isSameAs(environment);
        verify(components.rewardModelModule).createRewardModel();
        verify(components.environmentModule).createEnvironment(same(rewardModel));
    }

    @Test
    public void givenExperienceBuilder_whenCreateEnvironment_thenExperienceBuilderIsSetOnEnvironment() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        RewardModel rewardModel = mock(RewardModel.class);
        MLKEnvironment environment = mock(MLKEnvironment.class);
        ExperienceBuilder experienceBuilder = mock(ExperienceBuilder.class);

        when(components.rewardModelModule.createRewardModel()).thenReturn(rewardModel);
        when(components.environmentModule.createEnvironment(rewardModel)).thenReturn(environment);

        ExperimentConfiguration configuration = components.builder.experienceBuilder(experienceBuilder).build();

        // When
        MLKEnvironment createdEnvironment = configuration.createEnvironment();

        // Then
        assertThat(createdEnvironment).isSameAs(environment);
        verify(environment).setExperienceBuilder(experienceBuilder);
    }

    @Test
    public void givenNoExperienceBuilder_whenCreateEnvironment_thenEnvironmentExperienceBuilderIsNotModified() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        RewardModel rewardModel = mock(RewardModel.class);
        MLKEnvironment environment = mock(MLKEnvironment.class);

        when(components.rewardModelModule.createRewardModel()).thenReturn(rewardModel);
        when(components.environmentModule.createEnvironment(rewardModel)).thenReturn(environment);

        ExperimentConfiguration configuration = components.build();

        // When
        configuration.createEnvironment();

        // Then
        verify(environment, never()).setExperienceBuilder(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void givenConfiguration_whenCreateEnvironment_thenRewardModelIsCreatedBeforeEnvironment() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        RewardModel rewardModel = mock(RewardModel.class);
        MLKEnvironment environment = mock(MLKEnvironment.class);

        when(components.rewardModelModule.createRewardModel()).thenReturn(rewardModel);
        when(components.environmentModule.createEnvironment(rewardModel)).thenReturn(environment);

        ExperimentConfiguration configuration = components.build();
        InOrder inOrder = inOrder(components.rewardModelModule, components.environmentModule);

        // When
        configuration.createEnvironment();

        // Then
        inOrder.verify(components.rewardModelModule).createRewardModel();
        inOrder.verify(components.environmentModule).createEnvironment(rewardModel);
    }

    @Test
    public void givenConfiguration_whenGetSchedulerClass_thenSchedulerModuleResultIsReturned() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();

        when(components.schedulerModule.getSchedulerClass()).thenAnswer(_ -> TestScheduler.class);

        ExperimentConfiguration configuration = components.build();

        // When
        Class<? extends MLKScheduler> schedulerClass = configuration.getSchedulerClass();

        // Then
        assertThat(schedulerClass).isEqualTo(TestScheduler.class);
        verify(components.schedulerModule).getSchedulerClass();
    }

    @Test
    public void givenConfiguration_whenCreateScheduler_thenSchedulerModuleResultIsReturned() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        MLKScheduler scheduler = mock(MLKScheduler.class);

        when(components.schedulerModule.createScheduler()).thenReturn(scheduler);

        ExperimentConfiguration configuration = components.build();

        // When
        MLKScheduler createdScheduler = configuration.createScheduler();

        // Then
        assertThat(createdScheduler).isSameAs(scheduler);
        verify(components.schedulerModule).createScheduler();
    }

    @Test
    public void givenConfiguration_whenCreateSchedulerTwice_thenSchedulerModuleIsCalledTwice() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        MLKScheduler firstScheduler = mock(MLKScheduler.class);
        MLKScheduler secondScheduler = mock(MLKScheduler.class);

        when(components.schedulerModule.createScheduler()).thenReturn(firstScheduler, secondScheduler);

        ExperimentConfiguration configuration = components.build();

        // When
        MLKScheduler firstResult = configuration.createScheduler();
        MLKScheduler secondResult = configuration.createScheduler();

        // Then
        assertThat(firstResult).isSameAs(firstScheduler);
        assertThat(secondResult).isSameAs(secondScheduler);
        verify(components.schedulerModule, times(2)).createScheduler();
    }

    @Test
    public void givenConfiguration_whenCreateSystemEvaluator_thenSystemEvaluatorModuleResultIsReturned() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        SystemEvaluator systemEvaluator = mock(SystemEvaluator.class);

        when(components.systemEvaluatorModule.createSystemEvaluator()).thenReturn(systemEvaluator);

        ExperimentConfiguration configuration = components.build();

        // When
        SystemEvaluator createdEvaluator = configuration.createSystemEvaluator();

        // Then
        assertThat(createdEvaluator).isSameAs(systemEvaluator);
        verify(components.systemEvaluatorModule).createSystemEvaluator();
    }

    @Test
    public void givenConfiguration_whenCreateSystemEvaluatorTwice_thenModuleIsCalledTwice() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents();
        SystemEvaluator firstEvaluator = mock(SystemEvaluator.class);
        SystemEvaluator secondEvaluator = mock(SystemEvaluator.class);

        when(components.systemEvaluatorModule.createSystemEvaluator()).thenReturn(firstEvaluator, secondEvaluator);

        ExperimentConfiguration configuration = components.build();

        // When
        SystemEvaluator firstResult = configuration.createSystemEvaluator();
        SystemEvaluator secondResult = configuration.createSystemEvaluator();

        // Then
        assertThat(firstResult).isSameAs(firstEvaluator);
        assertThat(secondResult).isSameAs(secondEvaluator);
        verify(components.systemEvaluatorModule, times(2)).createSystemEvaluator();
    }

    @Test
    public void givenSingleAgentGroup_whenCreateAgents_thenExpectedNumberOfAgentsIsCreated() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents(3);

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        MLKAgent thirdAgent = mock(MLKAgent.class);

        when(components.agentModule.createAgent(components.agentSpec)).thenReturn(firstAgent, secondAgent, thirdAgent);

        ExperimentConfiguration configuration = components.build();

        // When
        List<MLKAgent> agents = configuration.createAgents();

        // Then
        assertThat(agents).containsExactly(firstAgent, secondAgent, thirdAgent);
        verify(components.agentModule, times(3)).createAgent(components.agentSpec);
    }

    @Test
    public void givenSingleAgentGroup_whenCreateAgents_thenModelOfOthersIsConfiguredWithCreatedAgents() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents(2);

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        when(components.agentModule.createAgent(components.agentSpec)).thenReturn(firstAgent, secondAgent);

        ExperimentConfiguration configuration = components.build();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<AgentGroupConfiguration, List<MLKAgent>>> mapCaptor = ArgumentCaptor.forClass(Map.class);

        // When
        List<MLKAgent> agents = configuration.createAgents();

        // Then
        verify(components.modelOfOthersModule).configure(same(components.agentGroup), mapCaptor.capture());

        Map<AgentGroupConfiguration, List<MLKAgent>> agentsByGroup = mapCaptor.getValue();

        assertThat(agents).containsExactly(firstAgent, secondAgent);
        assertThat(agentsByGroup).hasSize(1);
        assertThat(agentsByGroup).containsKey(components.agentGroup);
        assertThat(agentsByGroup.get(components.agentGroup)).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void givenMultipleAgentGroups_whenCreateAgents_thenAgentsAreReturnedInGroupOrder() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents(2);

        AgentModule secondAgentModule = mock(AgentModule.class);
        AgentSpec secondAgentSpec = mock(AgentSpec.class);
        ModelOfOthersModule secondModelOfOthersModule = mock(ModelOfOthersModule.class);
        AgentGroupConfiguration secondAgentGroup = mock(AgentGroupConfiguration.class);

        MLKAgent firstGroupFirstAgent = mock(MLKAgent.class);
        MLKAgent firstGroupSecondAgent = mock(MLKAgent.class);
        MLKAgent secondGroupFirstAgent = mock(MLKAgent.class);
        MLKAgent secondGroupSecondAgent = mock(MLKAgent.class);
        MLKAgent secondGroupThirdAgent = mock(MLKAgent.class);

        when(components.agentModule.createAgent(components.agentSpec)).thenReturn(firstGroupFirstAgent, firstGroupSecondAgent);

        when(secondAgentGroup.getNumberOfAgents()).thenReturn(3);
        when(secondAgentGroup.getAgentModule()).thenReturn(secondAgentModule);
        when(secondAgentGroup.getAgentSpec()).thenReturn(secondAgentSpec);
        when(secondAgentModule.getModelOfOthersModule()).thenReturn(secondModelOfOthersModule);
        when(secondAgentModule.createAgent(secondAgentSpec)).thenReturn(secondGroupFirstAgent, secondGroupSecondAgent, secondGroupThirdAgent);

        ExperimentConfiguration configuration = components.builder.agentGroup(secondAgentGroup).build();

        // When
        List<MLKAgent> agents = configuration.createAgents();

        // Then
        assertThat(agents).containsExactly(firstGroupFirstAgent, firstGroupSecondAgent, secondGroupFirstAgent, secondGroupSecondAgent, secondGroupThirdAgent);

        verify(components.agentModule, times(2)).createAgent(components.agentSpec);
        verify(secondAgentModule, times(3)).createAgent(secondAgentSpec);
    }

    @Test
    public void givenMultipleAgentGroups_whenCreateAgents_thenEveryGroupModelOfOthersIsConfiguredWithAllGroups() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents(1);

        AgentModule secondAgentModule = mock(AgentModule.class);
        AgentSpec secondAgentSpec = mock(AgentSpec.class);
        ModelOfOthersModule secondModelOfOthersModule = mock(ModelOfOthersModule.class);
        AgentGroupConfiguration secondAgentGroup = mock(AgentGroupConfiguration.class);

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        when(components.agentModule.createAgent(components.agentSpec)).thenReturn(firstAgent);

        when(secondAgentGroup.getNumberOfAgents()).thenReturn(1);
        when(secondAgentGroup.getAgentModule()).thenReturn(secondAgentModule);
        when(secondAgentGroup.getAgentSpec()).thenReturn(secondAgentSpec);
        when(secondAgentModule.getModelOfOthersModule()).thenReturn(secondModelOfOthersModule);
        when(secondAgentModule.createAgent(secondAgentSpec)).thenReturn(secondAgent);

        ExperimentConfiguration configuration = components.builder.agentGroup(secondAgentGroup).build();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<AgentGroupConfiguration, List<MLKAgent>>> firstMapCaptor = ArgumentCaptor.forClass(Map.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<AgentGroupConfiguration, List<MLKAgent>>> secondMapCaptor = ArgumentCaptor.forClass(Map.class);

        // When
        configuration.createAgents();

        // Then
        verify(components.modelOfOthersModule).configure(same(components.agentGroup), firstMapCaptor.capture());
        verify(secondModelOfOthersModule).configure(same(secondAgentGroup), secondMapCaptor.capture());

        Map<AgentGroupConfiguration, List<MLKAgent>> firstReceivedMap = firstMapCaptor.getValue();
        Map<AgentGroupConfiguration, List<MLKAgent>> secondReceivedMap = secondMapCaptor.getValue();

        assertThat(firstReceivedMap).isSameAs(secondReceivedMap);
        assertThat(firstReceivedMap.keySet()).containsExactly(components.agentGroup, secondAgentGroup);
        assertThat(firstReceivedMap.get(components.agentGroup)).containsExactly(firstAgent);
        assertThat(firstReceivedMap.get(secondAgentGroup)).containsExactly(secondAgent);
    }

    @Test
    public void givenAgentGroups_whenCreateAgents_thenAllAgentsAreCreatedBeforeModelOfOthersConfiguration() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents(2);

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        when(components.agentModule.createAgent(components.agentSpec)).thenReturn(firstAgent, secondAgent);

        ExperimentConfiguration configuration = components.build();
        InOrder inOrder = inOrder(components.agentModule, components.modelOfOthersModule);

        // When
        configuration.createAgents();

        // Then
        inOrder.verify(components.agentModule, times(2)).createAgent(components.agentSpec);
        inOrder.verify(components.modelOfOthersModule).configure(same(components.agentGroup), anyMap());
    }

    @Test
    public void givenConfiguration_whenCreateAgentsTwice_thenFreshAgentsAreCreatedForEachCall() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents(1);

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        when(components.agentModule.createAgent(components.agentSpec)).thenReturn(firstAgent, secondAgent);

        ExperimentConfiguration configuration = components.build();

        // When
        List<MLKAgent> firstAgents = configuration.createAgents();
        List<MLKAgent> secondAgents = configuration.createAgents();

        // Then
        assertThat(firstAgents).containsExactly(firstAgent);
        assertThat(secondAgents).containsExactly(secondAgent);
        assertThat(firstAgents).isNotSameAs(secondAgents);

        verify(components.agentModule, times(2)).createAgent(components.agentSpec);
        verify(components.modelOfOthersModule, times(2)).configure(same(components.agentGroup), anyMap());
    }

    @Test
    public void givenCreatedAgentsList_whenModificationAttempted_thenUnsupportedOperationExceptionIsThrown() {
        // Given
        ValidConfigurationComponents components = new ValidConfigurationComponents(1);
        MLKAgent createdAgent = mock(MLKAgent.class);

        when(components.agentModule.createAgent(components.agentSpec)).thenReturn(createdAgent);

        ExperimentConfiguration configuration = components.build();
        List<MLKAgent> agents = configuration.createAgents();

        // When & Then
        assertThatThrownBy(() -> agents.add(mock(MLKAgent.class)))
                .isInstanceOf(UnsupportedOperationException.class);
    }



    private static class ValidConfigurationComponents {

        private final EnvironmentModule environmentModule;
        private final RewardModelModule rewardModelModule;
        private final SchedulerModule schedulerModule;
        private final SystemEvaluatorModule systemEvaluatorModule;

        private final AgentModule agentModule;
        private final AgentSpec agentSpec;
        private final ModelOfOthersModule modelOfOthersModule;
        private final AgentGroupConfiguration agentGroup;

        private final ExperimentConfiguration.Builder builder;

        private ValidConfigurationComponents() {
            this(1);
        }

        private ValidConfigurationComponents(int numberOfAgents) {
            environmentModule = mock(EnvironmentModule.class);
            rewardModelModule = mock(RewardModelModule.class);
            schedulerModule = mock(SchedulerModule.class);
            systemEvaluatorModule = mock(SystemEvaluatorModule.class);

            agentModule = mock(AgentModule.class);
            agentSpec = mock(AgentSpec.class);
            modelOfOthersModule = mock(ModelOfOthersModule.class);
            agentGroup = mock(AgentGroupConfiguration.class);

            when(agentGroup.getNumberOfAgents()).thenReturn(numberOfAgents);
            when(agentGroup.getAgentModule()).thenReturn(agentModule);
            when(agentGroup.getAgentSpec()).thenReturn(agentSpec);
            when(agentModule.getModelOfOthersModule()).thenReturn(modelOfOthersModule);

            builder = ExperimentConfiguration.named(CONFIGURATION_NAME)
                    .environment(environmentModule)
                    .rewardModel(rewardModelModule)
                    .scheduler(schedulerModule)
                    .agentGroup(agentGroup)
                    .systemEvaluator(systemEvaluatorModule);
        }

        private ExperimentConfiguration build() {
            return builder.build();
        }
    }

    private abstract static class TestScheduler extends MLKScheduler {
    }
}
















