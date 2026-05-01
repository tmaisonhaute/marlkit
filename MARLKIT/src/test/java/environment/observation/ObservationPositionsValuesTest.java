package environment.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

import org.testng.annotations.Test;

import util.Tuple;

public class ObservationPositionsValuesTest {

    @Test
    public void givenTwoObservationPositionsValues_whenAdd_thenCombineObservations() {
        // Given
        ObservationPositionsValues obs1 = new ObservationPositionsValues();
        obs1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        obs1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 2.0));
        
        ObservationPositionsValues obs2 = new ObservationPositionsValues();
        obs2.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(5.0, 6.0)), 3.0));
        obs2.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(7.0, 8.0)), 4.0));
        
        // When
        Observation result = obs1.add(obs2);
        
        // Then
        assertThat(result).isInstanceOf(ObservationPositionsValues.class);
        ObservationPositionsValues combinedObs = (ObservationPositionsValues) result;
        
        // Check the combined observation has all observations from both inputs
        assertThat(combinedObs.getListObs()).hasSize(4);
        
        // Check the first two observations from obs1
        assertThat(combinedObs.getObs(0).getPosition().getValue(0)).isEqualTo(1.0);
        assertThat(combinedObs.getObs(0).getPosition().getValue(1)).isEqualTo(2.0);
        assertThat(combinedObs.getObs(0).getValue()).isEqualTo(1.0);
        
        assertThat(combinedObs.getObs(1).getPosition().getValue(0)).isEqualTo(3.0);
        assertThat(combinedObs.getObs(1).getPosition().getValue(1)).isEqualTo(4.0);
        assertThat(combinedObs.getObs(1).getValue()).isEqualTo(2.0);
        
        // Check the next two observations from obs2
        assertThat(combinedObs.getObs(2).getPosition().getValue(0)).isEqualTo(5.0);
        assertThat(combinedObs.getObs(2).getPosition().getValue(1)).isEqualTo(6.0);
        assertThat(combinedObs.getObs(2).getValue()).isEqualTo(3.0);
        
        assertThat(combinedObs.getObs(3).getPosition().getValue(0)).isEqualTo(7.0);
        assertThat(combinedObs.getObs(3).getPosition().getValue(1)).isEqualTo(8.0);
        assertThat(combinedObs.getObs(3).getValue()).isEqualTo(4.0);
    }
    
    @Test
    public void givenObservationPositionsValuesAndDifferentObservation_whenAdd_thenThrowException() {
        // Given
        ObservationPositionsValues obs1 = new ObservationPositionsValues();
        obs1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        
        // Create a mock Observation that is not ObservationPositionsValues
        Observation differentObservation = new Observation() {
            @Override
            public Observation add(Observation other) {
                return null;
            }

			@Override
			public Observation copy() {
				// TODO Auto-generated method stub
				return null;
			}
        };
        
        // When/Then
        assertThatThrownBy(() -> obs1.add(differentObservation))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Impossible to add a ObservationPositionsValues element with an element which isn't.");
    }
    
    @Test
    public void givenEmptyObservations_whenAdd_thenResultIsEmpty() {
        // Given
        ObservationPositionsValues obs1 = new ObservationPositionsValues();
        ObservationPositionsValues obs2 = new ObservationPositionsValues();
        
        // When
        Observation result = obs1.add(obs2);
        
        // Then
        assertThat(result).isInstanceOf(ObservationPositionsValues.class);
        ObservationPositionsValues combinedObs = (ObservationPositionsValues) result;
        assertThat(combinedObs.getListObs()).isEmpty();
    }
    
    @Test
    public void givenOneEmptyObservation_whenAdd_thenResultHasOnlyNonEmptyValues() {
        // Given
        ObservationPositionsValues obs1 = new ObservationPositionsValues();
        obs1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        
        ObservationPositionsValues obs2 = new ObservationPositionsValues();
        
        // When
        Observation result1 = obs1.add(obs2);
        Observation result2 = obs2.add(obs1);
        
        // Then
        assertThat(result1).isInstanceOf(ObservationPositionsValues.class);
        ObservationPositionsValues combinedObs1 = (ObservationPositionsValues) result1;
        assertThat(combinedObs1.getListObs()).hasSize(1);
        
        assertThat(result2).isInstanceOf(ObservationPositionsValues.class);
        ObservationPositionsValues combinedObs2 = (ObservationPositionsValues) result2;
        assertThat(combinedObs2.getListObs()).hasSize(1);
    }
}
