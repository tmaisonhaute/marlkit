package centralizedtraining;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.ActionInt;
import agent.action.JointAction;
import environment.MLKEnvironment;
import environment.observation.Observation;
import experience.DefaultExperience;
import experience.Experience;
import learning.Algorithm;
import learning.Batch;
import learning.Critic;
import learning.Policy;
import learning.algorithms.ActorCritic;
import learning.policies.CategoricalPolicyGradient;
import learning.policies.PolicyInput;
import madkit.kernel.AgentLogger;
import madkit.simulation.SimuAgent;
import reward.RewardStandard;

public class CentralizedCriticCollectExperienceActivatorTest {

    @Test
    public void givenNoAliveAgent_whenExecute_thenDoNothing() {
        // Given
        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of());

        // When
        activator.execute();

        // Then
        assertThat(activator.collectExperiencesCallCount).isZero();
    }

    @Test
    public void givenAgentsWithNoExperience_whenExecute_thenDoNothing() {
        // Given
        RecordingCritic critic1 = new RecordingCritic();
        RecordingCritic critic2 = new RecordingCritic();

        DummyAgent agent1 = new DummyAgent(null, new DummyActorCritic(critic1));
        DummyAgent agent2 = new DummyAgent(null, new DummyActorCritic(critic2));

        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of(agent1, agent2));

        // When
        activator.execute();

        // Then
        assertThat(agent1.feedbackExperiences).isEmpty();
        assertThat(agent2.feedbackExperiences).isEmpty();
        assertThat(critic1.enrichmentCalls).isEmpty();
        assertThat(critic2.enrichmentCalls).isEmpty();
    }

    @Test
    public void givenTwoAgents_whenExecute_thenMergeInputsInAgentOrder() {
        // Given
        DummyPolicyInput input1 = new DummyPolicyInput(List.of("o1"));
        DummyPolicyInput input2 = new DummyPolicyInput(List.of("o2"));

        Experience experience1 = new DefaultExperience(input1, new ActionInt(1), new RewardStandard(10));
        Experience experience2 = new DefaultExperience(input2, new ActionInt(2), new RewardStandard(20));

        RecordingCritic critic1 = new RecordingCritic();
        RecordingCritic critic2 = new RecordingCritic();

        DummyAgent agent1 = new DummyAgent(experience1, new DummyActorCritic(critic1));
        DummyAgent agent2 = new DummyAgent(experience2, new DummyActorCritic(critic2));

        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of(agent1, agent2));

        // When
        activator.execute();

        // Then
        Experience enrichedExperience1 = critic1.enrichmentCalls.get(0).enrichedExperience();
        Experience enrichedExperience2 = critic2.enrichmentCalls.get(0).enrichedExperience();

        assertThat(enrichedExperience1.getInput()).isEqualTo(new DummyPolicyInput(List.of("o1", "o2")));
        assertThat(enrichedExperience2.getInput()).isEqualTo(new DummyPolicyInput(List.of("o1", "o2")));
    }

    @Test
    public void givenTwoAgents_whenExecute_thenCreateJointActionInAgentOrder() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);

        Experience experience1 = new DefaultExperience(new DummyPolicyInput(List.of("o1")), action1, new RewardStandard(10));
        Experience experience2 = new DefaultExperience(new DummyPolicyInput(List.of("o2")), action2, new RewardStandard(20));

        RecordingCritic critic1 = new RecordingCritic();
        RecordingCritic critic2 = new RecordingCritic();

        DummyAgent agent1 = new DummyAgent(experience1, new DummyActorCritic(critic1));
        DummyAgent agent2 = new DummyAgent(experience2, new DummyActorCritic(critic2));

        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of(agent1, agent2));

        // When
        activator.execute();

        // Then
        JointAction jointAction1 = (JointAction) critic1.enrichmentCalls.get(0).enrichedExperience().getAction();
        JointAction jointAction2 = (JointAction) critic2.enrichmentCalls.get(0).enrichedExperience().getAction();

        assertThat(jointAction1.getActions()).containsExactly(action1, action2);
        assertThat(jointAction2.getActions()).containsExactly(action1, action2);
    }

    @Test
    public void givenTwoAgentsWithDifferentRewards_whenExecute_thenEachEnrichedExperienceKeepsOriginalReward() {
        // Given
        Experience experience1 = new DefaultExperience(
                new DummyPolicyInput(List.of("o1")),
                new ActionInt(1),
                new RewardStandard(10)
        );
        Experience experience2 = new DefaultExperience(
                new DummyPolicyInput(List.of("o2")),
                new ActionInt(2),
                new RewardStandard(20)
        );

        RecordingCritic critic1 = new RecordingCritic();
        RecordingCritic critic2 = new RecordingCritic();

        DummyAgent agent1 = new DummyAgent(experience1, new DummyActorCritic(critic1));
        DummyAgent agent2 = new DummyAgent(experience2, new DummyActorCritic(critic2));

        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of(agent1, agent2));

        // When
        activator.execute();

        // Then
        Experience enrichedExperience1 = critic1.enrichmentCalls.get(0).enrichedExperience();
        Experience enrichedExperience2 = critic2.enrichmentCalls.get(0).enrichedExperience();

        assertThat(enrichedExperience1.getReward()).isSameAs(experience1.getReward());
        assertThat(enrichedExperience2.getReward()).isSameAs(experience2.getReward());
    }

    @Test
    public void givenTwoAgents_whenExecute_thenFeedbackOriginalExperienceToEachAgent() {
        // Given
        Experience experience1 = new DefaultExperience(
                new DummyPolicyInput(List.of("o1")),
                new ActionInt(1),
                new RewardStandard(10)
        );
        Experience experience2 = new DefaultExperience(
                new DummyPolicyInput(List.of("o2")),
                new ActionInt(2),
                new RewardStandard(20)
        );

        DummyAgent agent1 = new DummyAgent(experience1, new DummyActorCritic(new RecordingCritic()));
        DummyAgent agent2 = new DummyAgent(experience2, new DummyActorCritic(new RecordingCritic()));

        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of(agent1, agent2));

        // When
        activator.execute();

        // Then
        assertThat(agent1.feedbackExperiences).containsExactly(experience1);
        assertThat(agent2.feedbackExperiences).containsExactly(experience2);
    }

    @Test
    public void givenTwoAgents_whenExecute_thenAssociateOriginalAndEnrichedExperiencesWithEachCritic() {
        // Given
        Experience experience1 = new DefaultExperience(
                new DummyPolicyInput(List.of("o1")),
                new ActionInt(1),
                new RewardStandard(10)
        );
        Experience experience2 = new DefaultExperience(
                new DummyPolicyInput(List.of("o2")),
                new ActionInt(2),
                new RewardStandard(20)
        );

        RecordingCritic critic1 = new RecordingCritic();
        RecordingCritic critic2 = new RecordingCritic();

        DummyAgent agent1 = new DummyAgent(experience1, new DummyActorCritic(critic1));
        DummyAgent agent2 = new DummyAgent(experience2, new DummyActorCritic(critic2));

        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of(agent1, agent2));

        // When
        activator.execute();

        // Then
        assertThat(critic1.enrichmentCalls).hasSize(1);
        assertThat(critic1.enrichmentCalls.get(0).originalExperience()).isSameAs(experience1);

        assertThat(critic2.enrichmentCalls).hasSize(1);
        assertThat(critic2.enrichmentCalls.get(0).originalExperience()).isSameAs(experience2);
    }

    @Test
    public void givenAgentWithoutActorCriticAlgorithm_whenExecute_thenThrowIllegalStateException() {
        // Given
        Experience experience = new DefaultExperience(
                new DummyPolicyInput(List.of("o1")),
                new ActionInt(1),
                new RewardStandard(10)
        );

        DummyAgent agent = new DummyAgent(experience, new DummyAlgorithm());

        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of(agent));

        // When / Then
        assertThatThrownBy(activator::execute)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires agents with ActorCritic algorithms");
    }

    @Test
    public void givenOneAgentWithoutExperience_whenExecute_thenCentralizeOnlyAvailableExperiences() {
        // Given
        Experience experience = new DefaultExperience(
                new DummyPolicyInput(List.of("o1")),
                new ActionInt(1),
                new RewardStandard(10)
        );

        RecordingCritic critic1 = new RecordingCritic();
        RecordingCritic critic2 = new RecordingCritic();

        DummyAgent agent1 = new DummyAgent(experience, new DummyActorCritic(critic1));
        DummyAgent agent2 = new DummyAgent(null, new DummyActorCritic(critic2));

        TestableCentralizedCriticCollectExperienceActivator activator =
                new TestableCentralizedCriticCollectExperienceActivator(List.of(agent1, agent2));

        // When
        activator.execute();

        // Then
        assertThat(agent1.feedbackExperiences).containsExactly(experience);
        assertThat(agent2.feedbackExperiences).isEmpty();
        assertThat(critic1.enrichmentCalls).hasSize(1);
        assertThat(critic2.enrichmentCalls).isEmpty();
    }

    private static final class TestableCentralizedCriticCollectExperienceActivator
            extends CentralizedCriticCollectExperienceActivator {

        private final List<MLKAgent> agents;
        private int collectExperiencesCallCount;

        private TestableCentralizedCriticCollectExperienceActivator(List<MLKAgent> agents) {
            super("test-group", "test-role");
            this.agents = agents;
        }

        @Override
        protected List<MLKAgent> collectAliveAgents() {
            return agents;
        }

        @Override
        protected Map<MLKAgent, Experience> collectExperiencesByAgent(List<MLKAgent> agents) {
            collectExperiencesCallCount++;
            return super.collectExperiencesByAgent(agents);
        }
    }

    private static final class DummyPolicyInput implements PolicyInput {

        private final List<String> values;

        private DummyPolicyInput(List<String> values) {
            this.values = List.copyOf(values);
        }

        @Override
        public PolicyInput add(PolicyInput other) {
            DummyPolicyInput otherInput = (DummyPolicyInput) other;
            List<String> mergedValues = new ArrayList<>(values);
            mergedValues.addAll(otherInput.values);
            return new DummyPolicyInput(mergedValues);
        }

        @Override
        public PolicyInput copy() {
            return new DummyPolicyInput(values);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof DummyPolicyInput other && values.equals(other.values));
        }

        @Override
        public int hashCode() {
            return values.hashCode();
        }
    }

    private static final class RecordingCritic implements Critic {

        private final List<EnrichmentCall> enrichmentCalls = new ArrayList<>();

        @Override
        public void init(MLKAgent agent) {
        	// No-op for testing
        }

        @Override
        public void enrichExperience(Experience originalExperience, Experience enrichedExperience) {
            enrichmentCalls.add(new EnrichmentCall(originalExperience, enrichedExperience));
        }

        @Override
        public Experience getEnrichedExperience(Experience originalExperience) {
            return enrichmentCalls.stream()
                    .filter(call -> call.originalExperience() == originalExperience)
                    .map(EnrichmentCall::enrichedExperience)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void removeEnrichedExperience(Experience originalExperience) {
            enrichmentCalls.removeIf(call -> call.originalExperience() == originalExperience);
        }

        @Override
        public void clearEnrichedExperiences() {
            enrichmentCalls.clear();
        }
    }

    private record EnrichmentCall(Experience originalExperience, Experience enrichedExperience) {
    }

    private static final class DummyActorCritic implements ActorCritic {

        private final Critic critic;
        private MLKAgent agent;

        private DummyActorCritic(Critic critic) {
            this.critic = critic;
        }

        @Override
        public CategoricalPolicyGradient getActor() {
            return null;
        }

        @Override
        public Critic getCritic() {
            return critic;
        }

        @Override
        public void setPolicy(Policy policy) {
        }

        @Override
        public Policy getPolicy() {
            return null;
        }

        @Override
        public void setAgent(MLKAgent agent) {
            this.agent = agent;
        }

        @Override
        public MLKAgent getAgent() {
            return agent;
        }

        @Override
        public int getLearningFrequency() {
            return 1;
        }

        @Override
        public void learnOnBatch(Batch batch, AgentLogger logger) {
        }

        @Override
        public void endEpisode(Batch batch, AgentLogger logger) {
        }
    }

    private static final class DummyAlgorithm implements Algorithm {

        private MLKAgent agent;

        @Override
        public void init(MLKAgent agent) {
            this.agent = agent;
        }

        @Override
        public void setPolicy(Policy policy) {
        }

        @Override
        public Policy getPolicy() {
            return null;
        }

        @Override
        public void setAgent(MLKAgent agent) {
            this.agent = agent;
        }

        @Override
        public MLKAgent getAgent() {
            return agent;
        }

        @Override
        public int getLearningFrequency() {
            return 1;
        }

        @Override
        public void learnOnBatch(Batch batch, AgentLogger logger) {
        }

        @Override
        public void endEpisode(Batch batch, AgentLogger logger) {
        }
    }

    private static final class DummyAgent implements MLKAgent {

        private final Experience environmentExperience;
        private final Algorithm algorithm;
        private final List<Experience> feedbackExperiences;

        private DummyAgent(Experience environmentExperience, Algorithm algorithm) {
            this.environmentExperience = environmentExperience;
            this.algorithm = algorithm;
            this.feedbackExperiences = new ArrayList<>();
        }

        @Override
        public Algorithm getAlgorithm() {
            return algorithm;
        }

        @Override
        public Experience getEnvExperience() {
            return environmentExperience;
        }

        @Override
        public void feedbackExperience(Experience experience) {
            feedbackExperiences.add(experience);
        }

        @Override
        public Policy getPolicy() {
            return null;
        }

        @Override
        public void setPolicy(Policy policy) {
        }

        @Override
        public void setAlgorithm(Algorithm algorithm) {
        }

        @Override
        public Observation getRegisteredObservation() {
            return null;
        }

        @Override
        public void setRegisteredObservation(Observation registeredObservation) {
        }

        @Override
        public RandomGenerator prng() {
            return new java.util.Random(0);
        }

        @Override
        public void notifySelfToEnvironment() {
        }

        @Override
        public void initializeAll() {
        }

        @Override
        public void feedbackExperience(PolicyInput input, Action action, reward.Reward reward) {
        }

        @Override
        public Action selectAction(PolicyInput input) {
            return null;
        }

        @Override
        public void takeAction() {
        }

        @Override
        public void collectExperience() {
        }

        @Override
        public MLKEnvironment getMLKEnvironment() {
            return null;
        }

        @Override
        public void updatePolicy(int timestep) {
        }

        @Override
        public void learnOnBatch() {
        }

        @Override
        public void endEpisode() {
        }

        @Override
        public SimuAgent getSimuAgent() {
            return null;
        }
    }
}