package agent.interaction;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import agent.interaction.wrapper.WapperActionProbabilities;
import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import util.Tuple;

public class PredictionsTest {

    private Predictions predictions;
    private MLKAgent agent1;
    private MLKAgent agent2;
    private Observation observation1;
    private Observation observation2;
    private Action action1;
    private Action action2;
    private WapperActionProbabilities mockWrapper;
    
    @BeforeMethod
    public void setUp() {
        predictions = new Predictions();
        
        // Create mock agents
        agent1 = mock(MLKAgent.class);
        agent2 = mock(MLKAgent.class);
        
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
        
        // Create mock wrapper
        mockWrapper = mock(WapperActionProbabilities.class);
    }
    
    @Test
    public void testAddAgent() {
        // When
        predictions.addAgent(agent1);
        
        // Then
        Set<MLKAgent> trackedAgents = predictions.getTrackedAgents();
        assertThat(trackedAgents).hasSize(1);
        assertThat(trackedAgents).contains(agent1);
    }
    
    @Test
    public void testRecordObservationAction() {
        // When
        predictions.recordObservationAction(agent1, observation1, action1);
        
        // Then
        Prediction prediction = predictions.getPrediction(agent1);
        assertThat(prediction).isNotNull();
        
        Map<Action, Double> probabilities = prediction.predictActionProbabilities(observation1);
        assertThat(probabilities.get(action1)).isEqualTo(1.0);
    }
    
    @Test
    public void testPredictActionProbabilities() {
        // Given
        predictions.recordObservationAction(agent1, observation1, action1);
        predictions.recordObservationAction(agent1, observation1, action2);
        predictions.recordObservationAction(agent1, observation1, action1);
        
        // When
        Map<Action, Double> probabilities = predictions.predictActionProbabilities(agent1, observation1);
        
        // Then
        assertThat(probabilities).hasSize(2);
        assertThat(probabilities.get(action1)).isEqualTo(2.0/3.0);
        assertThat(probabilities.get(action2)).isEqualTo(1.0/3.0);
    }
    
    @Test
    public void testPredictActionProbabilitiesForUnknownAgent() {
        // Given - no observations recorded for agent2
        
        // When
        Map<Action, Double> probabilities = predictions.predictActionProbabilities(agent2, observation1);
        
        // Then
        assertThat(probabilities).isEmpty();
    }
    
    @Test
    public void testGetPredictionsAsObservation() {
        // Given
        predictions.addAgent(agent1);
        predictions.recordObservationAction(agent1, observation1, action1);
        
        ObservationPositionsValues mockObservation = new ObservationPositionsValues();
        mockObservation.addObservation(
            new ObservationPositionValue(new Tuple(Arrays.asList(5.0, 6.0)), 3.0));
            
        when(mockWrapper.transform(org.mockito.Mockito.eq(agent1), 
                                  org.mockito.ArgumentMatchers.anyMap()))
            .thenReturn(mockObservation);
        
        // When
        Observation result = predictions.getPredictionsAsObservation(observation1, mockWrapper);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ObservationPositionsValues.class);
    }
    
    @Test
    public void testGetTrackedAgents() {
        // Given
        predictions.addAgent(agent1);
        predictions.addAgent(agent2);
        
        // When
        Set<MLKAgent> trackedAgents = predictions.getTrackedAgents();
        
        // Then
        assertThat(trackedAgents).hasSize(2);
        assertThat(trackedAgents).contains(agent1, agent2);
    }
    
    @Test
    public void testGetPrediction() {
        // Given
        predictions.addAgent(agent1);
        predictions.recordObservationAction(agent1, observation1, action1);
        
        // When
        Prediction prediction = predictions.getPrediction(agent1);
        
        // Then
        assertThat(prediction).isNotNull();
        assertThat(prediction.getObservedActions()).contains(action1);
    }
    
    @Test
    public void testMultipleAgentPredictions() {
        // Given
        predictions.recordObservationAction(agent1, observation1, action1);
        predictions.recordObservationAction(agent2, observation1, action2);
        
        // When
        Map<Action, Double> probabilities1 = predictions.predictActionProbabilities(agent1, observation1);
        Map<Action, Double> probabilities2 = predictions.predictActionProbabilities(agent2, observation1);
        
        // Then
        assertThat(probabilities1.get(action1)).isEqualTo(1.0);
        assertThat(probabilities2.get(action2)).isEqualTo(1.0);
    }
    
    @Test
    public void testGetPredictionsAsObservationWithNoAgents() {
        // Given - no agents added
        
        // When
        Observation result = predictions.getPredictionsAsObservation(observation1, mockWrapper);
        
        // Then
        assertThat(result).isNull();
    }
}
