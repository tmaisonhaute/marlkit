package learning.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2DInt;
import environment.observation.ObservationPositionValue;
import learning.ExplorationStrategy;
import learning.explorationstrategies.NoExploration;
import learning.policies.SoftmaxQPolicy;
import util.Pair;
import util.Tuple;

public class SoftmaxQPolicyTest {

    @Test
    public void givenObservation_whenTakeAction_thenActionIsReturned() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Move2DInt.up(),
            Move2DInt.down(),
            Move2DInt.left(),
            Move2DInt.right()
        );

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        SoftmaxQPolicy policy = new SoftmaxQPolicy(actionSet, 0.0, 1.0, new NoExploration());
        policy.init(agent);

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        // When
        Action action = policy.selectAction(observation);

        // Then
        assertThat(action).isNotNull();
        assertThat(actionSet).contains(action);
    }

    @Test
    public void givenExplorationStrategy_whenSelectAction_thenExploratoryActionReturned() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Move2DInt.up(),
            Move2DInt.down(),
            Move2DInt.left(),
            Move2DInt.right()
        );
        Action exploratoryAction = Move2DInt.left();

        ExplorationStrategy strategy = mock(ExplorationStrategy.class);
        when(strategy.getExploratoryAction(anyList(), any(RandomGenerator.class)))
            .thenReturn(Optional.of(exploratoryAction));

        SoftmaxQPolicy policy = new SoftmaxQPolicy(actionSet, 0.0, 1.0, strategy);
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);
        policy.init(agent);

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        // When
        Action action = policy.selectAction(observation);

        // Then
        assertThat(action).isEqualTo(exploratoryAction);
    }

    @Test
    public void givenDefaultQValue_whenGetQValue_thenDefaultReturned() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up());
        double defaultValue = 5.0;

        SoftmaxQPolicy policy = new SoftmaxQPolicy(actionSet, defaultValue, 1.0, new NoExploration());

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        // When
        double qValue = policy.getTable().getValue(observation, Move2DInt.up());

        // Then
        assertThat(qValue).isEqualTo(defaultValue);
    }

    @Test
    public void givenPolicy_whenInit_thenAgentSet() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up());
        SoftmaxQPolicy policy = new SoftmaxQPolicy(actionSet, 0.0, 1.0, new NoExploration());
        MLKAgent agent = mock(MLKAgent.class);

        // When
        policy.init(agent);

        // Then
        assertThat(policy.getAgent()).isEqualTo(agent);
    }

    @Test
    public void givenTemperatureNonPositive_whenConstruct_thenThrows() {
        // Given
        List<Action> actionSet = Arrays.asList(Move2DInt.up());

        // When / Then
        assertThatThrownBy(() -> new SoftmaxQPolicy(actionSet, 0.0, 0.0, new NoExploration()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("temperature");
    }

    @Test
    public void givenHigherQValue_whenSampling_thenBestActionMoreFrequent() {
        // Given
        List<Action> actionSet = Arrays.asList(
            Move2DInt.up(),
            Move2DInt.down(),
            Move2DInt.left(),
            Move2DInt.right()
        );

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        SoftmaxQPolicy policy = new SoftmaxQPolicy(actionSet, 0.0, 1.0, new NoExploration());
        policy.init(agent);

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        Action bestAction = Move2DInt.up();
        policy.getTable().setValue(new Pair<>(observation, bestAction), 10.0);
        policy.getTable().setValue(new Pair<>(observation, Move2DInt.down()), 0.0);
        policy.getTable().setValue(new Pair<>(observation, Move2DInt.left()), 0.0);
        policy.getTable().setValue(new Pair<>(observation, Move2DInt.right()), 0.0);

        // When
        int bestCount = 0;
        int samples = 100;
        for (int i = 0; i < samples; i++) {
            Action action = policy.selectAction(observation);
            if (action.equals(bestAction)) {
                bestCount++;
            }
        }

        // Then
        assertThat(bestCount).isGreaterThan(90);
    }


}
