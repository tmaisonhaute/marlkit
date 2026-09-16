package modelofotheragents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.ActionInt;
import agent.action.ActionSpace;
import agent.action.MappedJointAction;
import agent.action.OrderedJointAction;
import environment.MLKEnvironment;
import environment.observation.Observation;
import experience.Experience;
import learning.Algorithm;
import learning.Policy;
import learning.policies.valuefunction.ActionEvaluator;
import madkit.simulation.SimuAgent;

public class MinimaxValueFunctionPredictActionTest {

    private static final MLKAgent SELF = new DummyAgent("self");
    private static final MLKAgent OTHER1 = new DummyAgent("other1");
    private static final MLKAgent OTHER2 = new DummyAgent("other2");

    @Test
    public void givenPredictActionWithoutOwnAction_whenCalled_thenThrowUnsupportedOperationException() {
        // Given
        MinimaxValueFunctionPredictAction model = new MinimaxValueFunctionPredictAction(new DummyEvaluator());
        model.setPredictingAgent(SELF);

        // When / Then
        assertThatThrownBy(() -> model.predictAction(new DummyObservation()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void givenMultipleJointActions_whenPredict_thenSelectWorstConsistentAndRemoveOwnAction() {
        // Given
        ActionInt own = new ActionInt(1);
        ActionInt other1 = new ActionInt(2);
        ActionInt other2 = new ActionInt(3);

        MappedJointAction ja1 = new MappedJointAction(Map.of(SELF, own, OTHER1, other1));
        MappedJointAction ja2 = new MappedJointAction(Map.of(SELF, own, OTHER1, other2));

        RecordingEvaluator evaluator = new RecordingEvaluator(
                List.of(ja1, ja2)
        );
        evaluator.values.put(ja1, 10.0);
        evaluator.values.put(ja2, -5.0);

        MinimaxValueFunctionPredictAction model =
                new MinimaxValueFunctionPredictAction(evaluator);
        model.setPredictingAgent(SELF);

        // When
        MappedJointAction predicted = model.predictAction(new DummyObservation(), own);

        // Then
		assertThat(predicted.getAction(OTHER1)).isEqualTo(other2);
		assertThat(predicted.size()).isEqualTo(1);
    }

    @Test
    public void givenJointActionsWithDifferentOwnAction_whenPredict_thenIgnoreInconsistentOnes() {
        // Given
        ActionInt own = new ActionInt(1);
        ActionInt otherOwn = new ActionInt(99);
        ActionInt other = new ActionInt(2);

        MappedJointAction inconsistent = new MappedJointAction(Map.of(SELF, otherOwn, OTHER1, other));
        MappedJointAction consistent = new MappedJointAction(Map.of(SELF, own, OTHER1, other));

        RecordingEvaluator evaluator =
                new RecordingEvaluator(List.of(inconsistent, consistent));
        evaluator.values.put(consistent, 1.0);

        MinimaxValueFunctionPredictAction model =
                new MinimaxValueFunctionPredictAction(evaluator);
        model.setPredictingAgent(SELF);

        // When
        MappedJointAction predicted = model.predictAction(new DummyObservation(), own);

        // Then
		assertThat(predicted.getAction(OTHER1)).isEqualTo(other);
		assertThat(predicted.size()).isEqualTo(1);
    }

    @Test
    public void givenFilteredActionSpaceEmpty_whenPredict_thenUseDefaultJointAction() {
        // Given
        ActionInt own = new ActionInt(1);

        RecordingEvaluator evaluator =
                new RecordingEvaluator(List.of());

        MinimaxValueFunctionPredictAction model =
                new MinimaxValueFunctionPredictAction(evaluator);
        model.setPredictingAgent(SELF);

        // When
        MappedJointAction predicted = model.predictAction(new DummyObservation(), own);

        // Then
		assertThat(predicted.getMappedActions()).isEmpty();
    }

    @Test
    public void givenEqualValues_whenPredict_thenReturnFirstWorstEncountered() {
        // Given
        ActionInt own = new ActionInt(1);
        ActionInt other1 = new ActionInt(2);
        ActionInt other2 = new ActionInt(3);

        MappedJointAction ja1 = new MappedJointAction(Map.of(SELF, own, OTHER1, other1));
        MappedJointAction ja2 = new MappedJointAction(Map.of(SELF, own, OTHER1, other2));

        RecordingEvaluator evaluator =
                new RecordingEvaluator(List.of(ja1, ja2));
        evaluator.values.put(ja1, 0.0);
        evaluator.values.put(ja2, 0.0);

        MinimaxValueFunctionPredictAction model =
                new MinimaxValueFunctionPredictAction(evaluator);
        model.setPredictingAgent(SELF);

        // When
        MappedJointAction predicted = model.predictAction(new DummyObservation(), own);

        // Then
		assertThat(predicted.getAction(OTHER1)).isEqualTo(other1);
		assertThat(predicted.size()).isEqualTo(1);
    }

    @Test
    public void givenActionSpaceContainingNonJointAction_whenPredict_thenThrowIllegalArgumentException() {
        // Given
        ActionInt own = new ActionInt(1);
        Action nonJoint = new ActionInt(99);

        ActionSpace space = new ActionSpace(List.of(nonJoint));

        ActionEvaluator evaluator = new ActionEvaluator() {
            @Override
            public Double getValue(Observation input, Action action) {
                return 0.0;
            }

            @Override
            public ActionSpace getActionSpace(Observation observation) {
                return space;
            }
        };

        MinimaxValueFunctionPredictAction model =
                new MinimaxValueFunctionPredictAction(evaluator);
        model.setPredictingAgent(SELF);

        // When / Then
        assertThatThrownBy(() -> model.predictAction(new DummyObservation(), own))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenPrediction_whenReturned_thenResultIsDefensiveCopy() {
        // Given
        ActionInt own = new ActionInt(1);
        ActionInt other = new ActionInt(2);

        MappedJointAction ja = new MappedJointAction(Map.of(SELF, own, OTHER1, other));

        RecordingEvaluator evaluator =
                new RecordingEvaluator(List.of(ja));
        evaluator.values.put(ja, -1.0);

        MinimaxValueFunctionPredictAction model =
                new MinimaxValueFunctionPredictAction(evaluator);
        model.setPredictingAgent(SELF);

        // When
        MappedJointAction predicted = model.predictAction(new DummyObservation(), own);
        predicted.addAction(OTHER2, new ActionInt(999));

        // Then
		assertThat(ja.size()).isEqualTo(2); // original untouched
    }
    
    @Test
    public void givenDefaultActionSpace_whenAddAction_thenActionIsAdded() {
        // Given
        ActionSpace actionSpace = new ActionSpace();
        ActionInt action = new ActionInt(1);

        // When
        actionSpace.addAction(action);

        // Then
        assertThat(actionSpace.getActions()).containsExactly(action);
    }
    
    @Test
    public void givenJointActionCreatedWithOf_whenRemoveAction_thenActionIsRemoved() {
        // Given
        ActionInt action1 = new ActionInt(1);
        ActionInt action2 = new ActionInt(2);
        OrderedJointAction jointAction = OrderedJointAction.of(action1, action2);

        // When
        jointAction.removeActionAtIndex(0);

        // Then
        assertThat(jointAction.getActions()).containsExactly(action2);
    }
    
    @Test
    public void givenMatchingJointAction_whenPredict_thenFilteringCanAddToNewActionSpace() {
        // Given
        ActionInt own = new ActionInt(1);
        ActionInt other = new ActionInt(2);

        MappedJointAction jointAction = new MappedJointAction(Map.of(SELF, own, OTHER1, other));

        RecordingEvaluator evaluator = new RecordingEvaluator(List.of(jointAction));
        evaluator.values.put(jointAction, 3.0);

        MinimaxValueFunctionPredictAction model =
                new MinimaxValueFunctionPredictAction(evaluator);
        model.setPredictingAgent(SELF);

        // When
        MappedJointAction predicted = model.predictAction(new DummyObservation(), own);

        // Then
		assertThat(predicted.getAction(OTHER1)).isEqualTo(other);
		assertThat(predicted.size()).isEqualTo(1);
    }

    // ---------- Test doubles ----------

    private static final class RecordingEvaluator implements ActionEvaluator {

        private final ActionSpace space;
        private final Map<Action, Double> values = new HashMap<>();

        private RecordingEvaluator(List<MappedJointAction> actions) {
            this.space = new ActionSpace(new ArrayList<>(actions));
        }

        @Override
        public Double getValue(Observation input, Action action) {
            return values.getOrDefault(action, 0.0);
        }

        @Override
        public ActionSpace getActionSpace(Observation observation) {
            return space;
        }
    }

    private static final class DummyObservation implements Observation {

        @Override
        public void add(Observation other) {
        }

        @Override
        public Observation copy() {
            return this;
        }
    }

    private static final class DummyEvaluator implements ActionEvaluator {

        @Override
        public Double getValue(Observation input, Action action) {
            return 0.0;
        }

        @Override
        public ActionSpace getActionSpace(Observation observation) {
            return new ActionSpace();
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
        public java.util.random.RandomGenerator prng() {
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
        public void feedbackExperience(Experience experience) {
        }

        @Override
        public Action selectAction(Observation input) {
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