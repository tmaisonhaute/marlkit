package util.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.testng.annotations.Test;

import environment.state.State;

public class CriteriaTest {
    
    @Test
    public void givenAndCriteriaWithAllMet_whenIsMet_thenReturnsTrue() {
        // Given
        Criterion criterion1 = mock(Criterion.class);
        Criterion criterion2 = mock(Criterion.class);
        when(criterion1.isMet()).thenReturn(true);
        when(criterion2.isMet()).thenReturn(true);
        
        Criteria criteria = Criteria.and(criterion1, criterion2);
        
        // When
        boolean result = criteria.isMet();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    public void givenAndCriteriaWithOneFalse_whenIsMet_thenReturnsFalse() {
        // Given
        Criterion criterion1 = mock(Criterion.class);
        Criterion criterion2 = mock(Criterion.class);
        when(criterion1.isMet()).thenReturn(true);
        when(criterion2.isMet()).thenReturn(false);
        
        Criteria criteria = Criteria.and(criterion1, criterion2);
        
        // When
        boolean result = criteria.isMet();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    public void givenOrCriteriaWithAllFalse_whenIsMet_thenReturnsFalse() {
        // Given
        Criterion criterion1 = mock(Criterion.class);
        Criterion criterion2 = mock(Criterion.class);
        when(criterion1.isMet()).thenReturn(false);
        when(criterion2.isMet()).thenReturn(false);
        
        Criteria criteria = Criteria.or(criterion1, criterion2);
        
        // When
        boolean result = criteria.isMet();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    public void givenOrCriteriaWithOneTrue_whenIsMet_thenReturnsTrue() {
        // Given
        Criterion criterion1 = mock(Criterion.class);
        Criterion criterion2 = mock(Criterion.class);
        when(criterion1.isMet()).thenReturn(true);
        when(criterion2.isMet()).thenReturn(false);
        
        Criteria criteria = Criteria.or(criterion1, criterion2);
        
        // When
        boolean result = criteria.isMet();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    public void givenNotCriteriaWithFalse_whenIsMet_thenReturnsTrue() {
        // Given
        Criterion criterion = mock(Criterion.class);
        when(criterion.isMet()).thenReturn(false);
        
        Criteria criteria = Criteria.not(criterion);
        
        // When
        boolean result = criteria.isMet();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    public void givenNotCriteriaWithTrue_whenIsMet_thenReturnsFalse() {
        // Given
        Criterion criterion = mock(Criterion.class);
        when(criterion.isMet()).thenReturn(true);
        
        Criteria criteria = Criteria.not(criterion);
        
        // When
        boolean result = criteria.isMet();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    public void givenEmptyAndCriteria_whenIsMet_thenReturnsTrue() {
        // Given
        Criteria criteria = Criteria.and();
        
        // When
        boolean result = criteria.isMet();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    public void givenEmptyOrCriteria_whenIsMet_thenReturnsFalse() {
        // Given
        Criteria criteria = Criteria.or();
        
        // When
        boolean result = criteria.isMet();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    public void givenCriteria_whenUpdate_thenAllChildrenUpdated() {
        // Given
        State state = mock(State.class);
        Optional<State> optionalState = Optional.of(state);
        
        Criterion criterion1 = mock(Criterion.class);
        Criterion criterion2 = mock(Criterion.class);
        
        Criteria criteria = Criteria.and(criterion1, criterion2);
        
        // When
        criteria.update(optionalState);
        
        // Then
        verify(criterion1).update(optionalState);
        verify(criterion2).update(optionalState);
    }
    
    @Test
    public void givenNestedCriteria_whenIsMet_thenCorrectlyEvaluates() {
        // Given
        Criterion criterion1 = mock(Criterion.class);
        Criterion criterion2 = mock(Criterion.class);
        Criterion criterion3 = mock(Criterion.class);
        
        when(criterion1.isMet()).thenReturn(true);
        when(criterion2.isMet()).thenReturn(false);
        when(criterion3.isMet()).thenReturn(true);
        
        // (true AND false) OR (NOT true) = false OR false = false
        Criteria andCriteria = Criteria.and(criterion1, criterion2);
        Criteria notCriteria = Criteria.not(criterion3);
        Criteria orCriteria = Criteria.or(andCriteria, notCriteria);
        
        // When
        boolean result = orCriteria.isMet();
        
        // Then
        assertThat(result).isFalse();
    }
}
