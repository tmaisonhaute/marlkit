package agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

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

public class MultiPoliciesAgentTest {

    @Test
    public void givenPolicyAndAlgorithm_whenConstructAgent_thenDefaultComponentsAreStored() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);

        // When
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(policy, algorithm);

        // Then
        assertThat(agent.getPolicy()).isSameAs(policy);
        assertThat(agent.getAlgorithm()).isSameAs(algorithm);
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG)).isNotNull();
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG).getExperiences()).isEmpty();
        verify(algorithm).setPolicy(policy);
    }

    @Test
    public void givenPolicyAndAlgorithm_whenConstructAgent_thenOnlyDefaultPolicyIsStored() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);

        // When
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(policy, algorithm);

        // Then
        assertThat(agent.getAllPolicies()).containsOnlyKeys(MultiPoliciesAgent.DEFAULT_TAG);
        assertThat(agent.getAllPolicies().get(MultiPoliciesAgent.DEFAULT_TAG)).isSameAs(policy);
    }

    @Test
    public void givenNullPolicy_whenConstructAgent_thenDefaultAlgorithmAndBufferAreStored() {
        // Given
        Algorithm algorithm = mock(Algorithm.class);

        // When
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(null, algorithm);

        // Then
        assertThat(agent.getPolicy()).isNull();
        assertThat(agent.getAlgorithm()).isSameAs(algorithm);
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG)).isNotNull();
        verify(algorithm, never()).setPolicy(any(Policy.class));
    }

    @Test
    public void givenNullAlgorithm_whenConstructAgent_thenDefaultPolicyAndBufferAreStored() {
        // Given
        Policy policy = mock(Policy.class);

        // When
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(policy, null);

        // Then
        assertThat(agent.getPolicy()).isSameAs(policy);
        assertThat(agent.getAlgorithm()).isNull();
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG)).isNotNull();
    }

    @Test
    public void givenAgent_whenAddPolicyAlgo_thenTaggedComponentsAndBufferAreStored() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        // When
        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        // Then
        assertThat(agent.getPolicy("secondary")).isSameAs(secondaryPolicy);
        assertThat(agent.getAlgorithm("secondary")).isSameAs(secondaryAlgorithm);
        assertThat(agent.getExperienceBuffer("secondary")).isNotNull();
        assertThat(agent.getExperienceBuffer("secondary").getExperiences()).isEmpty();
        verify(secondaryAlgorithm).setPolicy(secondaryPolicy);
    }

    @Test
    public void givenExistingTaggedBuffer_whenReplacePolicyAndAlgorithm_thenBufferIsPreserved() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy initialPolicy = mock(Policy.class);
        Algorithm initialAlgorithm = mock(Algorithm.class);
        Policy replacementPolicy = mock(Policy.class);
        Algorithm replacementAlgorithm = mock(Algorithm.class);
        Experience experience = mock(Experience.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("secondary", initialPolicy, initialAlgorithm);
        agent.feedbackExperience("secondary", experience);
        Batch initialBuffer = agent.getExperienceBuffer("secondary");

        // When
        agent.addPolicyAlgo("secondary", replacementPolicy, replacementAlgorithm);

        // Then
        assertThat(agent.getPolicy("secondary")).isSameAs(replacementPolicy);
        assertThat(agent.getAlgorithm("secondary")).isSameAs(replacementAlgorithm);
        assertThat(agent.getExperienceBuffer("secondary")).isSameAs(initialBuffer);
        assertThat(agent.getExperienceBuffer("secondary").getExperiences()).containsExactly(experience);
        verify(replacementAlgorithm).setPolicy(replacementPolicy);
    }

    @Test
    public void givenUnknownTag_whenGetPolicyAndAlgorithm_thenNullIsReturned() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();

        // When
        Policy policy = agent.getPolicy("unknown");
        Algorithm algorithm = agent.getAlgorithm("unknown");

        // Then
        assertThat(policy).isNull();
        assertThat(algorithm).isNull();
    }

    @Test
    public void givenAgent_whenSetDefaultPolicy_thenDefaultPolicyIsReplacedAndAlgorithmIsUpdated() {
        // Given
        Policy initialPolicy = mock(Policy.class);
        Policy replacementPolicy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(initialPolicy, algorithm);

        // When
        agent.setPolicy(replacementPolicy);

        // Then
        assertThat(agent.getPolicy()).isSameAs(replacementPolicy);
        verify(algorithm).setPolicy(replacementPolicy);
    }

    @Test
    public void givenAgentWithoutDefaultBuffer_whenSetPolicy_thenDefaultBufferIsCreated() {
        // Given
        Policy initialPolicy = mock(Policy.class);
        Policy replacementPolicy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(initialPolicy, algorithm);

        agent.removeExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG);

        // When
        agent.setPolicy(replacementPolicy);

        // Then
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG)).isNotNull();
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG).getExperiences()).isEmpty();
    }

    @Test
    public void givenAgent_whenSetDefaultPolicyToNull_thenAlgorithmPolicyIsNotUpdated() {
        // Given
        Policy initialPolicy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(initialPolicy, algorithm);

        // When
        agent.setPolicy(null);

        // Then
        assertThat(agent.getPolicy()).isNull();
        verify(algorithm, never()).setPolicy(null);
    }

    @Test
    public void givenAgent_whenSetDefaultAlgorithm_thenAlgorithmIsStoredAndReceivesPolicy() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm initialAlgorithm = mock(Algorithm.class);
        Algorithm replacementAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(policy, initialAlgorithm);

        // When
        agent.setAlgorithm(replacementAlgorithm);

        // Then
        assertThat(agent.getAlgorithm()).isSameAs(replacementAlgorithm);
        verify(replacementAlgorithm).setPolicy(policy);
    }

    @Test
    public void givenAgentWithoutDefaultBuffer_whenSetAlgorithm_thenDefaultBufferIsCreated() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm initialAlgorithm = mock(Algorithm.class);
        Algorithm replacementAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(policy, initialAlgorithm);

        agent.removeExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG);

        // When
        agent.setAlgorithm(replacementAlgorithm);

        // Then
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG)).isNotNull();
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG).getExperiences()).isEmpty();
    }

    @Test
    public void givenAgent_whenSetDefaultAlgorithmToNull_thenAlgorithmBecomesNull() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();

        // When
        agent.setAlgorithm(null);

        // Then
        assertThat(agent.getAlgorithm()).isNull();
    }

    @Test
    public void givenMultiplePolicies_whenGetAllPolicies_thenAllPoliciesAreReturned() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        // When
        Map<String, Policy> policies = agent.getAllPolicies();

        // Then
        assertThat(policies).containsEntry(MultiPoliciesAgent.DEFAULT_TAG, defaultPolicy);
        assertThat(policies).containsEntry("secondary", secondaryPolicy);
        assertThat(policies).hasSize(2);
    }

    @Test
    public void givenReturnedPoliciesMap_whenModificationAttempted_thenUnsupportedOperationExceptionIsThrown() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        Map<String, Policy> policies = agent.getAllPolicies();

        // When & Then
        assertThatThrownBy(() -> policies.put("other", mock(Policy.class))).isInstanceOf(UnsupportedOperationException.class);
    }
    
    @Test
    public void givenDefaultPolicy_whenSelectAction_thenDefaultPolicyIsUsed() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        Observation observation = mock(Observation.class);
        Action expectedAction = mock(Action.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(policy, algorithm);

        when(policy.selectAction(observation)).thenReturn(expectedAction);

        // When
        Action action = agent.selectAction(observation);

        // Then
        assertThat(action).isSameAs(expectedAction);
        verify(policy).selectAction(observation);
    }

    @Test
    public void givenTaggedPolicy_whenSelectActionWithTag_thenTaggedPolicyIsUsed() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        Observation observation = mock(Observation.class);
        Action expectedAction = mock(Action.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);
        when(secondaryPolicy.selectAction(observation)).thenReturn(expectedAction);

        // When
        Action action = agent.selectActionWithTag("secondary", observation);

        // Then
        assertThat(action).isSameAs(expectedAction);
        verify(secondaryPolicy).selectAction(observation);
        verify(defaultPolicy, never()).selectAction(any(Observation.class));
    }

    @Test
    public void givenUnknownTag_whenSelectActionWithTag_thenIllegalArgumentExceptionIsThrown() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        Observation observation = mock(Observation.class);

        // When & Then
        assertThatThrownBy(() -> agent.selectActionWithTag("unknown", observation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No policy found for tag: unknown");
    }

    @Test
    public void givenRegisteredObservation_whenTakeAction_thenDefaultPolicyActionInfluencesEnvironment() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        Observation observation = mock(Observation.class);
        Action action = mock(Action.class);
        MLKEnvironment environment = mock(MLKEnvironment.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(policy, algorithm);

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
    public void givenTaggedPolicyAndRegisteredObservation_whenTakeActionWithTag_thenTaggedActionInfluencesEnvironment() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        Observation observation = mock(Observation.class);
        Action action = mock(Action.class);
        MLKEnvironment environment = mock(MLKEnvironment.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);
        agent.setEnvironment(environment);
        agent.setRegisteredObservation(observation);
        when(secondaryPolicy.selectAction(observation)).thenReturn(action);

        // When
        agent.takeAction("secondary");

        // Then
        verify(secondaryPolicy).selectAction(observation);
        verify(environment).influence(agent, action);
    }

    @Test
    public void givenPoliciesAndAlgorithms_whenInitializeAll_thenEveryComponentIsInitialized() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        // When
        agent.initializeAll();

        // Then
        verify(defaultPolicy).init(agent);
        verify(secondaryPolicy).init(agent);
        verify(defaultAlgorithm).init(agent);
        verify(secondaryAlgorithm).init(agent);
    }

    @Test
    public void givenNullTaggedComponents_whenInitializeAll_thenOnlyNonNullComponentsAreInitialized() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("policy-only", secondaryPolicy, null);
        agent.addPolicyAlgo("algorithm-only", null, mock(Algorithm.class));

        // When
        agent.initializeAll();

        // Then
        verify(defaultPolicy).init(agent);
        verify(defaultAlgorithm).init(agent);
        verify(secondaryPolicy).init(agent);
    }

    @Test
    public void givenEnvironmentExperience_whenGetEnvExperience_thenEnvironmentIsQueriedForAgent() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        MLKEnvironment environment = mock(MLKEnvironment.class);
        Experience expectedExperience = mock(Experience.class);

        agent.setEnvironment(environment);
        when(environment.getExperience(agent)).thenReturn(expectedExperience);

        // When
        Experience experience = agent.getEnvExperience();

        // Then
        assertThat(experience).isSameAs(expectedExperience);
        verify(environment).getExperience(agent);
    }

    @Test
    public void givenEnvironmentExperience_whenCollectExperience_thenExperienceIsAddedToDefaultBuffer() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        MLKEnvironment environment = mock(MLKEnvironment.class);
        Experience experience = mock(Experience.class);

        agent.setEnvironment(environment);
        when(environment.getExperience(agent)).thenReturn(experience);

        // When
        agent.collectExperience();

        // Then
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG).getExperiences()).containsExactly(experience);
    }

    @Test
    public void givenNoEnvironmentExperience_whenCollectExperience_thenDefaultBufferRemainsEmpty() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        MLKEnvironment environment = mock(MLKEnvironment.class);

        agent.setEnvironment(environment);
        when(environment.getExperience(agent)).thenReturn(null);

        // When
        agent.collectExperience();

        // Then
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG).getExperiences()).isEmpty();
    }

    @Test
    public void givenExperience_whenFeedbackExperience_thenExperienceIsAddedOnlyToDefaultBuffer() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        Experience experience = mock(Experience.class);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        // When
        agent.feedbackExperience(experience);

        // Then
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG).getExperiences()).containsExactly(experience);
        assertThat(agent.getExperienceBuffer("secondary").getExperiences()).isEmpty();
    }

    @Test
    public void givenTaggedExperience_whenFeedbackExperienceWithTag_thenExperienceIsAddedOnlyToTaggedBuffer() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        Experience experience = mock(Experience.class);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        // When
        agent.feedbackExperience("secondary", experience);

        // Then
        assertThat(agent.getExperienceBuffer("secondary").getExperiences()).containsExactly(experience);
        assertThat(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG).getExperiences()).isEmpty();
    }

    @Test
    public void givenUnknownTag_whenFeedbackExperience_thenIllegalArgumentExceptionIsThrown() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        Experience experience = mock(Experience.class);

        // When & Then
        assertThatThrownBy(() -> agent.feedbackExperience("unknown", experience))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No experience buffer found for tag: unknown");
    }
    
    @Test
    public void givenAlgorithmsReadyToLearn_whenUpdatePolicy_thenEachReadyAlgorithmLearnsOnOwnBuffer() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);
        int timestep = 10;

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        Batch defaultBuffer = agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG);
        Batch secondaryBuffer = agent.getExperienceBuffer("secondary");

        when(defaultAlgorithm.shouldLearn(timestep, defaultBuffer)).thenReturn(true);
        when(secondaryAlgorithm.shouldLearn(timestep, secondaryBuffer)).thenReturn(true);

        // When
        agent.updatePolicy(timestep);

        // Then
        verify(defaultAlgorithm).shouldLearn(timestep, defaultBuffer);
        verify(secondaryAlgorithm).shouldLearn(timestep, secondaryBuffer);
        verify(defaultAlgorithm).learnOnBatch(same(defaultBuffer), any(AgentLogger.class));
        verify(secondaryAlgorithm).learnOnBatch(same(secondaryBuffer), any(AgentLogger.class));
    }

    @Test
    public void givenOnlyOneAlgorithmReadyToLearn_whenUpdatePolicy_thenOnlyReadyAlgorithmLearns() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);
        int timestep = 10;

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        Batch defaultBuffer = agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG);
        Batch secondaryBuffer = agent.getExperienceBuffer("secondary");

        when(defaultAlgorithm.shouldLearn(timestep, defaultBuffer)).thenReturn(false);
        when(secondaryAlgorithm.shouldLearn(timestep, secondaryBuffer)).thenReturn(true);

        // When
        agent.updatePolicy(timestep);

        // Then
        verify(defaultAlgorithm, never()).learnOnBatch(any(Batch.class), any(AgentLogger.class));
        verify(secondaryAlgorithm).learnOnBatch(same(secondaryBuffer), any(AgentLogger.class));
    }

    @Test
    public void givenNullAlgorithmForTaggedPolicy_whenUpdatePolicy_thenTagIsIgnored() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("secondary", secondaryPolicy, null);
        when(defaultAlgorithm.shouldLearn(5, agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG))).thenReturn(false);

        // When
        agent.updatePolicy(5);

        // Then
        verify(defaultAlgorithm).shouldLearn(5, agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG));
    }

    @Test
    public void givenDefaultAlgorithm_whenLearnOnBatch_thenDefaultBufferIsForwarded() {
        // Given
        Policy policy = mock(Policy.class);
        Algorithm algorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(policy, algorithm);

        // When
        agent.learnOnBatch();

        // Then
        verify(algorithm).learnOnBatch(same(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG)), any(AgentLogger.class));
    }

    @Test
    public void givenTaggedAlgorithm_whenLearnOnBatchWithTag_thenTaggedBufferIsForwarded() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        // When
        agent.learnOnBatch("secondary");

        // Then
        verify(secondaryAlgorithm).learnOnBatch(same(agent.getExperienceBuffer("secondary")), any(AgentLogger.class));
    }

    @Test
    public void givenUnknownTag_whenLearnOnBatchWithTag_thenIllegalArgumentExceptionIsThrown() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();

        // When & Then
        assertThatThrownBy(() -> agent.learnOnBatch("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No algorithm or buffer found for tag: unknown");
    }

    @Test
    public void givenTaggedPolicyWithoutAlgorithm_whenLearnOnBatchWithTag_thenIllegalArgumentExceptionIsThrown() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        agent.addPolicyAlgo("secondary", mock(Policy.class), null);

        // When & Then
        assertThatThrownBy(() -> agent.learnOnBatch("secondary"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No algorithm or buffer found for tag: secondary");
    }

    @Test
    public void givenMultipleAlgorithms_whenEndEpisode_thenEveryAlgorithmReceivesItsOwnBuffer() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        // When
        agent.endEpisode();

        // Then
        verify(defaultAlgorithm).endEpisode(same(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG)), any(AgentLogger.class));
        verify(secondaryAlgorithm).endEpisode(same(agent.getExperienceBuffer("secondary")), any(AgentLogger.class));
    }

    @Test
    public void givenTaggedPolicyWithoutAlgorithm_whenEndEpisode_thenTagIsIgnored() {
        // Given
        Policy defaultPolicy = mock(Policy.class);
        Algorithm defaultAlgorithm = mock(Algorithm.class);
        TestableMultiPoliciesAgent agent = new TestableMultiPoliciesAgent(defaultPolicy, defaultAlgorithm);

        agent.addPolicyAlgo("secondary", mock(Policy.class), null);

        // When
        agent.endEpisode();

        // Then
        verify(defaultAlgorithm).endEpisode(same(agent.getExperienceBuffer(MultiPoliciesAgent.DEFAULT_TAG)), any(AgentLogger.class));
    }

    @Test
    public void givenTaggedAlgorithm_whenEndEpisodeWithTag_thenTaggedBufferIsForwarded() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        Policy secondaryPolicy = mock(Policy.class);
        Algorithm secondaryAlgorithm = mock(Algorithm.class);

        agent.addPolicyAlgo("secondary", secondaryPolicy, secondaryAlgorithm);

        // When
        agent.endEpisode("secondary");

        // Then
        verify(secondaryAlgorithm).endEpisode(same(agent.getExperienceBuffer("secondary")), any(AgentLogger.class));
    }

    @Test
    public void givenUnknownTag_whenEndEpisodeWithTag_thenNoExceptionIsThrown() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();

        // When
        agent.endEpisode("unknown");

        // Then
        assertThat(agent.getPolicy()).isNotNull();
    }

    @Test
    public void givenObservation_whenSetRegisteredObservation_thenObservationIsStored() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        Observation observation = mock(Observation.class);

        // When
        agent.setRegisteredObservation(observation);

        // Then
        assertThat(agent.getRegisteredObservation()).isSameAs(observation);
    }

    @Test
    public void givenEnvironment_whenNotifySelfToEnvironment_thenAgentIsRegistered() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        MLKEnvironment environment = mock(MLKEnvironment.class);

        agent.setEnvironment(environment);

        // When
        agent.notifySelfToEnvironment();

        // Then
        verify(environment).addAgent(agent);
    }

    @Test
    public void givenEnvironment_whenGetMLKEnvironment_thenConfiguredEnvironmentIsReturned() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();
        MLKEnvironment environment = mock(MLKEnvironment.class);

        agent.setEnvironment(environment);

        // When
        MLKEnvironment returnedEnvironment = agent.getMLKEnvironment();

        // Then
        assertThat(returnedEnvironment).isSameAs(environment);
    }

    @Test
    public void givenAgent_whenGetSimuAgent_thenSameInstanceIsReturned() {
        // Given
        TestableMultiPoliciesAgent agent = createAgent();

        // When
        SimuAgent simuAgent = agent.getSimuAgent();

        // Then
        assertThat(simuAgent).isSameAs(agent);
    }

    private TestableMultiPoliciesAgent createAgent() {
        return new TestableMultiPoliciesAgent(mock(Policy.class), mock(Algorithm.class));
    }

    private static class TestableMultiPoliciesAgent extends MultiPoliciesAgent {

        private MLKEnvironment environment;

        public TestableMultiPoliciesAgent(Policy policy, Algorithm algorithm) {
            super(policy, algorithm);
        }

        public void setEnvironment(MLKEnvironment environment) {
            this.environment = environment;
        }

        public Batch getExperienceBuffer(String tag) {
            return experienceBuffers.get(tag);
        }

        public void removeExperienceBuffer(String tag) {
            experienceBuffers.remove(tag);
        }

        public Action selectActionWithTag(String tag, Observation observation) {
            return selectAction(tag, observation);
        }

        @Override
        public MLKEnvironment getMLKEnvironment() {
            return environment;
        }

		@Override
		public void addAdditionalRole(String role) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<String> getAdditionalRole() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}