package environment.observation.wrapperobservationvector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.testng.annotations.Test;

import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import util.Tuple;

public class WrapperObservationVectorPositionsValuesTest {

    @Test
    public void givenObservationWithoutValue_whenTransformToVector_thenCorrectVectorReturned() {
        // Given
        ObservationPositionsValues obs = new ObservationPositionsValues();
        obs.addObservation(new ObservationPositionValue(new Tuple(List.of(1.0, 2.0)), 5.0));
        obs.addObservation(new ObservationPositionValue(new Tuple(List.of(3.0, 4.0)), 6.0));
        
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(false);
        
        // When
        double[] vector = wrapper.transform(obs);
        
        // Then
        assertThat(vector).hasSize(4); // 2 observations x 2 position values
        assertThat(vector[0]).isEqualTo(1.0);
        assertThat(vector[1]).isEqualTo(2.0);
        assertThat(vector[2]).isEqualTo(3.0);
        assertThat(vector[3]).isEqualTo(4.0);
    }
    
    @Test
    public void givenObservationWithValue_whenTransformToVector_thenCorrectVectorReturned() {
        // Given
        ObservationPositionsValues obs = new ObservationPositionsValues();
        obs.addObservation(new ObservationPositionValue(new Tuple(List.of(1.0, 2.0)), 5.0));
        obs.addObservation(new ObservationPositionValue(new Tuple(List.of(3.0, 4.0)), 6.0));
        
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(true);
        
        // When
        double[] vector = wrapper.transform(obs);
        
        // Then
        assertThat(vector).hasSize(6); // 2 observations x 3 values (x, y, value)
        assertThat(vector[0]).isEqualTo(1.0);
        assertThat(vector[1]).isEqualTo(2.0);
        assertThat(vector[2]).isEqualTo(5.0);
        assertThat(vector[3]).isEqualTo(3.0);
        assertThat(vector[4]).isEqualTo(4.0);
        assertThat(vector[5]).isEqualTo(6.0);
    }
    
    @Test
    public void givenVectorWithoutValue_whenTransformToObservation_thenCorrectObservationReturned() {
        // Given
        double[] vector = {1.0, 2.0, 3.0, 4.0};
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(false);
        
        // When
        Observation observation = wrapper.transform(vector);
        
        // Then
        assertThat(observation).isInstanceOf(ObservationPositionsValues.class);
        ObservationPositionsValues obs = (ObservationPositionsValues) observation;
        assertThat(obs.getListObs()).hasSize(2); // Should create 2 observations
        
        // First observation
        ObservationPositionValue firstObs = obs.getObs(0);
        assertThat(firstObs.getPosition().getValue(0)).isEqualTo(1.0);
        assertThat(firstObs.getPosition().getValue(1)).isEqualTo(2.0);
        assertThat(firstObs.getValue()).isEqualTo(1.0); // Default value when mustTranslateValue is false
        
        // Second observation
        ObservationPositionValue secondObs = obs.getObs(1);
        assertThat(secondObs.getPosition().getValue(0)).isEqualTo(3.0);
        assertThat(secondObs.getPosition().getValue(1)).isEqualTo(4.0);
        assertThat(secondObs.getValue()).isEqualTo(1.0); // Default value when mustTranslateValue is false
    }
    
    @Test
    public void givenVectorWithValue_whenTransformToObservation_thenCorrectObservationReturned() {
        // Given
        double[] vector = {1.0, 2.0, 5.0, 3.0, 4.0, 6.0};
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(true);
        
        // When
        Observation observation = wrapper.transform(vector);
        
        // Then
        assertThat(observation).isInstanceOf(ObservationPositionsValues.class);
        ObservationPositionsValues obs = (ObservationPositionsValues) observation;
        assertThat(obs.getListObs()).hasSize(2); // Should create 2 observations
        
        // First observation
        ObservationPositionValue firstObs = obs.getObs(0);
        assertThat(firstObs.getPosition().getValue(0)).isEqualTo(1.0);
        assertThat(firstObs.getPosition().getValue(1)).isEqualTo(2.0);
        assertThat(firstObs.getValue()).isEqualTo(5.0);
        
        // Second observation
        ObservationPositionValue secondObs = obs.getObs(1);
        assertThat(secondObs.getPosition().getValue(0)).isEqualTo(3.0);
        assertThat(secondObs.getPosition().getValue(1)).isEqualTo(4.0);
        assertThat(secondObs.getValue()).isEqualTo(6.0);
    }
    
    @Test
    public void givenInvalidObservation_whenTransformToVector_thenExceptionThrown() {
        // Given
        ObservationPositionsValues obs = new ObservationPositionsValues();
        obs.addObservation(new ObservationPositionValue(new Tuple(List.of(1.0, 2.0, 3.0)), 5.0)); // Position with 3 values
        
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(false);
        
        // When/Then
        assertThatThrownBy(() -> wrapper.transform(obs))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ObservationPositionValue must have a position of size 2");
    }
}
