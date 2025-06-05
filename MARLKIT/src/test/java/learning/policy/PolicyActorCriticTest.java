package learning.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.reward.RewardStandard;
import learning.Batch;
import learning.Experience;
import madkit.kernel.AgentLogger;
import util.Tuple;

public class PolicyActorCriticTest {

    @Test
    public void givenObservation_whenTakeAction_thenActionIsReturned() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        
        MLKAgent agent = mock(MLKAgent.class);
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(12345);
        when(agent.prng()).thenReturn(secureRandom);
        
        PolicyActorCritic policy = new PolicyActorCritic(actionSet, 4, 0.05);
        policy.init(agent);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        // When
        Action action = policy.takeAction(observation);
        
        // Then
        assertThat(action).isNotNull();
        assertThat(actionSet).contains(action);
    }
    
    
    @Test
    public void givenBatchWithExperiences_whenLearnOnBatch_thenBatchIsProcessed() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        MLKAgent agent = mock(MLKAgent.class);
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(12345);
        when(agent.prng()).thenReturn(secureRandom);

        PolicyActorCritic policy = new PolicyActorCritic(actionSet, 2, 0.05);
        policy.init(agent);
        
        ObservationPositionsValues obs1 = new ObservationPositionsValues();
        obs1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        ObservationPositionsValues obs2 = new ObservationPositionsValues();
        obs2.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Action2DMove.up(), new RewardStandard(1.0)));
        batch.addExperience(new Experience(obs2, Action2DMove.right(), new RewardStandard(2.0)));
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        int initialExperiences = batch.getExperiences().size();
        policy.learnOnBatch(batch, logger);
        int finalExperiences = batch.getExperiences().size();
        
        // Then
        assertThat(initialExperiences).isEqualTo(2);
        assertThat(finalExperiences).isEqualTo(1);
    }
    
    @Test
    public void givenMultipleUpdates_whenTakeAction_thenActionsReflectLearning() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        MLKAgent agent = mock(MLKAgent.class);
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(12345);
        when(agent.prng()).thenReturn(secureRandom);
        
        PolicyActorCritic policy = new PolicyActorCritic(actionSet, 4, 0.0); // epsilon = 0 for deterministic behavior
        policy.init(agent);
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // Create a simple observation
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 1.0));
        
        Action actionToReinforce = Action2DMove.up();
        
        // Train the policy to prefer the 'up' action for this observation
        for (int i = 0; i < 500; i++) {
            Batch batch = new Batch();
            batch.addExperience(new Experience(observation, actionToReinforce, new RewardStandard(1.0)));
            batch.addExperience(new Experience(observation, actionToReinforce, new RewardStandard(1.0)));
            policy.learnOnBatch(batch, logger);
        }
        
        // When
        int upCount = 0;
        int totalTrials = 10;
        
        for (int i = 0; i < totalTrials; i++) {
            Action selectedAction = policy.takeAction(observation);
            if (selectedAction.equals(actionToReinforce)) {
                upCount++;
            }
        }
        
        // Then
        // With epsilon = 0, if the policy has learned successfully, it should consistently choose the reinforced action
        assertThat(upCount).isGreaterThanOrEqualTo((int) (totalTrials * 0.7)); // At least 70% should be the reinforced action
    }
    

}
