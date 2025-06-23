package util.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.testng.annotations.Test;

public class ModuloTimeCriterionTest {

    @Test
    public void givenNewCriterion_whenIsMet_thenUsesInitialTimeValue() {
        // Given
        int modulo = 5;
        int remainder = 0;
        ModuloTimeCriterion criterion = new ModuloTimeCriterion(modulo, remainder);
        
        // When 
        boolean result = criterion.isMet();
        
        // Then - at time 0, 0 % 5 == 0, so criterion is met
        assertThat(result).isTrue();
    }
    
    @Test
    public void givenCriterion_whenUpdated_thenTimeIncreases() {
        // Given
        int modulo = 5;
        int remainder = 0;
        ModuloTimeCriterion criterion = new ModuloTimeCriterion(modulo, remainder);
        
        // When
        criterion.update(Optional.empty());
        
        // Then - at time 1, 1 % 5 != 0, so criterion is not met
        assertThat(criterion.isMet()).isFalse();
        assertThat(criterion.getCurrentTime()).isEqualTo(1);
        
        // When updated to time 5
        for (int i = 0; i < 4; i++) {
            criterion.update(Optional.empty());
        }
        
        // Then - at time 5, 5 % 5 == 0, so criterion is met again
        assertThat(criterion.isMet()).isTrue();
        assertThat(criterion.getCurrentTime()).isEqualTo(5);
    }
    
    @Test
    public void givenDifferentRemainder_whenUpdated_thenCorrectlyDetectsMet() {
        // Given - criterion that's met when time % 5 == 3
        int modulo = 5;
        int remainder = 3;
        ModuloTimeCriterion criterion = new ModuloTimeCriterion(modulo, remainder);
        
        // When - update to time 3
        for (int i = 0; i < 3; i++) {
            criterion.update(Optional.empty());
        }
        
        // Then - at time 3, 3 % 5 == 3, so criterion is met
        assertThat(criterion.isMet()).isTrue();
        
        // When - update to time 4
        criterion.update(Optional.empty());
        
        // Then - at time 4, 4 % 5 != 3, so criterion is not met
        assertThat(criterion.isMet()).isFalse();
        
        // When - update to time 8
        for (int i = 0; i < 4; i++) {
            criterion.update(Optional.empty());
        }
        
        // Then - at time 8, 8 % 5 == 3, so criterion is met again
        assertThat(criterion.isMet()).isTrue();
    }
    
    @Test
    public void givenInvalidModulo_whenConstructing_thenThrowsException() {
        // Given/When/Then
        assertThatThrownBy(() -> new ModuloTimeCriterion(0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Modulo must be positive");
        
        assertThatThrownBy(() -> new ModuloTimeCriterion(-5, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Modulo must be positive");
    }
    
    @Test
    public void givenInvalidRemainder_whenConstructing_thenThrowsException() {
        // Given/When/Then
        assertThatThrownBy(() -> new ModuloTimeCriterion(5, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Remainder must be positive");
        
    }
    
    @Test
    public void givenCriterion_whenGettersCalled_thenReturnCorrectValues() {
        // Given
        int modulo = 5;
        int remainder = 3;
        ModuloTimeCriterion criterion = new ModuloTimeCriterion(modulo, remainder);
        
        // When/Then
        assertThat(criterion.getModulo()).isEqualTo(modulo);
        assertThat(criterion.getRemainder()).isEqualTo(remainder);
        assertThat(criterion.getCurrentTime()).isEqualTo(0);
    }
}
