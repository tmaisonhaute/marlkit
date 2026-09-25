package agent.action.wrapperactionvector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import agent.action.Action;
import agent.action.Move2DDouble;
import agent.action.Move2DInt;
import agent.action.OrderedJointAction;

public class WrapperOrderedJointActionTest {

    @Test
    public void givenIndividualWrapperAndAgentCount_whenConstruct_thenWrapperCanTransformJointAction() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);

        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();

        OrderedJointAction jointAction = new OrderedJointAction();
        jointAction.addAction(firstAction);
        jointAction.addAction(secondAction);

        when(individualWrapper.transform(firstAction)).thenReturn(new double[] { -1.0, 0.0 });
        when(individualWrapper.transform(secondAction)).thenReturn(new double[] { 1.0, 0.0 });

        // When
        double[] vector = wrapper.transform(jointAction);

        // Then
        assertThat(vector).containsExactly(-1.0, 0.0, 1.0, 0.0);
    }

    @Test
    public void givenOrderedJointAction_whenTransform_thenIndividualActionsAreTransformedInOrder() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 3);

        Action firstAction = mock(Action.class);
        Action secondAction = mock(Action.class);
        Action thirdAction = mock(Action.class);

        OrderedJointAction jointAction = new OrderedJointAction();
        jointAction.addAction(firstAction);
        jointAction.addAction(secondAction);
        jointAction.addAction(thirdAction);

        when(individualWrapper.transform(firstAction)).thenReturn(new double[] { 1.0 });
        when(individualWrapper.transform(secondAction)).thenReturn(new double[] { 2.0 });
        when(individualWrapper.transform(thirdAction)).thenReturn(new double[] { 3.0 });

        // When
        double[] vector = wrapper.transform(jointAction);

        // Then
        assertThat(vector).containsExactly(1.0, 2.0, 3.0);
        verify(individualWrapper).transform(firstAction);
        verify(individualWrapper).transform(secondAction);
        verify(individualWrapper).transform(thirdAction);
    }

    @Test
    public void givenIndividualActionsWithDifferentVectorSizes_whenTransform_thenAllVectorsAreConcatenated() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 3);

        Action firstAction = mock(Action.class);
        Action secondAction = mock(Action.class);
        Action thirdAction = mock(Action.class);

        OrderedJointAction jointAction = new OrderedJointAction();
        jointAction.addAction(firstAction);
        jointAction.addAction(secondAction);
        jointAction.addAction(thirdAction);

        when(individualWrapper.transform(firstAction)).thenReturn(new double[] { 1.0 });
        when(individualWrapper.transform(secondAction)).thenReturn(new double[] { 2.0, 3.0 });
        when(individualWrapper.transform(thirdAction)).thenReturn(new double[] { 4.0, 5.0, 6.0 });

        // When
        double[] vector = wrapper.transform(jointAction);

        // Then
        assertThat(vector).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
    }

    @Test
    public void givenEmptyJointAction_whenTransform_thenEmptyVectorIsReturned() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);
        OrderedJointAction jointAction = new OrderedJointAction();

        // When
        double[] vector = wrapper.transform(jointAction);

        // Then
        assertThat(vector).isEmpty();
    }

    @Test
    public void givenNonJointAction_whenTransform_thenClassCastExceptionIsThrown() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);
        Action action = Move2DInt.up();

        // When & Then
        assertThatThrownBy(() -> wrapper.transform(action))
        	.isInstanceOf(ClassCastException.class);
    }

    @Test
    public void givenNullAction_whenTransform_thenNullPointerExceptionIsThrown() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);

        // When & Then
        assertThatThrownBy(() -> wrapper.transform((Action) null))
        	.isInstanceOf(ClassCastException.class);
    }

    @Test
    public void givenVectorDivisibleByAgentCount_whenTransform_thenOrderedJointActionIsReturned() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);

        when(individualWrapper.transform(any(double[].class))).thenAnswer(invocation -> {
                double[] vector = invocation.getArgument(0);
                return new Move2DDouble(vector[0], vector[1]);
            });

        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);

        Action firstAction = new Move2DDouble(1.0, 2.0);
        Action secondAction = new Move2DDouble(3.0, 4.0);

        // When
        Action transformedAction = wrapper.transform(new double[] {1.0, 2.0, 3.0, 4.0});

        // Then
        assertThat(transformedAction).isInstanceOf(OrderedJointAction.class);

        OrderedJointAction jointAction = (OrderedJointAction) transformedAction;

        assertThat(jointAction.size()).isEqualTo(2);
        assertThat(jointAction.getActions()).containsExactly(firstAction, secondAction);
    }

    @Test
    public void givenVector_whenTransform_thenEachWrapperCallReceivesCorrespondingVectorSegment() {
        // Given
        RecordingWrapperActionVector individualWrapper = new RecordingWrapperActionVector();
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 3);

        double[] vector = new double[] {
                1.0, 2.0,
                3.0, 4.0,
                5.0, 6.0
        };

        // When
        Action transformedAction = wrapper.transform(vector);

        // Then
        assertThat(transformedAction).isInstanceOf(OrderedJointAction.class);
        assertThat(individualWrapper.getReceivedVectors()).hasSize(3);
        assertThat(individualWrapper.getReceivedVectors().get(0)).containsExactly(1.0, 2.0);
        assertThat(individualWrapper.getReceivedVectors().get(1)).containsExactly(3.0, 4.0);
        assertThat(individualWrapper.getReceivedVectors().get(2)).containsExactly(5.0, 6.0);
    }

    @Test
    public void givenVectorNotDivisibleByAgentCount_whenTransform_thenIllegalArgumentExceptionIsThrown() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);
        double[] vector = new double[] { 1.0, 2.0, 3.0 };

        // When & Then
        assertThatThrownBy(() -> wrapper.transform(vector))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vector length must be divisible by the number of agents.");
    }

    @Test
    public void givenNullVector_whenTransform_thenNullPointerExceptionIsThrown() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);

        // When & Then
        assertThatThrownBy(() -> wrapper.transform((double[]) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenEmptyVectorAndPositiveAgentCount_whenTransform_thenOneEmptyActionPerAgentIsReturned() {
        // Given
        RecordingWrapperActionVector individualWrapper = new RecordingWrapperActionVector();
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);

        // When
        Action transformedAction = wrapper.transform(new double[0]);

        // Then
        assertThat(transformedAction).isInstanceOf(OrderedJointAction.class);

        OrderedJointAction jointAction = (OrderedJointAction) transformedAction;

        assertThat(jointAction.size()).isEqualTo(2);
        assertThat(individualWrapper.getReceivedVectors()).hasSize(2);
        assertThat(individualWrapper.getReceivedVectors().get(0)).isEmpty();
        assertThat(individualWrapper.getReceivedVectors().get(1)).isEmpty();
    }

    @Test
    public void givenNullIndividualWrapper_whenConstruct_thenNullPointerExceptionIsThrown() {
        // Given & When & Then
        assertThatThrownBy(() -> new WrapperOrderedJointAction(null, 2))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("wrapperActionVector");
    }


    @Test
    public void givenNegativeAgentCount_whenConstruct_thenIllegalArgumentExceptionIsThrown() {
        // Given
        WrapperActionVector individualWrapper = mock(WrapperActionVector.class);

        // When & Then
        assertThatThrownBy(() -> new WrapperOrderedJointAction(individualWrapper, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nbAgents");
    }


    @Test
    public void givenJointActionWithCorrectAgentCount_whenRoundTripTransform_thenEquivalentJointActionIsReturned() {
        // Given
        WrapperActionVector individualWrapper = new WrapperMove2DIntVector();
        WrapperOrderedJointAction wrapper = new WrapperOrderedJointAction(individualWrapper, 2);

        Move2DInt firstAction = Move2DInt.left();
        Move2DInt secondAction = Move2DInt.up();

        OrderedJointAction original = new OrderedJointAction();
        original.addAction(firstAction);
        original.addAction(secondAction);

        // When
        double[] vector = wrapper.transform(original);
        Action transformedAction = wrapper.transform(vector);

        // Then
        assertThat(vector).containsExactly(-1.0, 0.0, 0.0, 1.0);
        assertThat(transformedAction).isInstanceOf(OrderedJointAction.class);

        OrderedJointAction reconstructed = (OrderedJointAction) transformedAction;

        assertThat(reconstructed.getActions()).containsExactly(firstAction, secondAction);
    }

    private static class RecordingWrapperActionVector implements WrapperActionVector {

        private final java.util.List<double[]> receivedVectors = new java.util.ArrayList<>();

        @Override
        public double[] transform(Action action) {
            if (action instanceof Move2DDouble move) {
                return new double[] { move.getFirst(), move.getSecond() };
            }

            throw new IllegalArgumentException("Expected Move2DDouble.");
        }

        @Override
        public Action transform(double[] vector) {
            receivedVectors.add(java.util.Arrays.copyOf(vector, vector.length));

            double first = vector.length > 0 ? vector[0] : 0.0;
            double second = vector.length > 1 ? vector[1] : 0.0;

            return new Move2DDouble(first, second);
        }

        public java.util.List<double[]> getReceivedVectors() {
            return receivedVectors;
        }
    }
}