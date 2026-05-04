package learning.algorithm;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2D;
import environment.observation.ObservationPositionValue;
import environment.reward.RewardStandard;
import learning.Batch;
import learning.Experience;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;
import madkit.kernel.AgentLogger;
import util.Pair;
import util.Tuple;

public class SarsaTest {

    @Test
    public void givenSarsa_whenInit_thenAgentSet() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        Sarsa sarsa = new Sarsa(policy);
        MLKAgent agent = mock(MLKAgent.class);
        
        // When
        sarsa.init(agent);
        
        // Then
        assertThat(sarsa.getAgent()).isEqualTo(agent);
    }
    
    @Test
    public void givenSarsa_whenGetPolicy_thenCorrectPolicyReturned() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2D.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        Sarsa sarsa = new Sarsa(policy);
        
        // When & Then
        assertThat(sarsa.getPolicy()).isEqualTo(policy);
    }
    
    @Test
    public void givenSarsa_whenGetLearningFrequency_thenReturnsOne() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2D.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        Sarsa sarsa = new Sarsa(policy);
        
        // When & Then
        assertThat(sarsa.getLearningFrequency()).isEqualTo(1);
    }
    
    @Test
    public void givenBatchWithExperiences_whenLearnOnBatch_thenQValuesUpdated() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Move2D.up(), 
            Move2D.down()
        );
        
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.0));
        Sarsa sarsa = new Sarsa(policy, 0.1, 0.95);
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        sarsa.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 1.0)), 1.0
        );
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Move2D.up(), new RewardStandard(10.0)));
        batch.addExperience(new Experience(obs2, Move2D.down(), new RewardStandard(0.0)));
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        sarsa.learnOnBatch(batch, logger);
        
        // Then
        double qValue = policy.getTable().getValue(obs1, Move2D.up());
        // Q = 0 + 0.1 * (10 + 0.95 * 0 - 0) = 1.0
        assertThat(qValue).isEqualTo(1.0);
    }
    
    @Test
    public void givenBatchWithExperiences_whenLearnOnBatch_thenBatchSizeReducedToOne() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet);
        Sarsa sarsa = new Sarsa(policy);
        
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
        batch.addExperience(new Experience(obs1, Move2D.up(), new RewardStandard(1.0)));
        batch.addExperience(new Experience(obs2, Move2D.down(), new RewardStandard(2.0)));
        batch.addExperience(new Experience(obs3, Move2D.up(), new RewardStandard(3.0)));
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        int initialSize = batch.getExperiences().size();
        sarsa.learnOnBatch(batch, logger);
        int finalSize = batch.getExperiences().size();
        
        // Then
        assertThat(initialSize).isEqualTo(3);
        assertThat(finalSize).isEqualTo(1);
    }
    
    @Test
    public void givenSarsa_whenEndEpisode_thenBatchCleared() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2D.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.1));
        Sarsa sarsa = new Sarsa(policy);
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        sarsa.init(agent);
        policy.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Move2D.up(), new RewardStandard(1.0)));
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        sarsa.endEpisode(batch, logger);
        
        // Then
        assertThat(batch.getExperiences()).isEmpty();
    }
    
    @Test
    public void givenSarsa_whenEndEpisode_thenEpsilonUpdated() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2D.up());
        EpsilonGreedyExponentialDecay epsilonGreedy = new EpsilonGreedyExponentialDecay(1.0, 0.1); // 10% decay
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, epsilonGreedy);
        Sarsa sarsa = new Sarsa(policy);
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        sarsa.init(agent);
        policy.init(agent);
        
        double initialEpsilon = epsilonGreedy.getEpsilon();
        
        Batch batch = new Batch();
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        sarsa.endEpisode(batch, logger);
        
        // Then
        double newEpsilon = epsilonGreedy.getEpsilon();
        assertThat(newEpsilon).isLessThan(initialEpsilon);
    }
    
    @Test
    public void givenSarsa_whenEndEpisodeWithLastExperience_thenTerminalStateHandled() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2D.up());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.0));
        Sarsa sarsa = new Sarsa(policy, 1.0, 0.9); // alpha=1 for easier calculation
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        sarsa.init(agent);
        policy.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Move2D.up(), new RewardStandard(5.0)));
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        sarsa.endEpisode(batch, logger);
        
        // Then - Q(obs1, up) = 0 + 1.0 * (5 + 0.9 * 0 - 0) = 5.0 (terminal state has Q=0)
        double qValue = policy.getTable().getValue(obs1, Move2D.up());
        assertThat(qValue).isEqualTo(5.0);
    }
    
    @Test
    public void givenSarsaWithNextActionQ_whenLearnOnBatch_thenUsesActualNextAction() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        QValueBasedPolicy policy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.0));
        Sarsa sarsa = new Sarsa(policy, 1.0, 0.9); // alpha=1 for easier calculation
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        sarsa.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 1.0)), 1.0
        );
        
        // Set Q-values for next state: up=5, down=10
        // SARSA should use down (the actual next action), not max
        policy.getTable().setValue(new Pair<>(obs2, Move2D.up()), 5.0);
        policy.getTable().setValue(new Pair<>(obs2, Move2D.down()), 10.0);
        
        Batch batch = new Batch();
        batch.addExperience(new Experience(obs1, Move2D.up(), new RewardStandard(1.0)));
        batch.addExperience(new Experience(obs2, Move2D.down(), new RewardStandard(0.0))); // next action is down
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When
        sarsa.learnOnBatch(batch, logger);
        
        // Then - Q(obs1, up) = 0 + 1.0 * (1 + 0.9 * Q(obs2, down) - 0) = 1 + 0.9 * 10 = 10
        double qValue = policy.getTable().getValue(obs1, Move2D.up());
        assertThat(qValue).isEqualTo(10.0);
    }
    
    @Test
    public void givenSarsaVsQLearning_whenDifferentNextAction_thenDifferentUpdates() {
        // Given - same setup for both algorithms
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        
        QValueBasedPolicy sarsaPolicy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.0));
        Sarsa sarsa = new Sarsa(sarsaPolicy, 1.0, 0.9);
        
        QValueBasedPolicy qLearningPolicy = new QValueBasedPolicy(actionSet, 0.0, new EpsilonGreedyExponentialDecay(0.0));
        QLearning qLearning = new QLearning(qLearningPolicy, actionSet, 1.0, 0.9);
        
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        sarsa.init(agent);
        qLearning.init(agent);
        
        ObservationPositionValue obs1 = new ObservationPositionValue(
            new Tuple(Arrays.asList(0.0, 0.0)), 1.0
        );
        ObservationPositionValue obs2 = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 1.0)), 1.0
        );
        
        // Set different Q-values for next state actions
        sarsaPolicy.getTable().setValue(new Pair<>(obs2, Move2D.up()), 10.0);  // max
        sarsaPolicy.getTable().setValue(new Pair<>(obs2, Move2D.down()), 2.0); // actual next action
        
        qLearningPolicy.getTable().setValue(new Pair<>(obs2, Move2D.up()), 10.0);  // max
        qLearningPolicy.getTable().setValue(new Pair<>(obs2, Move2D.down()), 2.0);
        
        AgentLogger logger = mock(AgentLogger.class);
        
        // When - next action is "down" (not the max action)
        Batch sarsaBatch = new Batch();
        sarsaBatch.addExperience(new Experience(obs1, Move2D.up(), new RewardStandard(1.0)));
        sarsaBatch.addExperience(new Experience(obs2, Move2D.down(), new RewardStandard(0.0)));
        sarsa.learnOnBatch(sarsaBatch, logger);
        
        Batch qLearningBatch = new Batch();
        qLearningBatch.addExperience(new Experience(obs1, Move2D.up(), new RewardStandard(1.0)));
        qLearningBatch.addExperience(new Experience(obs2, Move2D.down(), new RewardStandard(0.0)));
        qLearning.learnOnBatch(qLearningBatch, logger);
        
        // Then - SARSA uses Q(obs2, down)=2, Q-Learning uses max=10
        double sarsaQ = sarsaPolicy.getTable().getValue(obs1, Move2D.up());
        double qLearningQ = qLearningPolicy.getTable().getValue(obs1, Move2D.up());
        
        // SARSA: Q = 0 + 1.0 * (1 + 0.9 * 2 - 0) = 2.8
        // Q-Learning: Q = 0 + 1.0 * (1 + 0.9 * 10 - 0) = 10.0
        assertThat(sarsaQ).isEqualTo(2.8);
        assertThat(qLearningQ).isEqualTo(10.0);
        assertThat(sarsaQ).isNotEqualTo(qLearningQ);
    }
}
