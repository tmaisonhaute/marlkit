package algorithm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.ActionInt;
import agent.action.MappedJointAction;
import agent.modelofotheragent.GroupModelPredictAction;
import environment.MLKEnvironment;
import environment.observation.Observation;
import learning.Algorithm;
import learning.Experience;
import learning.ExplorationStrategy;
import learning.Policy;
import learning.policies.PolicyInput;
import madkit.simulation.SimuAgent;
import util.Pair;

public class QLearningJALTest {

    private static final MLKAgent SELF = new DummyAgent("self");
    private static final MLKAgent OTHER = new DummyAgent("other");

    private static MappedJointAction fixedPrediction() {
        MappedJointAction prediction = new MappedJointAction();
        prediction.addAction(OTHER, new ActionInt(99));
        return prediction;
    }

    @Test
    public void givenExplorationReturnsAction_whenSelectAction_thenReturnExploratoryActionAndDoNotCallModel() {
        // Given
        ActionInt exploratoryAction = new ActionInt(9);
        RecordingGroupModelPredictAction model = new RecordingGroupModelPredictAction(fixedPrediction());
        QLearningJAL policy = new TestableQLearningJAL(List.of(new ActionInt(1), new ActionInt(2)), model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.of(exploratoryAction)));
        policy.init(SELF);

        // When
        Action selected = policy.selectAction(new DummyPolicyInput());

        // Then
        assertThat(selected).isEqualTo(exploratoryAction);
        assertThat(model.predictCalls).isEmpty();
    }

    @Test
    public void givenNoExplorationAndHigherQValueForOneAction_whenSelectAction_thenReturnGreedyAction() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        DummyPolicyInput input = new DummyPolicyInput();

        RecordingGroupModelPredictAction model = new RecordingGroupModelPredictAction(fixedPrediction());
        QLearningJAL policy = new TestableQLearningJAL(List.of(action1, action2), model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        MappedJointAction joint1 = model.fixedPrediction.withAction(SELF, action1);
        MappedJointAction joint2 = model.fixedPrediction.withAction(SELF, action2);
        policy.getTable().setValue(new Pair<>(input, joint1), 1.0);
        policy.getTable().setValue(new Pair<>(input, joint2), 2.0);

        // When
        Action selected = policy.selectAction(input);

        // Then
        assertThat(selected).isEqualTo(action2);
        assertThat(model.predictCalls).containsExactly(
                new PredictCall(input, action1),
                new PredictCall(input, action2)
        );
    }

    @Test
    public void givenNullModelAndExplorationReturnsAction_whenSelectAction_thenReturnExploratoryAction() {
        // Given
        ActionInt exploratoryAction = new ActionInt(9);
        QLearningJAL policy = new TestableQLearningJAL(List.of(new ActionInt(1), new ActionInt(2)), null);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.of(exploratoryAction)));
        policy.init(SELF);

        // When
        Action selected = policy.selectAction(new DummyPolicyInput());

        // Then
        assertThat(selected).isEqualTo(exploratoryAction);
    }

    @Test
    public void givenEmptyActionSetAndNoExploration_whenSelectAction_thenReturnNull() {
        // Given
        RecordingGroupModelPredictAction model = new RecordingGroupModelPredictAction(fixedPrediction());
        QLearningJAL policy = new TestableQLearningJAL(List.of(), model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        // When
        Action selected = policy.selectAction(new DummyPolicyInput());

        // Then
        assertThat(selected).isNull();
        assertThat(model.predictCalls).isEmpty();
    }

    @Test
    public void givenTwoPolicies_whenMutateOneQTable_thenOtherPolicySelectionIsUnaffected() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        DummyPolicyInput input = new DummyPolicyInput();
        RecordingGroupModelPredictAction model = new RecordingGroupModelPredictAction(fixedPrediction());

        QLearningJAL policy1 = new TestableQLearningJAL(List.of(action1, action2), model);
        QLearningJAL policy2 = new TestableQLearningJAL(List.of(action1, action2), model);

        policy1.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy2.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy1.init(SELF);
        policy2.init(SELF);

        MappedJointAction jointForAction2 = model.fixedPrediction.withAction(SELF, action2);
        policy1.getTable().setValue(new Pair<>(input, jointForAction2), 10.0);

        // When
        Action selected1 = policy1.selectAction(input);
        Action selected2 = policy2.selectAction(input);

        // Then
        assertThat(selected1).isEqualTo(action2);
        assertThat(selected2).isEqualTo(action1);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void givenNullModelAndNoExploration_whenSelectAction_thenThrowException() {
        // Given
        QLearningJAL policy = new TestableQLearningJAL(
                List.of(new ActionInt(1), new ActionInt(2)),
                null
        );
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        // When / Then
        policy.selectAction(new DummyPolicyInput());
    }
    
    @Test
    public void givenEqualQValues_whenSelectAction_thenReturnFirstActionInList() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        DummyPolicyInput input = new DummyPolicyInput();

        RecordingGroupModelPredictAction model =
            new RecordingGroupModelPredictAction(fixedPrediction());

        QLearningJAL policy = new TestableQLearningJAL(List.of(action1, action2), model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        MappedJointAction joint1 = model.fixedPrediction.withAction(SELF, action1);
        MappedJointAction joint2 = model.fixedPrediction.withAction(SELF, action2);

        policy.getTable().setValue(new Pair<>(input, joint1), 5.0);
        policy.getTable().setValue(new Pair<>(input, joint2), 5.0);

        // When
        Action selected = policy.selectAction(input);

        // Then
        assertThat(selected).isEqualTo(action1);
    }
    
    @Test
    public void givenNoQValuesDefined_whenSelectAction_thenReturnFirstAction() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);

        RecordingGroupModelPredictAction model =
            new RecordingGroupModelPredictAction(fixedPrediction());

        QLearningJAL policy = new TestableQLearningJAL(List.of(action1, action2), model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        // When
        Action selected = policy.selectAction(new DummyPolicyInput());

        // Then
        assertThat(selected).isEqualTo(action1);
    }
    
    @Test
    public void givenPredictedJointAction_whenSelectAction_thenQTableIsQueriedWithAugmentedJointAction() {
        // Given
        ActionInt ownAction = new ActionInt(1);
        ActionInt otherAction = new ActionInt(42);
        DummyPolicyInput input = new DummyPolicyInput();

        MappedJointAction predicted = new MappedJointAction();
        predicted.addAction(OTHER, otherAction);
        RecordingGroupModelPredictAction model =
                new RecordingGroupModelPredictAction(predicted);

        QLearningJAL policy = new TestableQLearningJAL(List.of(ownAction), model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        MappedJointAction expectedJoint = predicted.withAction(SELF, ownAction);
        policy.getTable().setValue(new Pair<>(input, expectedJoint), 7.0);

        // When
        Action selected = policy.selectAction(input);

        // Then
        assertThat(selected).isEqualTo(ownAction);
    }

    @Test
    public void givenPredictingAgent_whenSelectAction_thenSelfActionIsAddedWithAgentKey() {
        // Given
        ActionInt ownAction = new ActionInt(5);
        ActionInt otherAction = new ActionInt(8);
        DummyPolicyInput input = new DummyPolicyInput();

        MappedJointAction predicted = new MappedJointAction();
        predicted.addAction(OTHER, otherAction);
        RecordingGroupModelPredictAction model =
                new RecordingGroupModelPredictAction(predicted);

        QLearningJAL policy = new TestableQLearningJAL(List.of(ownAction), model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        MappedJointAction expected = predicted.withAction(SELF, ownAction);
        policy.getTable().setValue(new Pair<>(input, expected), 3.0);

        // When
        Action selected = policy.selectAction(input);

        // Then
        assertThat(selected).isEqualTo(ownAction);
        assertThat(model.predictCalls).containsExactly(
                new PredictCall(input, ownAction)
        );
    }
    
    @Test
    public void givenMutableActionsList_whenListIsModifiedAfterConstruction_thenPolicyBehaviorChanges() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        List<Action> actions = new ArrayList<>();
        actions.add(action1);

        RecordingGroupModelPredictAction model =
            new RecordingGroupModelPredictAction(fixedPrediction());

        QLearningJAL policy = new TestableQLearningJAL(actions, model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        actions.add(action2);

        // When
        policy.selectAction(new DummyPolicyInput());

        // Then
        assertThat(model.predictCalls).extracting(PredictCall::action)
                .containsExactly(action1, action2);
    }

    @Test
    public void givenSamePredictedJointActionReturnedEachTime_whenSelecting_thenActionsDoNotPolluteEachOther() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        DummyPolicyInput input = new DummyPolicyInput();

        MappedJointAction sharedPrediction = fixedPrediction();
        RecordingGroupModelPredictAction model =
                new RecordingGroupModelPredictAction(sharedPrediction);

        QLearningJAL policy = new TestableQLearningJAL(List.of(action1, action2), model);
        policy.setExplorationStrategy(new FixedExplorationStrategy(Optional.empty()));
        policy.init(SELF);

        MappedJointAction joint1 = sharedPrediction.withAction(SELF, action1);
        MappedJointAction joint2 = sharedPrediction.withAction(SELF, action2);

        policy.getTable().setValue(new Pair<>(input, joint1), 10.0);
        policy.getTable().setValue(new Pair<>(input, joint2), 1.0);

        // When
        Action selected = policy.selectAction(input);

        // Then
        assertThat(selected).isEqualTo(action1);
    }

    private static final class TestableQLearningJAL extends QLearningJAL {

        private final RandomGenerator randomGenerator;

        private TestableQLearningJAL(List<Action> actionsSet, GroupModelPredictAction moaGroupPredictAction) {
            super(actionsSet, moaGroupPredictAction);
            this.randomGenerator = new java.util.Random(0);
        }

        @Override
        public RandomGenerator prng() {
            return randomGenerator;
        }
    }

    private static final class FixedExplorationStrategy implements ExplorationStrategy {

        private final Optional<Action> exploratoryAction;

        private FixedExplorationStrategy(Optional<Action> exploratoryAction) {
            this.exploratoryAction = exploratoryAction;
        }

        @Override
        public Optional<Action> getExploratoryAction(List<Action> possibleActions, RandomGenerator pnrg) {
            return exploratoryAction;
        }

        @Override
        public void update() {
        }

        @Override
        public String getLoggerInfo() {
            return "fixed";
        }
    }

    private static final class RecordingGroupModelPredictAction implements GroupModelPredictAction {

        private final MappedJointAction fixedPrediction;
        private final List<PredictCall> predictCalls;

        private RecordingGroupModelPredictAction(MappedJointAction fixedPrediction) {
            this.fixedPrediction = fixedPrediction;
            this.predictCalls = new ArrayList<>();
        }

        @Override
        public MappedJointAction predictAction(PolicyInput observation) {
            predictCalls.add(new PredictCall(observation, null));
            return fixedPrediction;
        }

        @Override
        public MappedJointAction predictAction(PolicyInput observation, Action action) {
            predictCalls.add(new PredictCall(observation, action));
            return fixedPrediction;
        }

        @Override
        public void updateModel(PolicyInput observation, Action predictedAction, Action actualAction) {
        }

		@Override
        public MappedJointAction getLastPredictedJointAction() {
            return (MappedJointAction) fixedPrediction.copy();
		}
    }

    private record PredictCall(PolicyInput observation, Action action) {
    }

    private static final class DummyPolicyInput implements PolicyInput {

        @Override
        public PolicyInput add(PolicyInput other) {
            return this;
        }

        @Override
        public PolicyInput copy() {
            return new DummyPolicyInput();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj != null && getClass() == obj.getClass());
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }
    }

    private static final class DummyAgent implements MLKAgent {
        private final String name;

        private DummyAgent(String name) {
            this.name = name;
        }

        @Override
        public Policy getPolicy() {
            return null;
        }

        @Override
        public Algorithm getAlgorithm() {
            return null;
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
        public void setPolicy(Policy policy) {
        }

        @Override
        public void setAlgorithm(Algorithm algorithm) {
        }


        @Override
        public void initializeAll() {
        }

        @Override
        public void feedbackExperience(PolicyInput input, Action act, reward.Reward rew) {
        }

        @Override
        public void feedbackExperience(Experience experience) {
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
        public Experience getEnvExperience() {
            return null;
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

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof DummyAgent other && name.equals(other.name));
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

		@Override
		public void addAdditionalRole(String role) {
		}

		@Override
		public List<String> getAdditionalRole() {
			return null;
		}
    }
}
