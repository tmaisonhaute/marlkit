package agent.interaction;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import agent.action.Action;
import agent.action.Action2DMove;
import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import util.Tuple;

public class PredictionTest {

    private Prediction prediction;
    private Observation observation1;
    private Observation observation2;
    private Action action1;
    private Action action2;
    
    @BeforeMethod
    public void setUp() {
        prediction = new Prediction();
        
        // Create test observations
        observation1 = new ObservationPositionsValues();
        ((ObservationPositionsValues) observation1).addObservation(
            new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
            
        observation2 = new ObservationPositionsValues();
        ((ObservationPositionsValues) observation2).addObservation(
            new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 2.0));
        
        // Create test actions
        action1 = Action2DMove.up();
        action2 = Action2DMove.right();
    }
    
    @Test
    public void testRecordObservationAction() {
        // When
        prediction.recordObservationAction(observation1, action1);
        
        // Then
        Set<Action> observedActions = prediction.getObservedActions();
        assertThat(observedActions).hasSize(1);
        assertThat(observedActions).contains(action1);
    }
    
    @Test
    public void testPredictActionProbabilities_KnownObservation() {
        // Given
        prediction.recordObservationAction(observation1, action1);
        prediction.recordObservationAction(observation1, action1);
        prediction.recordObservationAction(observation1, action2);
        
        // When
        Map<Action, Double> probabilities = prediction.predictActionProbabilities(observation1);
        
        // Then
        assertThat(probabilities).hasSize(2);
        assertThat(probabilities.get(action1)).isEqualTo(2.0/3.0);
        assertThat(probabilities.get(action2)).isEqualTo(1.0/3.0);
    }
    
    @Test
    public void testPredictActionProbabilities_UnknownObservation() {
        // Given
        prediction.recordObservationAction(observation1, action1);
        prediction.recordObservationAction(observation1, action2);
        
        // When - predict for an observation we haven't seen before
        Map<Action, Double> probabilities = prediction.predictActionProbabilities(observation2);
        
        // Then - should return uniform distribution across observed actions
        assertThat(probabilities).hasSize(2);
        assertThat(probabilities.get(action1)).isEqualTo(0.5);
        assertThat(probabilities.get(action2)).isEqualTo(0.5);
    }
    
    @Test
    public void testGetObservedActions() {
        // Given
        prediction.recordObservationAction(observation1, action1);
        prediction.recordObservationAction(observation2, action2);
        
        // When
        Set<Action> observedActions = prediction.getObservedActions();
        
        // Then
        assertThat(observedActions).hasSize(2);
        assertThat(observedActions).contains(action1, action2);
    }
    
    @Test
    public void testMultipleObservationsSameAction() {
        // Given
        prediction.recordObservationAction(observation1, action1);
        prediction.recordObservationAction(observation2, action1);
        
        // When
        Map<Action, Double> probabilities1 = prediction.predictActionProbabilities(observation1);
        Map<Action, Double> probabilities2 = prediction.predictActionProbabilities(observation2);
        
        // Then
        assertThat(probabilities1.get(action1)).isEqualTo(1.0);
        assertThat(probabilities2.get(action1)).isEqualTo(1.0);
        assertThat(prediction.getObservedActions()).hasSize(1);
    }
    
    @Test
    public void testSameObservationDifferentActions() {
        // Given
        prediction.recordObservationAction(observation1, action1);
        prediction.recordObservationAction(observation1, action2);
        prediction.recordObservationAction(observation1, action1);
        prediction.recordObservationAction(observation1, action1);
        
        // When
        Map<Action, Double> probabilities = prediction.predictActionProbabilities(observation1);
        
        // Then
        assertThat(probabilities).hasSize(2);
        assertThat(probabilities.get(action1)).isEqualTo(0.75); // 3/4
        assertThat(probabilities.get(action2)).isEqualTo(0.25); // 1/4
    }
}
