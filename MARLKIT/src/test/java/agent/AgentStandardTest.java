package agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.Test;

import agent.action.Action;
import environment.MLKEnvironment;
import environment.observation.Observation;
import experience.Experience;
import learning.Algorithm;
import learning.Batch;
import learning.Policy;
import madkit.kernel.AgentLogger;
import madkit.simulation.SimuAgent;

public class AgentStandardTest {

    @Test
    public void givenPolicyAndAlgorithm_whenConstructAgent_thenComponentsAreStoredAndBufferIsEmpty() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);

        // When
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);

        // Then
        assertThat(agent.getPolicy()).isSameAs(policy);
        assertThat(agent.getAlgorithm()).isSameAs(algorithm);
        assertThat(agent.getExperienceBuffer().getExperiences()).isEmpty();
        assertThat(agent.getAdditionalRole()).isEmpty();
        assertThat(agent.getRegisteredObservation()).isNull();
    }

    @Test
    public void givenNoArguments_whenConstructAgent_thenComponentsAreNullAndCollectionsAreInitialized() {
        // Given & When
        TestableAgentStandard agent = new TestableAgentStandard();

        // Then
        assertThat(agent.getPolicy()).isNull();
        assertThat(agent.getAlgorithm()).isNull();
        assertThat(agent.getExperienceBuffer()).isNotNull();
        assertThat(agent.getExperienceBuffer().getExperiences()).isEmpty();
        assertThat(agent.getAdditionalRole()).isNotNull();
        assertThat(agent.getAdditionalRole()).isEmpty();
        assertThat(agent.getRegisteredObservation()).isNull();
    }

    @Test
    public void givenAgent_whenAddAdditionalRole_thenRoleIsStored() {
        // Given
        TestableAgentStandard agent = new TestableAgentStandard();

        // When
        agent.addAdditionalRole("communicating");

        // Then
        assertThat(agent.getAdditionalRole()).containsExactly("communicating");
    }

    @Test
    public void givenAgent_whenAddMultipleAdditionalRoles_thenInsertionOrderIsPreserved() {
        // Given
        TestableAgentStandard agent = new TestableAgentStandard();

        // When
        agent.addAdditionalRole("communicating");
        agent.addAdditionalRole("centralized-training");
        agent.addAdditionalRole("modeling-others");

        // Then
        assertThat(agent.getAdditionalRole()).containsExactly("communicating", "centralized-training", "modeling-others");
    }

    @Test
    public void givenAgent_whenAddSameAdditionalRoleTwice_thenDuplicatesAreStored() {
        // Given
        TestableAgentStandard agent = new TestableAgentStandard();

        // When
        agent.addAdditionalRole("communicating");
        agent.addAdditionalRole("communicating");

        // Then
        assertThat(agent.getAdditionalRole()).containsExactly("communicating", "communicating");
    }

    @Test
    public void givenPolicy_whenSelectAction_thenPolicyResultIsReturned() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        Observation observation = mock(Observation.class);
        Action expectedAction = mock(Action.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);

        when(policy.selectAction(observation)).thenReturn(expectedAction);

        // When
        Action returnedAction = agent.selectAction(observation);

        // Then
        assertThat(returnedAction).isSameAs(expectedAction);
        verify(policy).selectAction(observation);
    }

    @Test
    public void givenRegisteredObservation_whenTakeAction_thenSelectedActionInfluencesEnvironment() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        MLKEnvironment environment = mock(MLKEnvironment.class);
        Observation observation = mock(Observation.class);
        Action action = mock(Action.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);

        agent.setEnvironment(environment);
        agent.setRegisteredObservation(observation);
        when(policy.selectAction(observation)).thenReturn(action);

        // When
        agent.takeAction();

        // Then
        verify(policy).selectAction(observation);
        verify(environment).influence(agent, action);
    }

    @Test
    public void givenEnvironmentExperience_whenGetEnvExperience_thenEnvironmentIsQueriedForCurrentAgent() {
        // Given
        MLKEnvironment environment = mock(MLKEnvironment.class);
        Experience expectedExperience = mock(Experience.class);
        TestableAgentStandard agent = new TestableAgentStandard();

        agent.setEnvironment(environment);
        when(environment.getExperience(agent)).thenReturn(expectedExperience);

        // When
        Experience returnedExperience = agent.getEnvExperience();

        // Then
        assertThat(returnedExperience).isSameAs(expectedExperience);
        verify(environment).getExperience(agent);
    }

    @Test
    public void givenEnvironmentWithExperience_whenCollectExperience_thenExperienceIsAddedToBuffer() {
        // Given
        MLKEnvironment environment = mock(MLKEnvironment.class);
        Experience experience = mock(Experience.class);
        TestableAgentStandard agent = new TestableAgentStandard();

        agent.setEnvironment(environment);
        when(environment.getExperience(agent)).thenReturn(experience);

        // When
        agent.collectExperience();

        // Then
        assertThat(agent.getExperienceBuffer().getExperiences()).containsExactly(experience);
        verify(environment).getExperience(agent);
    }

    @Test
    public void givenEnvironmentWithoutExperience_whenCollectExperience_thenBufferRemainsEmpty() {
        // Given
        MLKEnvironment environment = mock(MLKEnvironment.class);
        TestableAgentStandard agent = new TestableAgentStandard();

        agent.setEnvironment(environment);
        when(environment.getExperience(agent)).thenReturn(null);

        // When
        agent.collectExperience();

        // Then
        assertThat(agent.getExperienceBuffer().getExperiences()).isEmpty();
        verify(environment).getExperience(agent);
    }

    @Test
    public void givenExperience_whenFeedbackExperience_thenExperienceIsAddedToBuffer() {
        // Given
        Experience experience = mock(Experience.class);
        TestableAgentStandard agent = new TestableAgentStandard();

        // When
        agent.feedbackExperience(experience);

        // Then
        assertThat(agent.getExperienceBuffer().getExperiences()).containsExactly(experience);
    }

    @Test
    public void givenMultipleExperiences_whenFeedbackExperience_thenInsertionOrderIsPreserved() {
        // Given
        Experience firstExperience = mock(Experience.class);
        Experience secondExperience = mock(Experience.class);
        Experience thirdExperience = mock(Experience.class);
        TestableAgentStandard agent = new TestableAgentStandard();

        // When
        agent.feedbackExperience(firstExperience);
        agent.feedbackExperience(secondExperience);
        agent.feedbackExperience(thirdExperience);

        // Then
        assertThat(agent.getExperienceBuffer().getExperiences()).containsExactly(firstExperience, secondExperience, thirdExperience);
    }

    @Test
    public void givenPolicyAndAlgorithm_whenInitializeAll_thenBothComponentsAreInitializedWithAgent() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);

        // When
        agent.initializeAll();

        // Then
        verify(policy).init(agent);
        verify(algorithm).init(agent);
    }

    @Test
    public void givenAlgorithmReadyToLearn_whenUpdatePolicy_thenLearningIsPerformed() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);
        int timestep = 12;

        when(algorithm.shouldLearn(timestep, agent.getExperienceBuffer())).thenReturn(true);

        // When
        agent.updatePolicy(timestep);

        // Then
        verify(algorithm).shouldLearn(timestep, agent.getExperienceBuffer());
        verify(algorithm).learnOnBatch(same(agent.getExperienceBuffer()), any(AgentLogger.class));
    }
    @Test
    public void givenAlgorithmNotReadyToLearn_whenUpdatePolicy_thenLearningIsNotPerformed() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);
        int timestep = 12;

        when(algorithm.shouldLearn(timestep, agent.getExperienceBuffer())).thenReturn(false);

        // When
        agent.updatePolicy(timestep);

        // Then
        verify(algorithm).shouldLearn(timestep, agent.getExperienceBuffer());
        verify(algorithm, never()).learnOnBatch(any(Batch.class), any(AgentLogger.class));
    }

    @Test
    public void givenAlgorithm_whenLearnOnBatch_thenAgentBufferAndLoggerAreForwarded() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);

        // When
        agent.learnOnBatch();

        // Then
        verify(algorithm).learnOnBatch(same(agent.getExperienceBuffer()), same(agent.getLogger()));
    }

    @Test
    public void givenAlgorithm_whenEndEpisode_thenAgentBufferAndLoggerAreForwarded() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);

        // When
        agent.endEpisode();

        // Then
        verify(algorithm).endEpisode(same(agent.getExperienceBuffer()), same(agent.getLogger()));
    }

    @Test
    public void givenAgent_whenSetPolicy_thenPolicyIsReplaced() {
        // Given
        Policy initialPolicy = mock(Policy.class);
        Policy replacementPolicy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(initialPolicy, algorithm);

        // When
        agent.setPolicy(replacementPolicy);

        // Then
        assertThat(agent.getPolicy()).isSameAs(replacementPolicy);
    }

    @Test
    public void givenAgent_whenSetPolicyToNull_thenPolicyBecomesNull() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);

        // When
        agent.setPolicy(null);

        // Then
        assertThat(agent.getPolicy()).isNull();
    }

    @Test
    public void givenAgent_whenSetAlgorithm_thenAlgorithmIsReplaced() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm initialAlgorithm = mock(Algorithm.class);
        Algorithm replacementAlgorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, initialAlgorithm);

        // When
        agent.setAlgorithm(replacementAlgorithm);

        // Then
        assertThat(agent.getAlgorithm()).isSameAs(replacementAlgorithm);
    }

    @Test
    public void givenAgent_whenSetAlgorithmToNull_thenAlgorithmBecomesNull() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableAgentStandard agent = new TestableAgentStandard(policy, algorithm);

        // When
        agent.setAlgorithm(null);

        // Then
        assertThat(agent.getAlgorithm()).isNull();
    }

    @Test
    public void givenObservation_whenSetRegisteredObservation_thenObservationIsStored() {
        // Given
        Observation observation = mock(Observation.class);
        TestableAgentStandard agent = new TestableAgentStandard();

        // When
        agent.setRegisteredObservation(observation);

        // Then
        assertThat(agent.getRegisteredObservation()).isSameAs(observation);
    }

    @Test
    public void givenRegisteredObservation_whenSetRegisteredObservationToNull_thenObservationIsCleared() {
        // Given
        Observation observation = mock(Observation.class);
        TestableAgentStandard agent = new TestableAgentStandard();
        agent.setRegisteredObservation(observation);

        // When
        agent.setRegisteredObservation(null);

        // Then
        assertThat(agent.getRegisteredObservation()).isNull();
    }

    @Test
    public void givenEnvironment_whenGetMLKEnvironment_thenConfiguredEnvironmentIsReturned() {
        // Given
        MLKEnvironment environment = mock(MLKEnvironment.class);
        TestableAgentStandard agent = new TestableAgentStandard();
        agent.setEnvironment(environment);

        // When
        MLKEnvironment returnedEnvironment = agent.getMLKEnvironment();

        // Then
        assertThat(returnedEnvironment).isSameAs(environment);
    }

    @Test
    public void givenEnvironment_whenNotifySelfToEnvironment_thenAgentIsRegistered() {
        // Given
        MLKEnvironment environment = mock(MLKEnvironment.class);
        TestableAgentStandard agent = new TestableAgentStandard();
        agent.setEnvironment(environment);

        // When
        agent.notifySelfToEnvironment();

        // Then
        verify(environment).addAgent(agent);
    }

    @Test
    public void givenAgent_whenGetSimuAgent_thenSameInstanceIsReturned() {
        // Given
        TestableAgentStandard agent = new TestableAgentStandard();

        // When
        SimuAgent simuAgent = agent.getSimuAgent();

        // Then
        assertThat(simuAgent).isSameAs(agent);
    }

    @Test
    public void givenReturnedAdditionalRoles_whenModified_thenInternalRoleListIsModified() {
        // Given
        TestableAgentStandard agent = new TestableAgentStandard();
        agent.addAdditionalRole("role-1");

        // When
        List<String> returnedRoles = agent.getAdditionalRole();
        returnedRoles.add("role-2");

        // Then
        assertThat(agent.getAdditionalRole()).containsExactly("role-1", "role-2");
    }

    private static class TestableAgentStandard extends AgentStandard {

        private MLKEnvironment environment;

        public TestableAgentStandard() {
            super();
        }

        public TestableAgentStandard(Policy policy, Algorithm algorithm) {
            super(policy, algorithm);
        }

        public void setEnvironment(MLKEnvironment environment) {
            this.environment = environment;
        }

        public Batch getExperienceBuffer() {
            return experienceBuffer;
        }

        @Override
        public MLKEnvironment getMLKEnvironment() {
            return environment;
        }
    }
}