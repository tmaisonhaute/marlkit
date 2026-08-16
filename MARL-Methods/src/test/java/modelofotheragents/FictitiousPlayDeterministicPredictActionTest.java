package modelofotheragents;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

import agent.action.Action;
import agent.action.ActionInt;
import learning.policies.PolicyInput;

public class FictitiousPlayDeterministicPredictActionTest {

    @Test
    public void givenNoHistory_whenPredict_thenReturnDefaultCopy() {
        // Given
        ActionInt defaultAction = new ActionInt(1);
        FictitiousPlayDeterministicPredictAction model =
                new FictitiousPlayDeterministicPredictAction(defaultAction);
        PolicyInput observation = new DummyPolicyInput();

        // When
        Action predicted = model.predictAction(observation);

        // Then
        assertThat(predicted).isEqualTo(defaultAction);
        assertThat(predicted).isNotSameAs(defaultAction);
    }

    @Test
    public void givenUpdates_whenPredict_thenReturnMostFrequentAction() {
        // Given
        ActionInt defaultAction = new ActionInt(0);
        FictitiousPlayDeterministicPredictAction model =
                new FictitiousPlayDeterministicPredictAction(defaultAction);
        PolicyInput observation = new DummyPolicyInput();

        ActionInt actionA = new ActionInt(1);
        ActionInt actionB = new ActionInt(2);

        model.updateModel(observation, null, actionA);
        model.updateModel(observation, null, actionA);
        model.updateModel(observation, null, actionB);

        // When
        Action predicted = model.predictAction(observation);

        // Then
        assertThat(predicted).isEqualTo(actionA);
    }

    @Test
    public void givenMultipleObservations_whenPredict_thenUseSeparateFrequencies() {
        // Given
        ActionInt defaultAction = new ActionInt(0);
        FictitiousPlayDeterministicPredictAction model =
                new FictitiousPlayDeterministicPredictAction(defaultAction);

        PolicyInput observation1 = new DummyPolicyInput(Arrays.asList(1.0));
        PolicyInput observation2 = new DummyPolicyInput(Arrays.asList(2.0));

        ActionInt actionA = new ActionInt(1);
        ActionInt actionB = new ActionInt(2);

        model.updateModel(observation1, null, actionA);
        model.updateModel(observation2, null, actionB);

        // When
        Action predicted1 = model.predictAction(observation1);
        Action predicted2 = model.predictAction(observation2);

        // Then
        assertThat(predicted1).isEqualTo(actionA);
        assertThat(predicted2).isEqualTo(actionB);
    }

    @Test
    public void givenPrediction_whenCalled_thenLastPredictedActionIsStored() {
        // Given
        ActionInt defaultAction = new ActionInt(0);
        FictitiousPlayDeterministicPredictAction model =
                new FictitiousPlayDeterministicPredictAction(defaultAction);
        PolicyInput observation = new DummyPolicyInput();

        ActionInt actionA = new ActionInt(1);
        model.updateModel(observation, null, actionA);

        // When
        Action predicted = model.predictAction(observation);

        // Then
        assertThat(model.getLastPredictedAction()).isEqualTo(predicted);
        assertThat(model.getLastPredictedAction()).isNotSameAs(predicted);
    }

    @Test
    public void givenActionMutatedAfterUpdate_whenPredict_thenStoredCopyUsed() {
        // Given
        ActionInt defaultAction = new ActionInt(0);
        FictitiousPlayDeterministicPredictAction model =
                new FictitiousPlayDeterministicPredictAction(defaultAction);
        PolicyInput observation = new DummyPolicyInput();

        ActionInt actionA = new ActionInt(1);
        model.updateModel(observation, null, actionA);
        actionA.setValue(9);

        // When
        Action predicted = model.predictAction(observation);

        // Then
        assertThat(predicted).isEqualTo(new ActionInt(1));
    }

    private static final class DummyPolicyInput implements PolicyInput {
        private final Object key;

        private DummyPolicyInput() {
            this.key = new Object();
        }

        private DummyPolicyInput(Object key) {
            this.key = key;
        }

        @Override
        public PolicyInput add(PolicyInput other) {
            return this;
        }

        @Override
        public PolicyInput copy() {
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof DummyPolicyInput other && key.equals(other.key));
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }
}
