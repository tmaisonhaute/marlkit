package learning.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2DInt;
import environment.observation.ObservationPositionValue;
import learning.Batch;
import learning.Experience;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;
import madkit.kernel.AgentLogger;
import reward.RewardStandard;
import util.Pair;
import util.Tuple;

public class QLearningTest {

    @Test
    public void givenQLearning_whenInit_thenAgentSet() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up(), Move2DInt.down());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        QLearning qLearning = new QLearning(policy, actionSet);
        MLKAgent agent = mock(MLKAgent.class);
        
        // When
        qLearning.init(agent);
        
        // Then
        assertThat(qLearning.getAgent()).isEqualTo(agent);
    }
    
    @Test
    public void givenQLearning_whenGetPolicy_thenCorrectPolicyReturned() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        QLearning qLearning = new QLearning(policy, actionSet);
        
        // When & Then
        assertThat(qLearning.getPolicy()).isEqualTo(policy);
    }
    
    @Test
    public void givenQLearning_whenGetLearningFrequency_thenReturnsOne() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        QLearning qLearning = new QLearning(policy, actionSet);
        
        // When & Then
        assertThat(qLearning.getLearningFrequency()).isEqualTo(1);
    }
    
    @Test
    public void givenBatchWithExperiences_whenLearnOnBatch_thenQValuesUpdated() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Move2DInt.up(), 
            Move2DInt.down()
        );
        
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.0));
        QLearning qLearning = new QLearning(policy, actionSet, 0.1, 0.95);
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        qLearning.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 1.0)), 1.0
        );
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Move2DInt.up(), new RewardStandard(10.0)));
        batch.addExperience(new Experience(obs2, Move2DInt.down(), new RewardStandard(0.0)));
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        qLearning.learnOnBatch(batch, logger);
        
        // Then
        double qValue = policy.getTable().getValue(obs1, Move2DInt.up());
        // Q = 0 + 0.1 * (10 + 0.95 * 0 - 0) = 1.0
        assertThat(qValue).isEqualTo(1.0);
    }
    
    @Test
    public void givenBatchWithExperiences_whenLearnOnBatch_thenBatchSizeReducedToOne() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up(), Move2DInt.down());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        QLearning qLearning = new QLearning(policy, actionSet);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 1.0)), 1.0
        );
        ObservationPositionValue obs3 = new ObservationPositionValue(
            new Tuple(Arrays.asList(2.0, 2.0)), 1.0
        );
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Move2DInt.up(), new RewardStandard(1.0)));
        batch.addExperience(new Experience(obs2, Move2DInt.down(), new RewardStandard(2.0)));
        batch.addExperience(new Experience(obs3, Move2DInt.up(), new RewardStandard(3.0)));
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        int initialSize = batch.getExperiences().size();
        qLearning.learnOnBatch(batch, logger);
        int finalSize = batch.getExperiences().size();
        
        // Then
        assertThat(initialSize).isEqualTo(3);
        assertThat(finalSize).isEqualTo(1);
    }
    
    @Test
    public void givenQLearning_whenEndEpisode_thenBatchCleared() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.1));
        QLearning qLearning = new QLearning(policy, actionSet);
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        qLearning.init(agent);
        policy.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Move2DInt.up(), new RewardStandard(1.0)));
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        qLearning.endEpisode(batch, logger);
        
        // Then
        assertThat(batch.getExperiences()).isEmpty();
    }
    
    @Test
    public void givenQLearning_whenEndEpisode_thenEpsilonUpdated() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up());
        EpsilonGreedyExponentialDecay epsilonGreedy = new EpsilonGreedyExponentialDecay(1.0, 0.1); // 10% decay
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, epsilonGreedy);
        QLearning qLearning = new QLearning(policy, actionSet);
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        qLearning.init(agent);
        policy.init(agent);
        
        double initialEpsilon = epsilonGreedy.getEpsilon();
        
        Batch batch = new Batch();
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        qLearning.endEpisode(batch, logger);
        
        // Then
        double newEpsilon = epsilonGreedy.getEpsilon();
        assertThat(newEpsilon).isLessThan(initialEpsilon);
    }
    
    @Test
    public void givenMultipleUpdates_whenLearnOnBatch_thenQValuesConverge() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up(), Move2DInt.down());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.0));
        QLearning qLearning = new QLearning(policy, actionSet, 0.5, 0.9); // Higher learning rate
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        qLearning.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 1.0)), 1.0
        );
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When - train multiple times
        for (int i = 0; i < 100; i++) {
            Batch batch = new Batch();
            batch.addExperience(new Experience(obs1, Move2DInt.up(), new RewardStandard(10.0)));
            batch.addExperience(new Experience(obs2, Move2DInt.down(), new RewardStandard(0.0)));
            qLearning.learnOnBatch(batch, logger);
        }
        
        // Then - Q-value should converge towards the reward
        double qValue = policy.getTable().getValue(obs1, Move2DInt.up());
        assertThat(qValue).isGreaterThan(9.0); // Should be close to 10
    }
    
    @Test
    public void givenQLearningWithMaxNextQ_whenLearnOnBatch_thenUsesMaxQValue() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up(), Move2DInt.down());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.0));
        QLearning qLearning = new QLearning(policy, actionSet, 1.0, 0.9); // alpha=1 for easier calculation
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        qLearning.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 1.0)), 1.0
        );
        
        // Set Q-values for next state: up=5, down=10
        policy.getTable().setValue(new Pair<>(obs2, Move2DInt.up()), 5.0);
        policy.getTable().setValue(new Pair<>(obs2, Move2DInt.down()), 10.0);
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Move2DInt.up(), new RewardStandard(1.0)));
        batch.addExperience(new Experience(obs2, Move2DInt.down(), new RewardStandard(0.0)));
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        qLearning.learnOnBatch(batch, logger);
        
        // Then - Q(obs1, up) = 0 + 1.0 * (1 + 0.9 * max(5, 10) - 0) = 1 + 9 = 10
        double qValue = policy.getTable().getValue(obs1, Move2DInt.up());
        assertThat(qValue).isEqualTo(10.0);
    }
}
