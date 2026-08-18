package algorithm;

import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.ActionInt;
import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;
import experience.DefaultExperience;
import experience.Experience;
import learning.Batch;
import learning.Policy;
import learning.nn.StateValueCritic;
import learning.policies.CategoricalPolicyGradient;
import reward.RewardStandard;

public class TDActorCriticTest {

    @Test
    public void givenNonActorNetworkPolicy_whenSetPolicy_thenThrowIllegalArgumentException() {
        // Given
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));
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
        RecordingPolicyGradientPolicy actor1 = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor1, critic);
        MLKAgent agent = agentWithPrng(new java.util.Random(0));
        algorithm.setAgent(agent);

        RecordingPolicyGradientPolicy actor2 = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));

        // When
        algorithm.setPolicy(actor2);

        // Then
        assertThat(actor2.initCallCount).isEqualTo(1);
        assertThat(actor2.lastInitAgent).isSameAs(agent);
    }

    @Test
    public void givenAgentIsNull_whenSetPolicyWithActorNetwork_thenDoNotInitActor() {
        // Given
        RecordingPolicyGradientPolicy actor1 = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor1, critic);

        RecordingPolicyGradientPolicy actor2 = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));

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
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(action1, action2));
        actor.fixedProbs = new double[] { 0.25, 0.75 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(2.0);
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        DummyObservation input1 = new DummyObservation("s1");
        DummyObservation input2 = new DummyObservation("s2");
        DummyObservation input3 = new DummyObservation("s3");

        Experience exp1 = new DefaultExperience(input1, action2, new RewardStandard(5));
        Experience exp2 = new DefaultExperience(input2, action2, new RewardStandard(6));
        Experience exp3 = new DefaultExperience(input3, action2, new RewardStandard(7));

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
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(action1, action2));
        actor.fixedProbs = new double[] { 0.5, 0.5 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        DummyObservation original1 = new DummyObservation("o1");
        DummyObservation original2 = new DummyObservation("o2");
        DummyObservation enriched1 = new DummyObservation("e1");
        DummyObservation enriched2 = new DummyObservation("e2");

        Experience exp1 = new DefaultExperience(original1, action2, new RewardStandard(5));
        Experience exp2 = new DefaultExperience(original2, action2, new RewardStandard(6));
        Batch batch = new Batch(List.of(exp1, exp2));

        critic.enrichedExperience1 = new DefaultExperience(enriched1, action2, new RewardStandard(5));
        critic.enrichedExperience2 = new DefaultExperience(enriched2, action2, new RewardStandard(6));

        // When
        algorithm.learnOnBatch(batch, null);

        // Then
        assertThat(critic.updateCalls.get(0).observation()).isSameAs(enriched1);
        assertThat(critic.updateCalls.get(0).nextObservation()).isSameAs(enriched2);
    }

    @Test
    public void givenSelectedActionNotInActorActionSet_whenLearnOnBatch_thenThrowIllegalArgumentException() {
        // Given
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));
        actor.fixedProbs = new double[] { 0.5, 0.5 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        Action notInSet = new ActionInt(999);
        Experience exp1 = new DefaultExperience(new DummyObservation("s1"), notInSet, new RewardStandard(5));
        Experience exp2 = new DefaultExperience(new DummyObservation("s2"), notInSet, new RewardStandard(6));
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
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(action1, action2));
        actor.fixedProbs = new double[] { 0.25, 0.75 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(2.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        Experience exp1 = new DefaultExperience(new DummyObservation("s1"), action2, new RewardStandard(5));
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
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));
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
        RecordingPolicyGradientPolicy actor1 = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));
        actor1.fixedProbs = new double[] { 0.25, 0.75 };
        RecordingStateValueCritic critic1 = new RecordingStateValueCritic();
        critic1.tdErrors.addLast(1.0);
        TDActorCritic algorithm1 = new TDActorCritic(actor1, critic1, 0.1, 0.2, 0.9);

        RecordingPolicyGradientPolicy actor2 = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic2 = new RecordingStateValueCritic();
        TDActorCritic algorithm2 = new TDActorCritic(actor2, critic2, 0.1, 0.2, 0.9);

        Experience exp1 = new DefaultExperience(new DummyObservation("s1"), new ActionInt(2), new RewardStandard(5));
        Experience exp2 = new DefaultExperience(new DummyObservation("s2"), new ActionInt(2), new RewardStandard(6));
        Batch batch = new Batch(List.of(exp1, exp2));

        // When
        algorithm1.learnOnBatch(batch, null);

        // Then
        assertThat(actor1.updateCalls).hasSize(1);
        assertThat(actor2.updateCalls).isEmpty();
        assertThat(algorithm2.getActor()).isSameAs(actor2);
    }

    
    @Test
    public void givenTransition_whenLearnOnBatch_thenPassCorrectParametersToCritic() {
        // Given
        ActionInt action = new ActionInt(2);
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), action));
        actor.fixedProbs = new double[] { 0.5, 0.5 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        DummyObservation input1 = new DummyObservation("s1");
        DummyObservation input2 = new DummyObservation("s2");

        Experience exp1 = new DefaultExperience(input1, action, new RewardStandard(5));
        Experience exp2 = new DefaultExperience(input2, action, new RewardStandard(6));
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
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), new ActionInt(2)));
        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        TDActorCritic algorithm = new TDActorCritic(actor, critic);

        Batch batch = new Batch(List.of(
                new DefaultExperience(new DummyObservation("s1"), new ActionInt(1), new RewardStandard(5))
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
        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(new ActionInt(1), action));
        actor.fixedProbs = new double[] { 0.5, 0.5 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(1.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        DummyObservation original = new DummyObservation("original");
        DummyObservation enriched = new DummyObservation("enriched");

        Experience exp = new DefaultExperience(original, action, new RewardStandard(5));
        critic.enrichedExperience1 = new DefaultExperience(enriched, action, new RewardStandard(5));

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

        RecordingPolicyGradientPolicy actor = new RecordingPolicyGradientPolicy(List.of(action1, action2));
        actor.fixedProbs = new double[] { 0.25, 0.75 };

        RecordingStateValueCritic critic = new RecordingStateValueCritic();
        critic.tdErrors.addLast(-2.0);

        TDActorCritic algorithm = new TDActorCritic(actor, critic, 0.1, 0.2, 0.9);

        Batch batch = new Batch(List.of(
                new DefaultExperience(new DummyObservation("s1"), action2, new RewardStandard(5)),
                new DefaultExperience(new DummyObservation("s2"), action2, new RewardStandard(6))
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
        public Action selectAction(Observation input) {
            return null;
        }
    }

    private static final class DummyWrapper implements WrapperObservationVector {

        @Override
        public double[] transform(Observation observation) {
            return new double[] { 0.0 };
        }

        @Override
        public Observation transform(double[] vector) {
            return new DummyObservation("v");
        }
    }

    private static final class RecordingPolicyGradientPolicy implements CategoricalPolicyGradient {

        private final List<Action> actionSet;
        private int initCallCount;
        private MLKAgent lastInitAgent;
        private double[] fixedProbs;
        private final List<ActorUpdateCall> updateCalls;

        private RecordingPolicyGradientPolicy(List<Action> actionSet) {
            this.actionSet = actionSet;
            this.updateCalls = new ArrayList<>();
        }

        @Override
        public void init(MLKAgent agent) {
            initCallCount++;
            lastInitAgent = agent;
        }

        @Override
        public MLKAgent getAgent() {
            return lastInitAgent;
        }

        @Override
        public Action selectAction(Observation input) {
            return null;
        }

        @Override
        public double[] forwardLogits(Observation input) {
            return new double[actionSet.size()];
        }

        @Override
        public double[][] forwardLogits(Observation[] inputs) {
            double[][] logits = new double[inputs.length][];

            for (int i = 0; i < inputs.length; i++) {
                logits[i] = forwardLogits(inputs[i]);
            }

            return logits;
        }

        @Override
        public double[] softmax(double[] logits) {
            return fixedProbs;
        }

        @Override
        public void updateFromLogitsGradient(Observation input, double[] dLossDLogits, double learningRate) {
            updateCalls.add(new ActorUpdateCall(input, dLossDLogits, learningRate));
        }

        @Override
        public void updateFromLogitsGradient(Observation[] inputs, double[][] dLossDLogits, double learningRate) {
            for (int i = 0; i < inputs.length; i++) {
                updateFromLogitsGradient(inputs[i], dLossDLogits[i], learningRate);
            }
        }

        @Override
        public int actionIndex(Action action) {
            int index = actionSet.indexOf(action);

            if (index < 0) {
                throw new IllegalArgumentException("Unknown action.");
            }

            return index;
        }

        @Override
        public double getSoftmaxTemperature() {
            return 1.0;
        }
    }
    
    private record ActorUpdateCall(Observation input, double[] gradient, double learningRate) {
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
        public double updateFromTransition(Observation observation, double reward, Observation nextObservation, boolean terminal, double gamma, double learningRate) {
            updateCalls.add(new CriticUpdateCall(observation, reward, nextObservation, terminal, gamma, learningRate));
            return tdErrors.removeFirst();
        }
    }

    private record CriticUpdateCall(Observation observation, double reward, Observation nextObservation, boolean terminal, double gamma,
            double learningRate) {
    }

    private static final class DummyObservation implements Observation {

        private final String id;

        private DummyObservation(String id) {
            this.id = id;
        }

        @Override
        public Observation add(Observation other) {
            return this;
        }

        @Override
        public Observation copy() {
            return new DummyObservation(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof DummyObservation other)) {
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
