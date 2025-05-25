package learning.nn;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.observation.wrapperobservationvector.WrapperObservationVectorPositionsValues;
import util.Tuple;

public class CriticNetworkTest {

    @Test
    public void givenObservation_whenGetValue_thenValueIsReturned() {
        // Given
        WrapperObservationVectorPositionsValues wrapper = new WrapperObservationVectorPositionsValues(false);
        CriticNetwork critic = new CriticNetwork(4, 10, 0.01, wrapper);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        // When
        double value = critic.getValue(observation);
        
        // Then
        assertThat(value).isNotNull();
    }
    
    @Test
    public void givenObservationAndTarget_whenUpdate_thenErrorIsReturned() {
        // Given
        WrapperObservationVectorPositionsValues wrapper = new WrapperObservationVectorPositionsValues(false);
        CriticNetwork critic = new CriticNetwork(4, 10, 0.01, wrapper);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        double targetValue = 1.0;
        
        // When
        double tdError = critic.update(observation, targetValue);
        
        // Then
        assertThat(tdError).isNotNull();
    }
    
    @Test
    public void givenMultipleUpdates_whenGetValue_thenValueConvergesToTarget() {
        // Given
        WrapperObservationVectorPositionsValues wrapper = new WrapperObservationVectorPositionsValues(false);
        CriticNetwork critic = new CriticNetwork(4, 10, 0.001, wrapper);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        double targetValue = 1.0;
        double initialValue = critic.getValue(observation);
        double initialError = Math.abs(targetValue - initialValue);
        
        // When
        for (int i = 0; i < 100; i++) {
            critic.update(observation, targetValue);
        }
        double finalValue = critic.getValue(observation);
        double finalError = Math.abs(targetValue - finalValue);
        
        // Then
        assertThat(finalError).isLessThan(initialError);
    }
    
    @Test
    public void givenLearningRateChange_whenUpdate_thenLearningSpeedChanges() {
        // Given
        WrapperObservationVectorPositionsValues wrapper = new WrapperObservationVectorPositionsValues(false);
        CriticNetwork critic = new CriticNetwork(4, 10, 0.01, wrapper);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        double targetValue = 1.0;
        double initialValue = critic.getValue(observation);
        
        // When: update with initial learning rate
        for (int i = 0; i < 10; i++) {
            critic.update(observation, targetValue);
        }
        double valueAfterInitialLR = critic.getValue(observation);
        double initialChange = Math.abs(valueAfterInitialLR - initialValue);
        
        // Change learning rate to higher value
        critic.setLearningRate(0.1);
        
        // Update with new learning rate
        for (int i = 0; i < 10; i++) {
            critic.update(observation, targetValue);
        }
        double valueAfterHigherLR = critic.getValue(observation);
        double changeWithHigherLR = Math.abs(valueAfterHigherLR - valueAfterInitialLR);
        
        // Then
        assertThat(changeWithHigherLR).isGreaterThan(initialChange);
    }
}
