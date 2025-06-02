package learning.nn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import agent.action.Action;
import agent.action.Action2DMove;
import agent.action.wrapperactionvector.WrapperAction2DMoveVector;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.observation.wrapperobservationvector.WrapperObservationVectorPositionsValues;
import util.Tuple;

public class ActorNetworkTest {

    @Test
    public void givenObservation_whenSelectAction_thenActionIsReturned() {
        // Given
        WrapperObservationVectorPositionsValues observationWrapper = new WrapperObservationVectorPositionsValues(false);
        WrapperAction2DMoveVector actionWrapper = new WrapperAction2DMoveVector();
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(12345);
        
        ActorNetwork actor = new ActorNetwork(4, 10, secureRandom, 0.01, observationWrapper, actionWrapper, actionSet);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        // When: Select action with epsilon = 0 (always exploit)
        Action action = actor.selectAction(observation, 0.0);
        
        // Then
        assertThat(action).isNotNull();
        assertThat(actionSet).contains(action);
    }
    
    @Test
    public void givenEpsilonOne_whenSelectAction_thenRandomActionsSelected() {
        // Given
        WrapperObservationVectorPositionsValues observationWrapper = new WrapperObservationVectorPositionsValues(false);
        WrapperAction2DMoveVector actionWrapper = new WrapperAction2DMoveVector();
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(12345);
        
        ActorNetwork actor = new ActorNetwork(4, 10, secureRandom, 0.01, observationWrapper, actionWrapper, actionSet);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        // When: Select multiple actions with epsilon = 1.0 (always explore)
        Map<Action, Integer> actionCounts = new HashMap<>();
        int numSamples = 1000;
        
        for (int i = 0; i < numSamples; i++) {
            Action action = actor.selectAction(observation, 1.0);
            actionCounts.put(action, actionCounts.getOrDefault(action, 0) + 1);
        }
        
        // Then: All actions should have been selected approximately equally
        for (Action action : actionSet) {
            double frequency = (double) actionCounts.getOrDefault(action, 0) / numSamples;
            // With 4 actions, each should be selected ~25% of the time
            assertThat(frequency).isCloseTo(0.25, offset(0.1));
        }
    }
    
    @Test
    public void givenObservationActionAndTDError_whenUpdate_thenPolicyLearns() {
        // Given
        WrapperObservationVectorPositionsValues observationWrapper = new WrapperObservationVectorPositionsValues(false);
        WrapperAction2DMoveVector actionWrapper = new WrapperAction2DMoveVector();
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(12345);
        
        ActorNetwork actor = new ActorNetwork(4, 10, secureRandom, 0.1, observationWrapper, actionWrapper, actionSet);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        Action actionToReinforce = Action2DMove.up();
        double positiveReinforcement = 1.0;
        
        // Count initial action selection frequencies
        Map<Action, Integer> initialCounts = new HashMap<>();
        int numSamples = 100;
        
        for (int i = 0; i < numSamples; i++) {
            Action action = actor.selectAction(observation, 0.0);  // Exploitation only
            initialCounts.put(action, initialCounts.getOrDefault(action, 0) + 1);
        }
        
        // When: Repeatedly update with positive TD error for the target action
        for (int i = 0; i < 100; i++) {
            actor.update(observation, actionToReinforce, positiveReinforcement);
        }
        
        // Count action selection frequencies after learning
        Map<Action, Integer> finalCounts = new HashMap<>();
        for (int i = 0; i < numSamples; i++) {
            Action action = actor.selectAction(observation, 0.0);  // Exploitation only
            finalCounts.put(action, finalCounts.getOrDefault(action, 0) + 1);
        }
        
        // Then: The reinforced action should be selected more frequently after learning
        int initialCount = initialCounts.getOrDefault(actionToReinforce, 0);
        int finalCount = finalCounts.getOrDefault(actionToReinforce, 0);
        
        assertThat(finalCount).isGreaterThanOrEqualTo(initialCount);
    }
    
    @Test
    public void givenLearningRateChange_whenUpdate_thenLearningSpeedChanges() {
        // Given
        WrapperObservationVectorPositionsValues observationWrapper = new WrapperObservationVectorPositionsValues(false);
        WrapperAction2DMoveVector actionWrapper = new WrapperAction2DMoveVector();
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(12345);
        
        ActorNetwork actor = new ActorNetwork(4, 10, secureRandom, 0.01, observationWrapper, actionWrapper, actionSet);
        actor.setLearningRate(0.01);  // Starting with a low learning rate
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        Action actionToReinforce = Action2DMove.up();
        double positiveReinforcement = 1.0;
        
        // When: Update with initial learning rate
        for (int i = 0; i < 10; i++) {
            actor.update(observation, actionToReinforce, positiveReinforcement);
        }
        
        // Count action selection with low learning rate
        Map<Action, Integer> lowLRCounts = new HashMap<>();
        int numSamples = 100;
        
        for (int i = 0; i < numSamples; i++) {
            Action action = actor.selectAction(observation, 0.0);
            lowLRCounts.put(action, lowLRCounts.getOrDefault(action, 0) + 1);
        }
        
        // Increase learning rate
        actor.setLearningRate(0.1);
        
        // Update with higher learning rate
        for (int i = 0; i < 10; i++) {
            actor.update(observation, actionToReinforce, positiveReinforcement);
        }
        
        // Count action selection with high learning rate
        Map<Action, Integer> highLRCounts = new HashMap<>();
        for (int i = 0; i < numSamples; i++) {
            Action action = actor.selectAction(observation, 0.0);
            highLRCounts.put(action, highLRCounts.getOrDefault(action, 0) + 1);
        }
        
        // Then: The reinforced action should be selected more frequently with higher learning rate
        int lowLRCount = lowLRCounts.getOrDefault(actionToReinforce, 0);
        int highLRCount = highLRCounts.getOrDefault(actionToReinforce, 0);
        
        assertThat(highLRCount).isGreaterThanOrEqualTo(lowLRCount);
    }
}
