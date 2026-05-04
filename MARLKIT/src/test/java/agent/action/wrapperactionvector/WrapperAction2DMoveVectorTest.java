package agent.action.wrapperactionvector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.testng.annotations.Test;

import agent.action.Action;
import agent.action.Move2D;
import util.Pair;

public class WrapperAction2DMoveVectorTest {

    @Test
    public void givenAction2DMove_whenTransformToVector_thenCorrectVectorReturned() {
        // Given
        Move2D action = new Move2D(new Pair<>(2, 3));
        WrapperAction2DMoveVector wrapper = new WrapperAction2DMoveVector();
        
        // When
        double[] vector = wrapper.transform(action);
        
        // Then
        assertThat(vector).hasSize(2);
        assertThat(vector[0]).isEqualTo(2);
        assertThat(vector[1]).isEqualTo(3);
    }
    
    @Test
    public void givenVector_whenTransformToAction_thenCorrectAction2DMoveReturned() {
        // Given
        double[] vector = {2, 3};
        WrapperAction2DMoveVector wrapper = new WrapperAction2DMoveVector();
        
        // When
        Action action = wrapper.transform(vector);
        
        // Then
        assertThat(action).isInstanceOf(Move2D.class);
        Move2D actionMove = (Move2D) action;
        assertThat(actionMove.getValue().getFirst()).isEqualTo(2);
        assertThat(actionMove.getValue().getSecond()).isEqualTo(3);
    }
    
    @Test
    public void givenInvalidVector_whenTransformToAction_thenExceptionThrown() {
        // Given
        double[] invalidVector = {1, 2, 3}; // Vector with wrong size
        WrapperAction2DMoveVector wrapper = new WrapperAction2DMoveVector();
        
        // When/Then
        assertThatThrownBy(() -> wrapper.transform(invalidVector))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Action2DMove must have a vector of size 2");
    }
    
    @Test
    public void givenPredefinedActions_whenTransformed_thenCorrectVectorsReturned() {
        // Given
        WrapperAction2DMoveVector wrapper = new WrapperAction2DMoveVector();
        Move2D up = Move2D.up();
        Move2D down = Move2D.down();
        Move2D left = Move2D.left();
        Move2D right = Move2D.right();
        
        // When
        double[] upVector = wrapper.transform(up);
        double[] downVector = wrapper.transform(down);
        double[] leftVector = wrapper.transform(left);
        double[] rightVector = wrapper.transform(right);
        
        // Then
        assertThat(upVector).containsExactly(0, 1);
        assertThat(downVector).containsExactly(0, -1);
        assertThat(leftVector).containsExactly(-1, 0);
        assertThat(rightVector).containsExactly(1, 0);
    }
}
