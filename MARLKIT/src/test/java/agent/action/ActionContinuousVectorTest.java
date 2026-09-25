package agent.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.testng.annotations.Test;

import agent.MLKAgent;

public class ActionContinuousVectorTest {

    @Test
    public void givenValues_whenConstructWithoutBounds_thenValuesAreStoredAndBoundsAreInfinite() {
        // Given
        double[] values = new double[] { 1.0, -2.0, 3.5 };

        // When
        ActionContinuousVector action = new ActionContinuousVector(values);

        // Then
        assertThat(action.getValues()).containsExactly(1.0, -2.0, 3.5);
        assertThat(action.getLowerBound()).isEqualTo(Double.NEGATIVE_INFINITY);
        assertThat(action.getUpperBound()).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    public void givenValuesAndBounds_whenConstruct_thenValuesAndBoundsAreStored() {
        // Given
        double[] values = new double[] { -0.5, 0.0, 0.5 };

        // When
        ActionContinuousVector action = new ActionContinuousVector(values, -1.0, 1.0);

        // Then
        assertThat(action.getValues()).containsExactly(-0.5, 0.0, 0.5);
        assertThat(action.getLowerBound()).isEqualTo(-1.0);
        assertThat(action.getUpperBound()).isEqualTo(1.0);
    }

    @Test
    public void givenValuesOutsideBounds_whenConstruct_thenValuesAreClipped() {
        // Given
        double[] values = new double[] { -2.0, -1.0, 0.0, 1.0, 2.0 };

        // When
        ActionContinuousVector action = new ActionContinuousVector(values, -1.0, 1.0);

        // Then
        assertThat(action.getValues()).containsExactly(-1.0, -1.0, 0.0, 1.0, 1.0);
    }

    @Test
    public void givenEqualBounds_whenConstruct_thenAllValuesAreClippedToBound() {
        // Given
        double[] values = new double[] { -5.0, 0.0, 5.0 };

        // When
        ActionContinuousVector action = new ActionContinuousVector(values, 2.0, 2.0);

        // Then
        assertThat(action.getValues()).containsExactly(2.0, 2.0, 2.0);
        assertThat(action.getLowerBound()).isEqualTo(2.0);
        assertThat(action.getUpperBound()).isEqualTo(2.0);
    }

    @Test
    public void givenLowerBoundGreaterThanUpperBound_whenConstruct_thenIllegalArgumentExceptionIsThrown() {
        // Given
        double[] values = new double[] { 0.0 };

        // When & Then
        assertThatThrownBy(() -> new ActionContinuousVector(values, 1.0, -1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lowerBound must be less than or equal to upperBound.");
    }

    @Test
    public void givenNaNValue_whenConstructWithBounds_thenIllegalArgumentExceptionIsThrown() {
        // Given
        double[] values = new double[] { 0.0, Double.NaN };

        // When & Then
        assertThatThrownBy(() -> new ActionContinuousVector(values, -1.0, 1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Action values must not be NaN.");
    }

    @Test
    public void givenNullValues_whenConstruct_thenNullPointerExceptionIsThrown() {
        // Given
        double[] values = null;

        // When & Then
        assertThatThrownBy(() -> new ActionContinuousVector(values))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenNullValues_whenConstructWithBounds_thenNullPointerExceptionIsThrown() {
        // Given
        double[] values = null;

        // When & Then
        assertThatThrownBy(() -> new ActionContinuousVector(values, -1.0, 1.0))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenSize_whenConstruct_thenVectorContainsZeros() {
        // Given
        int size = 3;

        // When
        ActionContinuousVector action = new ActionContinuousVector(size);

        // Then
        assertThat(action.getSize()).isEqualTo(3);
        assertThat(action.getValues()).containsExactly(0.0, 0.0, 0.0);
        assertThat(action.getLowerBound()).isEqualTo(Double.NEGATIVE_INFINITY);
        assertThat(action.getUpperBound()).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    public void givenZeroSize_whenConstruct_thenVectorIsEmpty() {
        // Given & When
        ActionContinuousVector action = new ActionContinuousVector(0);

        // Then
        assertThat(action.getSize()).isZero();
        assertThat(action.getValues()).isEmpty();
    }

    @Test
    public void givenNegativeSize_whenConstruct_thenNegativeArraySizeExceptionIsThrown() {
        // Given & When & Then
        assertThatThrownBy(() -> new ActionContinuousVector(-1))
                .isInstanceOf(NegativeArraySizeException.class);
    }

    @Test
    public void givenSourceArray_whenConstructThenSourceArrayMutated_thenActionValuesRemainUnchanged() {
        // Given
        double[] values = new double[] { 1.0, 2.0 };
        ActionContinuousVector action = new ActionContinuousVector(values);

        // When
        values[0] = 99.0;

        // Then
        assertThat(action.getValues()).containsExactly(1.0, 2.0);
    }

    @Test
    public void givenAction_whenReturnedValuesArrayIsMutated_thenActionValuesRemainUnchanged() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0, 2.0 });

        // When
        double[] returnedValues = action.getValues();
        returnedValues[0] = 99.0;

        // Then
        assertThat(action.getValues()).containsExactly(1.0, 2.0);
    }

    @Test
    public void givenUnboundedAction_whenSetBounds_thenBoundsAreStored() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { -0.5, 0.5 });

        // When
        action.setBounds(-1.0, 1.0);

        // Then
        assertThat(action.getLowerBound()).isEqualTo(-1.0);
        assertThat(action.getUpperBound()).isEqualTo(1.0);
        assertThat(action.getValues()).containsExactly(-0.5, 0.5);
    }

    @Test
    public void givenActionWithValuesOutsideNewBounds_whenSetBounds_thenValuesAreClipped() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { -5.0, 0.0, 5.0 });

        // When
        action.setBounds(-2.0, 2.0);

        // Then
        assertThat(action.getValues()).containsExactly(-2.0, 0.0, 2.0);
    }

    @Test
    public void givenInvalidBounds_whenSetBounds_thenIllegalArgumentExceptionIsThrownAndOldBoundsRemain() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 }, -1.0, 1.0);

        // When & Then
        assertThatThrownBy(() -> action.setBounds(2.0, -2.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lowerBound must be less than or equal to upperBound.");

        assertThat(action.getLowerBound()).isEqualTo(-1.0);
        assertThat(action.getUpperBound()).isEqualTo(1.0);
    }

    @Test
    public void givenBoundedAction_whenClearBounds_thenBoundsBecomeInfinite() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.5 }, -1.0, 1.0);

        // When
        action.clearBounds();

        // Then
        assertThat(action.getLowerBound()).isEqualTo(Double.NEGATIVE_INFINITY);
        assertThat(action.getUpperBound()).isEqualTo(Double.POSITIVE_INFINITY);
        assertThat(action.getValue(0)).isEqualTo(0.5);
    }

    @Test
    public void givenClearedBounds_whenSetValueOutsidePreviousBounds_thenValueIsNotClipped() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 }, -1.0, 1.0);
        action.clearBounds();

        // When
        action.setValue(0, 10.0);

        // Then
        assertThat(action.getValue(0)).isEqualTo(10.0);
    }

    @Test
    public void givenValueWithinBounds_whenSetValue_thenValueIsStored() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 }, -1.0, 1.0);

        // When
        action.setValue(0, 0.75);

        // Then
        assertThat(action.getValue(0)).isEqualTo(0.75);
    }

    @Test
    public void givenValueBelowLowerBound_whenSetValue_thenValueIsClippedToLowerBound() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 }, -1.0, 1.0);

        // When
        action.setValue(0, -10.0);

        // Then
        assertThat(action.getValue(0)).isEqualTo(-1.0);
    }

    @Test
    public void givenValueAboveUpperBound_whenSetValue_thenValueIsClippedToUpperBound() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 }, -1.0, 1.0);

        // When
        action.setValue(0, 10.0);

        // Then
        assertThat(action.getValue(0)).isEqualTo(1.0);
    }

    @Test
    public void givenNaNValue_whenSetValue_thenIllegalArgumentExceptionIsThrownAndPreviousValueIsPreserved() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.5 }, -1.0, 1.0);

        // When & Then
        assertThatThrownBy(() -> action.setValue(0, Double.NaN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Action values must not be NaN.");

        assertThat(action.getValue(0)).isEqualTo(0.5);
    }

    @Test
    public void givenInvalidIndex_whenSetValue_thenIndexOutOfBoundsExceptionIsThrown() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 });

        // When & Then
        assertThatThrownBy(() -> action.setValue(1, 2.0))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void givenInvalidIndex_whenGetValue_thenIndexOutOfBoundsExceptionIsThrown() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 });

        // When & Then
        assertThatThrownBy(() -> action.getValue(1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void givenNewValuesWithinBounds_whenSetValues_thenValuesAreReplaced() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0, 0.0 }, -1.0, 1.0);

        // When
        action.setValues(new double[] { 0.25, -0.75, 1.0 });

        // Then
        assertThat(action.getValues()).containsExactly(0.25, -0.75, 1.0);
        assertThat(action.getSize()).isEqualTo(3);
    }

    @Test
    public void givenNewValuesOutsideBounds_whenSetValues_thenValuesAreClipped() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 }, -1.0, 1.0);

        // When
        action.setValues(new double[] { -10.0, 0.0, 10.0 });

        // Then
        assertThat(action.getValues()).containsExactly(-1.0, 0.0, 1.0);
    }

    @Test
    public void givenNaNInNewValues_whenSetValues_thenIllegalArgumentExceptionIsThrownAndPreviousValuesArePreserved() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.5 }, -1.0, 1.0);

        // When & Then
        assertThatThrownBy(() -> action.setValues(new double[] { 0.0, Double.NaN }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Action values must not be NaN.");

        assertThat(action.getValues()).containsExactly(0.5);
    }

    @Test
    public void givenNullValues_whenSetValues_thenNullPointerExceptionIsThrown() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.0 });

        // When & Then
        assertThatThrownBy(() -> action.setValues(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenSourceArray_whenSetValuesThenSourceArrayMutated_thenActionValuesRemainUnchanged() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(2);
        double[] newValues = new double[] { 1.0, 2.0 };

        // When
        action.setValues(newValues);
        newValues[0] = 99.0;

        // Then
        assertThat(action.getValues()).containsExactly(1.0, 2.0);
    }

    @Test
    public void givenAction_whenCopy_thenValuesAndBoundsAreCopied() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { -0.5, 0.5 }, -1.0, 1.0);

        // When
        ActionContinuousVector copy = action.copy();

        // Then
        assertThat(copy).isNotSameAs(action);
        assertThat(copy.getValues()).containsExactly(-0.5, 0.5);
        assertThat(copy.getLowerBound()).isEqualTo(-1.0);
        assertThat(copy.getUpperBound()).isEqualTo(1.0);
    }

    @Test
    public void givenCopiedAction_whenOriginalIsMutated_thenCopyRemainsUnchanged() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.25, 0.5 }, -1.0, 1.0);
        ActionContinuousVector copy = action.copy();

        // When
        action.setValue(0, 0.75);
        action.clearBounds();

        // Then
        assertThat(copy.getValues()).containsExactly(0.25, 0.5);
        assertThat(copy.getLowerBound()).isEqualTo(-1.0);
        assertThat(copy.getUpperBound()).isEqualTo(1.0);
    }

    @Test
    public void givenCopiedAction_whenCopyIsMutated_thenOriginalRemainsUnchanged() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 0.25, 0.5 }, -1.0, 1.0);
        ActionContinuousVector copy = action.copy();

        // When
        copy.setValue(1, -0.75);

        // Then
        assertThat(action.getValues()).containsExactly(0.25, 0.5);
        assertThat(copy.getValues()).containsExactly(0.25, -0.75);
    }

    @Test
    public void givenAction_whenComparedWithItself_thenEqualsReturnsTrue() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0, 2.0 });

        // When
        boolean equal = action.equals(action);

        // Then
        assertThat(equal).isTrue();
    }

    @Test
    public void givenAction_whenComparedWithNull_thenEqualsReturnsFalse() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0, 2.0 });

        // When
        boolean equal = action.equals(null);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenAction_whenComparedWithDifferentActionType_thenEqualsReturnsFalse() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0, 2.0 });
        Action otherAction = Move2DInt.up();

        // When
        boolean equal = action.equals(otherAction);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenActionsWithSameValues_whenCompared_thenEqualsReturnsTrue() {
        // Given
        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 1.0, 2.0 });
        ActionContinuousVector secondAction = new ActionContinuousVector(new double[] { 1.0, 2.0 });

        // When
        boolean equal = firstAction.equals(secondAction);

        // Then
        assertThat(equal).isTrue();
    }

    @Test
    public void givenActionsWithDifferentValues_whenCompared_thenEqualsReturnsFalse() {
        // Given
        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 1.0, 2.0 });
        ActionContinuousVector secondAction = new ActionContinuousVector(new double[] { 1.0, 3.0 });

        // When
        boolean equal = firstAction.equals(secondAction);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenActionsWithDifferentSizes_whenCompared_thenEqualsReturnsFalse() {
        // Given
        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 1.0, 2.0 });
        ActionContinuousVector secondAction = new ActionContinuousVector(new double[] { 1.0 });

        // When
        boolean equal = firstAction.equals(secondAction);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenActionsWithSameValuesAndDifferentBounds_whenCompared_thenEqualsReturnsTrue() {
        // Given
        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 0.5 }, -1.0, 1.0);
        ActionContinuousVector secondAction = new ActionContinuousVector(new double[] { 0.5 }, -10.0, 10.0);

        // When
        boolean equal = firstAction.equals(secondAction);

        // Then
        assertThat(equal).isTrue();
    }

    @Test
    public void givenEqualActions_whenHashCodeCalled_thenHashCodesAreEqual() {
        // Given
        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 1.0, 2.0 });
        ActionContinuousVector secondAction = new ActionContinuousVector(new double[] { 1.0, 2.0 });

        // When
        int firstHashCode = firstAction.hashCode();
        int secondHashCode = secondAction.hashCode();

        // Then
        assertThat(firstHashCode).isEqualTo(secondHashCode);
    }

    @Test
    public void givenActionsWithSameValuesAndDifferentBounds_whenHashCodeCalled_thenHashCodesAreEqual() {
        // Given
        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 0.5 }, -1.0, 1.0);
        ActionContinuousVector secondAction = new ActionContinuousVector(new double[] { 0.5 }, -10.0, 10.0);

        // When
        int firstHashCode = firstAction.hashCode();
        int secondHashCode = secondAction.hashCode();

        // Then
        assertThat(firstHashCode).isEqualTo(secondHashCode);
    }

    @Test
    public void givenEmptyJointAction_whenFromJointAction_thenIllegalArgumentExceptionIsThrown() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();

        // When & Then
        assertThatThrownBy(() -> ActionContinuousVector.fromJointAction(jointAction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Joint action must not be empty.");
    }

    @Test
    public void givenSingleContinuousAction_whenFromJointAction_thenEquivalentVectorIsReturned() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);
        ActionContinuousVector individualAction = new ActionContinuousVector(new double[] { 0.25, -0.5 }, -1.0, 1.0);
        MappedJointAction jointAction = new MappedJointAction();

        jointAction.addAction(agent, individualAction);

        // When
        ActionContinuousVector mergedAction = ActionContinuousVector.fromJointAction(jointAction);

        // Then
        assertThat(mergedAction.getValues()).containsExactly(0.25, -0.5);
        assertThat(mergedAction.getLowerBound()).isEqualTo(-1.0);
        assertThat(mergedAction.getUpperBound()).isEqualTo(1.0);
    }

    @Test
    public void givenMultipleContinuousActions_whenFromJointAction_thenValuesAreConcatenatedInJointOrder() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        MLKAgent thirdAgent = mock(MLKAgent.class);

        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 1.0, 2.0 }, -10.0, 10.0);
        ActionContinuousVector secondAction = new ActionContinuousVector(new double[] { 3.0 }, -10.0, 10.0);
        ActionContinuousVector thirdAction = new ActionContinuousVector(new double[] { 4.0, 5.0 }, -10.0, 10.0);

        MappedJointAction jointAction = new MappedJointAction();
        jointAction.addAction(firstAgent, firstAction);
        jointAction.addAction(secondAgent, secondAction);
        jointAction.addAction(thirdAgent, thirdAction);

        // When
        ActionContinuousVector mergedAction = ActionContinuousVector.fromJointAction(jointAction);

        // Then
        assertThat(mergedAction.getValues()).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0);
        assertThat(mergedAction.getSize()).isEqualTo(5);
    }

    @Test
    public void givenContinuousActionsWithDifferentBounds_whenFromJointAction_thenBroadestBoundsAreUsed() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 0.5 }, -1.0, 1.0);
        ActionContinuousVector secondAction = new ActionContinuousVector(new double[] { 5.0 }, -10.0, 10.0);

        MappedJointAction jointAction = new MappedJointAction();
        jointAction.addAction(firstAgent, firstAction);
        jointAction.addAction(secondAgent, secondAction);

        // When
        ActionContinuousVector mergedAction = ActionContinuousVector.fromJointAction(jointAction);

        // Then
        assertThat(mergedAction.getValues()).containsExactly(0.5, 5.0);
        assertThat(mergedAction.getLowerBound()).isEqualTo(-10.0);
        assertThat(mergedAction.getUpperBound()).isEqualTo(10.0);
    }

    @Test
    public void givenJointActionContainingNonContinuousAction_whenFromJointAction_thenIllegalArgumentExceptionIsThrown() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        MappedJointAction jointAction = new MappedJointAction();
        jointAction.addAction(firstAgent, new ActionContinuousVector(new double[] { 0.5 }));
        jointAction.addAction(secondAgent, Move2DInt.up());

        // When & Then
        assertThatThrownBy(() -> ActionContinuousVector.fromJointAction(jointAction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("All actions in the joint action must be of type ActionContinuousVector.");
    }

    @Test
    public void givenJointActionContainingEmptyContinuousVector_whenFromJointAction_thenOtherValuesArePreserved() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        MappedJointAction jointAction = new MappedJointAction();
        jointAction.addAction(firstAgent, new ActionContinuousVector(new double[0], -1.0, 1.0));
        jointAction.addAction(secondAgent, new ActionContinuousVector(new double[] { 2.0 }, -5.0, 5.0));

        // When
        ActionContinuousVector mergedAction = ActionContinuousVector.fromJointAction(jointAction);

        // Then
        assertThat(mergedAction.getValues()).containsExactly(2.0);
        assertThat(mergedAction.getLowerBound()).isEqualTo(-5.0);
        assertThat(mergedAction.getUpperBound()).isEqualTo(5.0);
    }

    @Test
    public void givenJointAction_whenFromJointActionThenIndividualActionMutated_thenMergedActionRemainsUnchanged() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);
        ActionContinuousVector individualAction = new ActionContinuousVector(new double[] { 1.0, 2.0 });
        MappedJointAction jointAction = new MappedJointAction();

        jointAction.addAction(agent, individualAction);

        // When
        ActionContinuousVector mergedAction = ActionContinuousVector.fromJointAction(jointAction);
        individualAction.setValue(0, 99.0);

        // Then
        assertThat(mergedAction.getValues()).containsExactly(1.0, 2.0);
    }
}