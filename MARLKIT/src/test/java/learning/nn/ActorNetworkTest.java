package learning.nn;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;

import agent.action.Action;
import agent.action.Action2DMove;
import agent.action.wrapperactionvector.WrapperAction2DMoveVector;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import util.Tuple;

public class ActorNetworkTest {

    @Test
    public void givenObservation_whenSelectAction_thenActionIsReturned() {
        // Given
        WrapperVectorObservationPositionsValues observationWrapper = new WrapperVectorObservationPositionsValues(false);
        WrapperAction2DMoveVector actionWrapper = new WrapperAction2DMoveVector();
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        
        Random random = new Random(12345);
        ActorNetwork actor = new ActorNetwork(4, 10, random, observationWrapper, actionSet);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        // When: Select action with epsilon = 0 (always exploit)
        Action action = actor.takeAction(observation);
        
        // Then
        assertThat(action).isNotNull();
        assertThat(actionSet).contains(action);
    }
    
    
}
