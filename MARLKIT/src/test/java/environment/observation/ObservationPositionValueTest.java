package environment.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.Arrays;

import org.testng.annotations.Test;

import util.Tuple;

public class ObservationPositionValueTest {

    @Test
    public void givenTwoObservationPositionValues_whenAdd_thenAverageThem() {
        // Given
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(2.0, 4.0)), 10.0);
            
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(4.0, 8.0)), 20.0);
        
        // When
        Observation result = obs1.add(obs2);
        
        // Then
        assertThat(result).isInstanceOf(ObservationPositionValue.class);
        ObservationPositionValue combinedObs = (ObservationPositionValue) result;
        
        // Position should be the average of the two positions
        assertThat(combinedObs.getPosition().getValue(0)).isEqualTo(3.0);
        assertThat(combinedObs.getPosition().getValue(1)).isEqualTo(6.0);
        
        // Value should be the average of the two values
        assertThat(combinedObs.getValue()).isEqualTo(15.0);
    }
    
    @Test
    public void givenNegativeValues_whenAdd_thenCorrectlyAverageThem() {
        // Given
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(-2.0, -4.0)), -10.0);
            
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(4.0, 8.0)), 20.0);
        
        // When
        Observation result = obs1.add(obs2);
        
        // Then
        assertThat(result).isInstanceOf(ObservationPositionValue.class);
        ObservationPositionValue combinedObs = (ObservationPositionValue) result;
        
        // Position should be the average of the two positions
        assertThat(combinedObs.getPosition().getValue(0)).isEqualTo(1.0);
        assertThat(combinedObs.getPosition().getValue(1)).isEqualTo(2.0);
        
        // Value should be the average of the two values
        assertThat(combinedObs.getValue()).isEqualTo(5.0);
    }
    
    @Test
    public void givenZeroValues_whenAdd_thenResultIsZero() {
        // Given
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 0.0);
            
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 0.0);
        
        // When
        Observation result = obs1.add(obs2);
        
        // Then
        assertThat(result).isInstanceOf(ObservationPositionValue.class);
        ObservationPositionValue combinedObs = (ObservationPositionValue) result;
        
        // Position should be zeros
        assertThat(combinedObs.getPosition().getValue(0)).isEqualTo(0.0);
        assertThat(combinedObs.getPosition().getValue(1)).isEqualTo(0.0);
        
        // Value should be zero
        assertThat(combinedObs.getValue()).isEqualTo(0.0);
    }
    
    @Test
    public void givenDifferentSizedPositions_whenAdd_thenStillCalculateAverage() {
        // Given
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(2.0, 4.0, 6.0)), 10.0);
            
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(4.0, 8.0, 12.0)), 20.0);
        
        // When
        Observation result = obs1.add(obs2);
        
        // Then
        assertThat(result).isInstanceOf(ObservationPositionValue.class);
        ObservationPositionValue combinedObs = (ObservationPositionValue) result;
        
        // Position should be the average of the two positions
        assertThat(combinedObs.getPosition().getValue(0)).isEqualTo(3.0);
        assertThat(combinedObs.getPosition().getValue(1)).isEqualTo(6.0);
        assertThat(combinedObs.getPosition().getValue(2)).isEqualTo(9.0);
        
        // Value should be the average of the two values
        assertThat(combinedObs.getValue()).isEqualTo(15.0);
    }
    
    @Test
    public void givenObservationPositionValueAndDifferentObservation_whenAdd_thenThrowException() {
        // Given
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(2.0, 4.0)), 10.0);
        
        // Create a mock Observation that is not ObservationPositionValue
        Observation differentObservation = new Observation() {
            @Override
            public Observation add(Observation other) {
                return null;
            }
        };
        
        // When/Then
        assertThatThrownBy(() -> obs1.add(differentObservation))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Impossible to add a ObservationPositionValue element with an element which isn't.");
    }
}
