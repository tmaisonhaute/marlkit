package agent.action;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

public class ActionIntTest {

    @Test
    public void givenActionInt_whenGetValue_thenReturnValue() {
        // Given
        ActionInt action = new ActionInt(7);

        // When
        int value = action.getValue();

        // Then
        assertThat(value).isEqualTo(7);
    }

    @Test
    public void givenActionInt_whenSetValue_thenReturnUpdatedValue() {
        // Given
        ActionInt action = new ActionInt(7);

        // When
        action.setValue(9);

        // Then
        assertThat(action.getValue()).isEqualTo(9);
    }

    @Test
    public void givenTwoActionIntWithSameValue_whenEquals_thenReturnTrue() {
        // Given
        ActionInt action1 = new ActionInt(7);
        ActionInt action2 = new ActionInt(7);

        // When
        boolean isEqual = action1.equals(action2);

        // Then
        assertThat(isEqual).isTrue();
    }

    @Test
    public void givenActionInt_whenEqualsNull_thenReturnFalse() {
        // Given
        ActionInt action = new ActionInt(7);

        // When
        Object other = null;
        boolean isEqual = action.equals(other);

        // Then
        assertThat(isEqual).isFalse();
    }

    @Test
    public void givenActionInt_whenCopy_thenReturnNewInstanceWithSameValue() {
        // Given
        ActionInt action = new ActionInt(7);

        // When
        ActionInt copy = action.copy();

        // Then
        assertThat(copy).isEqualTo(new ActionInt(7)).isNotSameAs(action);
    }
}
