package algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.ActionInt;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.Batch;
import learning.Experience;
import learning.nn.ActorNetwork;
import learning.nn.StateValueCritic;
import learning.policy.Policy;
import learning.policy.PolicyInput;
import reward.RewardStandard;

public class TDActorCriticTest {

    @Test
    public void givenNonActorNetworkPolicy_whenSetPolicy_thenThrowIllegalArgumentException() {
        // Given
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor, critic);
        Policy invalidPolicy = new DummyPolicy();

        // When
        TDActorCritic target = algorithm;

        // Then
        assertThatThrownBy(() -> target.setPolicy(invalidPolicy)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenAgentIsSet_whenSetPolicyWithActorNetwork_thenInitNewActor() {
        // Given
        RecordingActorNetwork actor1 = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor1, critic);
        MLKAgent agent = agentWithPrng(new java.util.Random(0));
        algorithm.setAgent(agent);

        RecordingActorNetwork actor2 = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));

        // When
        algorithm.setPolicy(actor2);

        // Then
        assertThat(actor2.initCallCount).isEqualTo(1);
        assertThat(actor2.lastInitAgent).isSameAs(agent);
    }

    @Test
    public void givenAgentIsNull_whenSetPolicyWithActorNetwork_thenDoNotInitActor() {
        // Given
        RecordingActorNetwork actor1 = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor1, critic);

        RecordingActorNetwork actor2 = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));

        // When
        algorithm.setPolicy(actor2);

        // Then
        assertThat(actor2.initCallCount).isZero();
    }

    @Test
    public void givenBatchWithThreeExperiences_whenLearnOnBatch_thenConsumeUntilOneAndUpdateActorTwice() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(action1, action2));
        actor.fixedProbs = new double[] { 0.25, 0.75 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(2.0);
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        DummyPolicyInput input1 = new DummyPolicyInput("s1");
        DummyPolicyInput input2 = new DummyPolicyInput("s2");
        DummyPolicyInput input3 = new DummyPolicyInput("s3");

        Experience exp1 = new Experience(input1, action2, new RewardStandard(5));
        Experience exp2 = new Experience(input2, action2, new RewardStandard(6));
        Experience exp3 = new Experience(input3, action2, new RewardStandard(7));

        Batch batch = new Batch(List.of(exp1, exp2, exp3));

        // When
        algorithm.learnOnBatch(batch, null);

        // Then
        assertThat(batch.getExperiences()).containsExactly(exp3);
        assertThat(actor.updateCalls).hasSize(2);
        assertThat(actor.updateCalls.get(0).learningRate()).isEqualTo(0.1);
        assertThat(actor.updateCalls.get(0).gradient()).containsExactly(0.5, -0.5);
        assertThat(actor.updateCalls.get(1).learningRate()).isEqualTo(0.1);
        assertThat(actor.updateCalls.get(1).gradient()).containsExactly(0.25, -0.25);
        assertThat(critic.updateCalls).hasSize(2);
        assertThat(critic.removedEnriched).containsExactly(exp1, exp2);
    }

    @Test
    public void givenCriticProvidesEnrichedExperiences_whenLearnOnBatch_thenUseEnrichedInputsInUpdate() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(action1, action2));
        actor.fixedProbs = new double[] { 0.5, 0.5 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        DummyPolicyInput original1 = new DummyPolicyInput("o1");
        DummyPolicyInput original2 = new DummyPolicyInput("o2");
        DummyPolicyInput enriched1 = new DummyPolicyInput("e1");
        DummyPolicyInput enriched2 = new DummyPolicyInput("e2");

        Experience exp1 = new Experience(original1, action2, new RewardStandard(5));
        Experience exp2 = new Experience(original2, action2, new RewardStandard(6));
        Batch batch = new Batch(List.of(exp1, exp2));

        critic.enrichedExperience1 = new Experience(enriched1, action2, new RewardStandard(5));
        critic.enrichedExperience2 = new Experience(enriched2, action2, new RewardStandard(6));

        // When
        algorithm.learnOnBatch(batch, null);

        // Then
        assertThat(critic.updateCalls.get(0).observation()).isSameAs(enriched1);
        assertThat(critic.updateCalls.get(0).nextObservation()).isSameAs(enriched2);
    }

    @Test
    public void givenSelectedActionNotInActorActionSet_whenLearnOnBatch_thenThrowIllegalArgumentException() {
        // Given
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        actor.fixedProbs = new double[] { 0.5, 0.5 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        Action notInSet = new ActionInt(999);
        Experience exp1 = new Experience(new DummyPolicyInput("s1"), notInSet, new RewardStandard(5));
        Experience exp2 = new Experience(new DummyPolicyInput("s2"), notInSet, new RewardStandard(6));
        Batch batch = new Batch(List.of(exp1, exp2));

        // When
        TDActorCritic target = algorithm;

        // Then
        assertThatThrownBy(() -> target.learnOnBatch(batch, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenBatchWithSingleExperience_whenEndEpisode_thenUpdateWithTerminalTrueAndClearBatch() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(action1, action2));
        actor.fixedProbs = new double[] { 0.25, 0.75 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(2.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        Experience exp1 = new Experience(new DummyPolicyInput("s1"), action2, new RewardStandard(5));
        Batch batch = new Batch(List.of(exp1));

        // When
        algorithm.endEpisode(batch, null);

        // Then
        assertThat(batch.getExperiences()).isEmpty();
        assertThat(critic.updateCalls).hasSize(1);
        assertThat(critic.updateCalls.get(0).terminal()).isTrue();
        assertThat(critic.updateCalls.get(0).nextObservation()).isNull();
        assertThat(actor.updateCalls).hasSize(1);
        assertThat(critic.clearCallCount).isEqualTo(1);
    }

    @Test
    public void givenEmptyBatch_whenEndEpisode_thenOnlyClearCriticAndBatch() {
        // Given
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);
        Batch batch = new Batch();

        // When
        algorithm.endEpisode(batch, null);

        // Then
        assertThat(actor.updateCalls).isEmpty();
        assertThat(critic.updateCalls).isEmpty();
        assertThat(critic.clearCallCount).isEqualTo(1);
        assertThat(batch.getExperiences()).isEmpty();
    }

    @Test
    public void givenTwoAlgorithms_whenLearnOnOne_thenOtherActorIsNotUpdated() {
        // Given
        RecordingActorNetwork actor1 = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        actor1.fixedProbs = new double[] { 0.25, 0.75 };
        RecordingStateValueCritic critic1 = new RecordingStateValueCritic();
        critic1.tdErrors.addLast(1.0);
        TDActorCritic algorithm1 = new TDActorCritic(actor1, critic1, 0.1, 0.2, 0.9);

        RecordingActorNetwork actor2 = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic2 = new RecordingStateValueCritic();
        TDActorCritic algorithm2 = new TDActorCritic(actor2, critic2, 0.1, 0.2, 0.9);

        Experience exp1 = new Experience(new DummyPolicyInput("s1"), new ActionInt(2), new RewardStandard(5));
        Experience exp2 = new Experience(new DummyPolicyInput("s2"), new ActionInt(2), new RewardStandard(6));
        Batch batch = new Batch(List.of(exp1, exp2));

        // When
        algorithm1.learnOnBatch(batch, null);

        // Then
        assertThat(actor1.updateCalls).hasSize(1);
        assertThat(actor2.updateCalls).isEmpty();
        assertThat(algorithm2.getActor()).isSameAs(actor2);
    }
    
    @Test
    public void givenAlgorithm_whenInit_thenSetAgentAndInitActorAndCritic() {
        // Given
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor, critic);
        MLKAgent agent = agentWithPrng(new java.util.Random(0));

        // When
        algorithm.init(agent);

        // Then
        assertThat(algorithm.getAgent()).isSameAs(agent);
        assertThat(actor.initCallCount).isEqualTo(1);
        assertThat(actor.lastInitAgent).isSameAs(agent);
    }
    
    @Test
    public void givenTransition_whenLearnOnBatch_thenPassCorrectParametersToCritic() {
        // Given
        ActionInt action = new ActionInt(2);
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(new ActionInt(1), action));
        actor.fixedProbs = new double[] { 0.5, 0.5 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        DummyPolicyInput input1 = new DummyPolicyInput("s1");
        DummyPolicyInput input2 = new DummyPolicyInput("s2");

        Experience exp1 = new Experience(input1, action, new RewardStandard(5));
        Experience exp2 = new Experience(input2, action, new RewardStandard(6));
        Batch batch = new Batch(List.of(exp1, exp2));

        // When
        algorithm.learnOnBatch(batch, null);

        // Then
        CriticUpdateCall call = critic.updateCalls.get(0);
        assertThat(call.observation()).isSameAs(input1);
        assertThat(call.nextObservation()).isSameAs(input2);
        assertThat(call.reward()).isEqualTo(5.0);
        assertThat(call.terminal()).isFalse();
        assertThat(call.gamma()).isEqualTo(0.9);
        assertThat(call.learningRate()).isEqualTo(0.2);
    }
    
    @Test
    public void givenBatchWithOneExperience_whenLearnOnBatch_thenDoNothing() {
        // Given
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor, critic);

        Batch batch = new Batch(List.of(
                new Experience(new DummyPolicyInput("s1"), new ActionInt(1), new RewardStandard(5))
        ));

        // When
        algorithm.learnOnBatch(batch, null);

        // Then
        assertThat(batch.getExperiences()).hasSize(1);
        assertThat(actor.updateCalls).isEmpty();
        assertThat(critic.updateCalls).isEmpty();
    }
    
    @Test
    public void givenCriticProvidesEnrichedExperience_whenEndEpisode_thenUseEnrichedInputInTerminalUpdate() {
        // Given
        ActionInt action = new ActionInt(2);
        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(new ActionInt(1), action));
        actor.fixedProbs = new double[] { 0.5, 0.5 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        DummyPolicyInput original = new DummyPolicyInput("original");
        DummyPolicyInput enriched = new DummyPolicyInput("enriched");

        Experience exp = new Experience(original, action, new RewardStandard(5));
        critic.enrichedExperience1 = new Experience(enriched, action, new RewardStandard(5));

        Batch batch = new Batch(List.of(exp));

        // When
        algorithm.endEpisode(batch, null);

        // Then
        assertThat(critic.updateCalls.get(0).observation()).isSameAs(enriched);
    }
    
    @Test
    public void givenNegativeTdError_whenLearnOnBatch_thenActorGradientHasCorrectSign() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);

        RecordingActorNetwork actor = new RecordingActorNetwork(List.of(action1, action2));
        actor.fixedProbs = new double[] { 0.25, 0.75 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(-2.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        Batch batch = new Batch(List.of(
                new Experience(new DummyPolicyInput("s1"), action2, new RewardStandard(5)),
                new Experience(new DummyPolicyInput("s2"), action2, new RewardStandard(6))
        ));

        // When
        algorithm.learnOnBatch(batch, null);

        // Then
        assertThat(actor.updateCalls.get(0).gradient()).containsExactly(-0.5, 0.5);
    }

    private static MLKAgent agentWithPrng(RandomGenerator prng) {
        return (MLKAgent) Proxy.newProxyInstance(
                MLKAgent.class.getClassLoader(),
                new Class<?>[] { MLKAgent.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("prng")) {
                        return prng;
                    }
                    Class<?> returnType = method.getReturnType();
                    if (returnType.equals(boolean.class)) {
                        return false;
                    }
                    if (returnType.equals(int.class)) {
                        return 0;
                    }
                    if (returnType.equals(double.class)) {
                        return 0.0;
                    }
                    if (returnType.equals(long.class)) {
                        return 0L;
                    }
                    return null;
                });
    }

    private static final class DummyPolicy implements Policy {

        @Override
        public void init(MLKAgent agent) {
        }

        @Override
        public MLKAgent getAgent() {
            return null;
        }

        @Override
        public Action selectAction(PolicyInput input) {
            return null;
        }
    }

    private static final class DummyWrapper implements WrapperPolicyInputVector {

        @Override
        public double[] transform(PolicyInput observation) {
            return new double[] { 0.0 };
        }

        @Override
        public PolicyInput transform(double[] vector) {
            return new DummyPolicyInput("v");
        }
    }

    private static final class RecordingActorNetwork extends ActorNetwork {

        private final List<Action> actionSet;
        private int initCallCount;
        private MLKAgent lastInitAgent;
        private double[] fixedProbs;
        private final List<ActorUpdateCall> updateCalls;

        private RecordingActorNetwork(List<Action> actionSet) {
            super(1, 1, new DummyWrapper(), actionSet);
            this.actionSet = actionSet;
            this.updateCalls = new java.util.ArrayList<>();
        }

        @Override
        public void init(MLKAgent agent) {
            initCallCount++;
            lastInitAgent = agent;
        }

        @Override
        public double[] forwardLogits(PolicyInput input) {
            return new double[actionSet.size()];
        }

        @Override
        public double[] softmax(double[] logits) {
            return fixedProbs;
        }

        @Override
        public void updateFromLogitsGradient(PolicyInput input, double[] dLossDLogits, double learningRate) {
            updateCalls.add(new ActorUpdateCall(input, dLossDLogits, learningRate));
        }

        @Override
        public List<Action> getActionSet() {
            return actionSet;
        }
    }

    private record ActorUpdateCall(PolicyInput input, double[] gradient, double learningRate) {
    }

    private static final class RecordingStateValueCritic extends StateValueCritic {

        private final Deque<Double> tdErrors;
        private final List<CriticUpdateCall> updateCalls;
        private final List<Experience> removedEnriched;
        private Experience enrichedExperience1;
        private Experience enrichedExperience2;
        private int clearCallCount;

        private RecordingStateValueCritic() {
            super(1, 1, new DummyWrapper());
            this.tdErrors = new ArrayDeque<>();
            this.updateCalls = new java.util.ArrayList<>();
            this.removedEnriched = new java.util.ArrayList<>();
        }

        @Override
        public void init(MLKAgent agent) {
        }

        @Override
        public Experience getEnrichedExperience(Experience originalExperience) {
            if (enrichedExperience1 != null && enrichedExperience1.getRewardValue().equals(originalExperience.getRewardValue())) {
                return enrichedExperience1;
            }
            if (enrichedExperience2 != null && enrichedExperience2.getRewardValue().equals(originalExperience.getRewardValue())) {
                return enrichedExperience2;
            }
            return null;
        }

        @Override
        public void removeEnrichedExperience(Experience originalExperience) {
            removedEnriched.add(originalExperience);
        }

        @Override
        public void clearEnrichedExperiences() {
            clearCallCount++;
        }

        @Override
        public double updateFromTransition(PolicyInput observation, double reward, PolicyInput nextObservation, boolean terminal, double gamma, double learningRate) {
            updateCalls.add(new CriticUpdateCall(observation, reward, nextObservation, terminal, gamma, learningRate));
            return tdErrors.removeFirst();
        }
    }

    private record CriticUpdateCall(PolicyInput observation, double reward, PolicyInput nextObservation, boolean terminal, double gamma,
            double learningRate) {
    }

    private static final class DummyPolicyInput implements PolicyInput {

        private final String id;

        private DummyPolicyInput(String id) {
            this.id = id;
        }

        @Override
        public PolicyInput add(PolicyInput other) {
            return this;
        }

        @Override
        public PolicyInput copy() {
            return new DummyPolicyInput(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof DummyPolicyInput other)) {
                return false;
            }
            return id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
