package learning.policy;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.observation.ObservationPositionValue;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;
import util.Pair;
import util.Tuple;

public class QValueBasedPolicyTest {

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
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        policy.init(agent);
        
        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );
        
        // When
        Action action = policy.takeAction(observation);
        
        // Then
        assertThat(action).isNotNull();
        assertThat(actionSet).contains(action);
    }
    
    @Test
    public void givenQValues_whenTakeActionWithZeroEpsilon_thenBestActionSelected() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        
        // Use epsilon = 0 to always exploit
        EpsilonGreedyExponentialDecay noExploration = new EpsilonGreedyExponentialDecay(0.0);
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, noExploration);
        policy.init(agent);
        
        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );
        
        // Set Q-values: make "up" the best action
        Action bestAction = Action2DMove.up();
        policy.getTable().setValue(new Pair<>(observation, bestAction), 10.0);
        policy.getTable().setValue(new Pair<>(observation, Action2DMove.down()), 1.0);
        policy.getTable().setValue(new Pair<>(observation, Action2DMove.left()), 2.0);
        policy.getTable().setValue(new Pair<>(observation, Action2DMove.right()), 3.0);
        
        // When
        Action selectedAction = policy.takeAction(observation);
        
        // Then
        assertThat(selectedAction).isEqualTo(bestAction);
    }
    
    @Test
    public void givenDefaultQValue_whenGetQValue_thenDefaultReturned() {
        // Given
        List<Action> actionSet = Arrays.asList(Action2DMove.up());
        double defaultValue = 5.0;
        
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, defaultValue);
        
        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );
        
        // When
        double qValue = policy.getTable().getValue(observation, Action2DMove.up());
        
        // Then
        assertThat(qValue).isEqualTo(defaultValue);
    }
    
    @Test
    public void givenPolicy_whenReset_thenQTableCleared() {
        // Given
        List<Action> actionSet = Arrays.asList(Action2DMove.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        
        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );
        
        policy.getTable().setValue(new Pair<>(observation, Action2DMove.up()), 10.0);
        assertThat(policy.getTable().size()).isEqualTo(1);
        
        // When
        policy.reset();
        
        // Then
        assertThat(policy.getTable().size()).isEqualTo(0);
    }
    
    @Test
    public void givenPolicy_whenInit_thenAgentSet() {
        // Given
        List<Action> actionSet = Arrays.asList(Action2DMove.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        MLKAgent agent = mock(MLKAgent.class);
        
        // When
        policy.init(agent);
        
        // Then
        assertThat(policy.getAgent()).isEqualTo(agent);
    }
    
    @Test
    public void givenExplorationStrategy_whenGetExplorationStrategy_thenCorrectStrategyReturned() {
        // Given
        List<Action> actionSet = Arrays.asList(Action2DMove.up());
        EpsilonGreedyExponentialDecay strategy = new EpsilonGreedyExponentialDecay(0.1);
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, strategy);
        
        // When & Then
        assertThat(policy.getExplorationStrategy()).isEqualTo(strategy);
    }
    
    @Test
    public void givenHighEpsilon_whenTakeActionMultipleTimes_thenExplorationOccurs() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Action2DMove.up(), 
            Action2DMove.down(), 
            Action2DMove.left(), 
            Action2DMove.right()
        );
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        
        // High epsilon for lots of exploration
        EpsilonGreedyExponentialDecay highExploration = new EpsilonGreedyExponentialDecay(1.0);
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, highExploration);
        policy.init(agent);
        
        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );
        
        // Set one action as clearly best
        policy.getTable().setValue(new Pair<>(observation, Action2DMove.up()), 100.0);
        
        // When - take many actions
        int differentActionsCount = 0;
        Action firstAction = policy.takeAction(observation);
        for (int i = 0; i < 100; i++) {
            Action action = policy.takeAction(observation);
            if (!action.equals(firstAction)) {
                differentActionsCount++;
            }
        }
        
        // Then - with epsilon=1.0, we should see different actions (exploration)
        assertThat(differentActionsCount).isGreaterThan(0);
    }
}
