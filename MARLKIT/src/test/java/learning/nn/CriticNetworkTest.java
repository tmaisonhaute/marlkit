package learning.nn;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Random;

import org.testng.annotations.Test;

import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import util.Tuple;

public class CriticNetworkTest {

    @Test
    public void givenObservation_whenGetValue_thenValueIsReturned() {
        // Given
    	Random random = new Random(12345);
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(false);
        CriticNetwork critic = new CriticNetwork(4, 10, random, 0.01, wrapper);
        
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
    	Random random = new Random(12345);
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(false);
        CriticNetwork critic = new CriticNetwork(4, 10, random, 0.01, wrapper);
        
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
    	Random random = new Random(12345);
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(false);
        CriticNetwork critic = new CriticNetwork(4, 10, random, 0.001, wrapper);
        
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
        Random random = new Random(12345);
        WrapperVectorObservationPositionsValues wrapper = new WrapperVectorObservationPositionsValues(false);
        CriticNetwork critic = new CriticNetwork(4, 10, random, 0.01, wrapper);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        double targetValue = 1.0;
        double initialValue = critic.getValue(observation);
        
        // When: update with initial learning rate
        critic.update(observation, targetValue);
        
        double valueAfterInitialLR = critic.getValue(observation);
        double initialChange = Math.abs(valueAfterInitialLR - initialValue);
        
        // Change learning rate to higher value
        CriticNetwork critic2 = new CriticNetwork(4, 10, random, 0.1, wrapper);
        
        // Update with new learning rate
        critic2.update(observation, targetValue);
        
        double valueAfterHigherLR = critic2.getValue(observation);
        double changeWithHigherLR = Math.abs(valueAfterHigherLR - valueAfterInitialLR);
        
        // Then
        assertThat(changeWithHigherLR).isGreaterThan(initialChange);
    }
}
