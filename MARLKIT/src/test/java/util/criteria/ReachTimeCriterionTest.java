package util.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.testng.annotations.Test;

import environment.state.State;
public class ReachTimeCriterionTest {

    @Test
    public void givenNewCriterion_whenIsMet_thenReturnsFalse() {
        // Given
        int threshold = 10;
        ReachTimeCriterion criterion = new ReachTimeCriterion(threshold);
        
        // When
        boolean result = criterion.isMet();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    public void givenCriterionBelowThreshold_whenUpdatedAndIsMet_thenReturnsFalse() {
        // Given
        int threshold = 10;
        ReachTimeCriterion criterion = new ReachTimeCriterion(threshold);
        State mockState = mock(State.class);
        Optional<State> optionalState = Optional.of(mockState);
        
        // When: update 5 times (less than threshold)
        for (int i = 0; i < 5; i++) {
            criterion.update(optionalState);
        }
        boolean result = criterion.isMet();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    public void givenCriterionAtThreshold_whenUpdatedAndIsMet_thenReturnsTrue() {
        // Given
        int threshold = 10;
        ReachTimeCriterion criterion = new ReachTimeCriterion(threshold);
        
        // When: update exactly threshold times
        for (int i = 0; i < threshold; i++) {
            criterion.update(Optional.empty());
        }
        boolean result = criterion.isMet();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    public void givenCriterionAboveThreshold_whenUpdatedAndIsMet_thenReturnsTrue() {
        // Given
        int threshold = 10;
        ReachTimeCriterion criterion = new ReachTimeCriterion(threshold);
        
        // When: update more than threshold times
        for (int i = 0; i < threshold + 5; i++) {
            criterion.update(Optional.empty());
        }
        boolean result = criterion.isMet();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    public void givenCriterion_whenThresholdChanged_thenBehaviorChanges() {
        // Given
        int initialThreshold = 10;
        ReachTimeCriterion criterion = new ReachTimeCriterion(initialThreshold);
        
        // When: update 5 times then change threshold to 5
        for (int i = 0; i < 5; i++) {
            criterion.update(Optional.empty());
        }
        boolean resultBeforeChange = criterion.isMet();
        
        criterion.setThreshold(5);
        boolean resultAfterChange = criterion.isMet();
        
        // Then
        assertThat(resultBeforeChange).isFalse(); // 5 < 10, not met
        assertThat(resultAfterChange).isTrue();   // 5 >= 5, met
    }
    
    @Test
    public void givenCriterion_whenGetThreshold_thenReturnsCorrectValue() {
        // Given
        int threshold = 10;
        ReachTimeCriterion criterion = new ReachTimeCriterion(threshold);
        
        // When
        int result = criterion.getThreshold();
        
        // Then
        assertThat(result).isEqualTo(threshold);
    }
}
