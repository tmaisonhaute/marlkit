package util.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.testng.annotations.Test;

public class ModuloTimeGreaterThanCriterionTest {

    @Test
    public void givenNewCriterion_whenIsMet_thenReturnsFalse() {
        // Given
        int modulo = 5;
        int threshold = 2;
        ModuloTimeGreaterThanCriterion criterion = new ModuloTimeGreaterThanCriterion(modulo, threshold);
        
        // When 
        boolean result = criterion.isMet();
        
        // Then - at time 0, 0 % 5 == 0, which is NOT > 2, so criterion is not met
        assertThat(result).isFalse();
    }
    
    @Test
    public void givenCriterion_whenUpdatedToExceedThreshold_thenReturnsTrueWhenGreaterThanThreshold() {
        // Given
        int modulo = 5;
        int threshold = 2;
        ModuloTimeGreaterThanCriterion criterion = new ModuloTimeGreaterThanCriterion(modulo, threshold);
        
        // When - update to time 3
        for (int i = 0; i < 3; i++) {
            criterion.update(Optional.empty());
        }
        
        // Then - at time 3, 3 % 5 == 3, which is > 2, so criterion is met
        assertThat(criterion.isMet()).isTrue();
        assertThat(criterion.getCurrentTime()).isEqualTo(3);
        
        // When - update to time 4
        criterion.update(Optional.empty());
        
        // Then - at time 4, 4 % 5 == 4, which is > 2, so criterion is still met
        assertThat(criterion.isMet()).isTrue();
        assertThat(criterion.getCurrentTime()).isEqualTo(4);
        
        // When - update to time 5
        criterion.update(Optional.empty());
        
        // Then - at time 5, 5 % 5 == 0, which is NOT > 2, so criterion is not met
        assertThat(criterion.isMet()).isFalse();
        assertThat(criterion.getCurrentTime()).isEqualTo(5);
    }
    
    @Test
    public void givenCriterion_whenCyclingThroughFullPeriod_thenCorrectlyDetectsMetCondition() {
        // Given
        int modulo = 5;
        int threshold = 1;
        ModuloTimeGreaterThanCriterion criterion = new ModuloTimeGreaterThanCriterion(modulo, threshold);
        
        // Test a full cycle
        boolean[] expectedResults = {false, false, true, true, true, false, false, true, true, true};
        
        for (int i = 0; i < expectedResults.length; i++) {
            // When - check at current time
            boolean result = criterion.isMet();
            
            // Then - verify expected result for current time
            assertThat(result).isEqualTo(expectedResults[i]);
            
            // Update to next time
            criterion.update(Optional.empty());
        }
    }
    
    @Test
    public void givenCriterion_whenReset_thenTimeCounterResets() {
        // Given
        int modulo = 5;
        int threshold = 2;
        ModuloTimeGreaterThanCriterion criterion = new ModuloTimeGreaterThanCriterion(modulo, threshold);
        
        // When - update several times then reset
        for (int i = 0; i < 8; i++) {
            criterion.update(Optional.empty());
        }
        boolean resultBeforeReset = criterion.isMet();
        criterion.reset();
        boolean resultAfterReset = criterion.isMet();
        
        // Then
        assertThat(resultBeforeReset).isTrue(); // At time 8, 8 % 5 == 3, which is > 2
        assertThat(resultAfterReset).isFalse(); // After reset, at time 0, 0 % 5 == 0, which is NOT > 2
        assertThat(criterion.getCurrentTime()).isEqualTo(0);
    }
    
    @Test
    public void givenInvalidModulo_whenConstructing_thenThrowsException() {
        // Given/When/Then
        assertThatThrownBy(() -> new ModuloTimeGreaterThanCriterion(0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Modulo must be positive");
        
        assertThatThrownBy(() -> new ModuloTimeGreaterThanCriterion(-5, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Modulo must be positive");
    }
    
    @Test
    public void givenInvalidThreshold_whenConstructing_thenThrowsException() {
        // Given/When/Then
        assertThatThrownBy(() -> new ModuloTimeGreaterThanCriterion(5, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Threshold must be between 0 and 4");
        
        assertThatThrownBy(() -> new ModuloTimeGreaterThanCriterion(5, 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Threshold must be between 0 and 4");
    }
    
    @Test
    public void givenCriterion_whenGettersCalled_thenReturnCorrectValues() {
        // Given
        int modulo = 5;
        int threshold = 3;
        ModuloTimeGreaterThanCriterion criterion = new ModuloTimeGreaterThanCriterion(modulo, threshold);
        
        // When/Then
        assertThat(criterion.getModulo()).isEqualTo(modulo);
        assertThat(criterion.getThreshold()).isEqualTo(threshold);
        assertThat(criterion.getCurrentTime()).isEqualTo(0);
    }
}
