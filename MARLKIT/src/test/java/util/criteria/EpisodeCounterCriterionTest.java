package util.criteria;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.testng.annotations.Test;

import environment.state.State;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EpisodeCounterCriterionTest {

    @Test
    public void givenNewCriterion_whenIsMet_thenReturnsFalse() {
        // Given
        int targetEpisodes = 10;
        EpisodeCounterCriterion criterion = new EpisodeCounterCriterion(targetEpisodes);
        
        // When
        boolean result = criterion.isMet();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    public void givenCriterionBelowTarget_whenUpdatedWithTerminalStatesAndIsMet_thenReturnsFalse() {
        // Given
        int targetEpisodes = 10;
        EpisodeCounterCriterion criterion = new EpisodeCounterCriterion(targetEpisodes);
        State mockState = mock(State.class);
        when(mockState.isTerminal()).thenReturn(true);
        Optional<State> terminalState = Optional.of(mockState);
        
        // When: update 5 times with terminal states (less than target)
        for (int i = 0; i < 5; i++) {
            criterion.update(terminalState);
        }
        boolean result = criterion.isMet();
        
        // Then
        assertThat(result).isFalse();
        assertThat(criterion.getCurrentEpisodes()).isEqualTo(5);
    }
    
    @Test
    public void givenCriterionAtTarget_whenUpdatedWithTerminalStatesAndIsMet_thenReturnsTrue() {
        // Given
        int targetEpisodes = 10;
        EpisodeCounterCriterion criterion = new EpisodeCounterCriterion(targetEpisodes);
        State mockState = mock(State.class);
        when(mockState.isTerminal()).thenReturn(true);
        Optional<State> terminalState = Optional.of(mockState);
        
        // When: update exactly target times with terminal states
        for (int i = 0; i < targetEpisodes; i++) {
            criterion.update(terminalState);
        }
        boolean result = criterion.isMet();
        
        // Then
        assertThat(result).isTrue();
        assertThat(criterion.getCurrentEpisodes()).isEqualTo(targetEpisodes);
    }
    
    @Test
    public void givenCriterionAboveTarget_whenUpdatedWithTerminalStatesAndIsMet_thenReturnsTrue() {
        // Given
        int targetEpisodes = 10;
        EpisodeCounterCriterion criterion = new EpisodeCounterCriterion(targetEpisodes);
        State mockState = mock(State.class);
        when(mockState.isTerminal()).thenReturn(true);
        Optional<State> terminalState = Optional.of(mockState);
        
        // When: update more than target times with terminal states
        for (int i = 0; i < targetEpisodes + 5; i++) {
            criterion.update(terminalState);
        }
        boolean result = criterion.isMet();
        
        // Then
        assertThat(result).isTrue();
        assertThat(criterion.getCurrentEpisodes()).isEqualTo(targetEpisodes + 5);
    }
    
    @Test
    public void givenCriterion_whenUpdatedWithNonTerminalStates_thenDoesNotIncrement() {
        // Given
        int targetEpisodes = 10;
        EpisodeCounterCriterion criterion = new EpisodeCounterCriterion(targetEpisodes);
        State mockState = mock(State.class);
        when(mockState.isTerminal()).thenReturn(false);
        Optional<State> nonTerminalState = Optional.of(mockState);
        
        // When: update multiple times with non-terminal states
        for (int i = 0; i < 20; i++) {
            criterion.update(nonTerminalState);
        }
        
        // Then
        assertThat(criterion.getCurrentEpisodes()).isEqualTo(0);
        assertThat(criterion.isMet()).isFalse();
    }
    
    @Test
    public void givenCriterion_whenReset_thenCounterResets() {
        // Given
        int targetEpisodes = 10;
        EpisodeCounterCriterion criterion = new EpisodeCounterCriterion(targetEpisodes);
        State mockState = mock(State.class);
        when(mockState.isTerminal()).thenReturn(true);
        Optional<State> terminalState = Optional.of(mockState);
        
        // When: update 5 times then reset
        for (int i = 0; i < 5; i++) {
            criterion.update(terminalState);
        }
        int episodesBeforeReset = criterion.getCurrentEpisodes();
        criterion.reset();
        int episodesAfterReset = criterion.getCurrentEpisodes();
        
        // Then
        assertThat(episodesBeforeReset).isEqualTo(5);
        assertThat(episodesAfterReset).isEqualTo(0);
    }
    
    @Test
    public void givenInvalidTarget_whenConstructing_thenThrowsException() {
        // Given/When/Then
        assertThatThrownBy(() -> new EpisodeCounterCriterion(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Target episodes must be positive");
        
        assertThatThrownBy(() -> new EpisodeCounterCriterion(-5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Target episodes must be positive");
    }
    
    @Test
    public void givenCriterion_whenGetTargetEpisodes_thenReturnsCorrectValue() {
        // Given
        int targetEpisodes = 10;
        EpisodeCounterCriterion criterion = new EpisodeCounterCriterion(targetEpisodes);
        
        // When
        int result = criterion.getTargetEpisodes();
        
        // Then
        assertThat(result).isEqualTo(targetEpisodes);
    }
} 