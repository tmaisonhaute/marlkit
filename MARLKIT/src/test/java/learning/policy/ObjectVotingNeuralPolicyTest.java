package learning.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Random;

import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2DInt;
import environment.observation.ObservationPositionValue;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.nn.NeuralNetwork;
import learning.policies.ObjectVotingNeuralPolicy;
import util.Tuple;
import util.VectorOperator;

public class ObjectVotingNeuralPolicyTest {

    private static final double EPSILON = 1e-12;

    @Test
    public void givenPolicy_whenInit_thenAgentSet() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = new ObjectVotingNeuralPolicy(network, wrapper, actions, 1.0, true);
        MLKAgent agent = agent(12345);

        policy.init(agent);

        assertThat(policy.getAgent()).isEqualTo(agent);
    }

    @Test
    public void givenObservation_whenSelectAction_thenActionIsReturnedFromActionSet() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObservationPositionValue observation = observation(1.0, 2.0);

        when(wrapper.transform(observation)).thenReturn(new double[] {
            1.0, 2.0, -1.0,
            2.0, 0.0, 3.0
        });

        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        Action selected = policy.selectAction(observation);

        assertThat(selected).isNotNull();
        assertThat(Arrays.asList(actions)).contains(selected);
    }

    @Test
    public void givenObservation_whenSelectAction_thenWrapperIsUsed() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObservationPositionValue observation = observation(1.0, 2.0);

        when(wrapper.transform(observation)).thenReturn(new double[] {
            1.0, 2.0, -1.0
        });

        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        policy.selectAction(observation);

        verify(wrapper).transform(observation);
    }

    @Test
    public void givenNoEntity_whenComputeLogits_thenZeroLogitsReturned() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = new ObjectVotingNeuralPolicy(network, wrapper, actions, 1.0, false);

        double[] logits = policy.computeLogits(new double[0][]);

        assertThat(logits).containsExactly(0.0, 0.0, 0.0, 0.0);
    }

    @Test
    public void givenNullEntities_whenComputeLogits_thenZeroLogitsReturned() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = new ObjectVotingNeuralPolicy(network, wrapper, actions, 1.0, false);

        double[] logits = policy.computeLogits(null);

        assertThat(logits).containsExactly(0.0, 0.0, 0.0, 0.0);
    }

    @Test
    public void givenRepeatedSameEntity_whenComputeLogits_thenMeanAggregationKeepsSameLogits() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        double[] entity = new double[] {1.0, 2.0, -1.0};

        double[] single = policy.computeLogits(new double[][] {entity});
        double[] repeated = policy.computeLogits(new double[][] {entity, entity, entity});


        assertThat(repeated).containsExactly(new double[] {single[0], single[1], single[2], single[3]}, Offset.offset(1e-12));

    }

    @Test
    public void givenNullEntityInsideBatch_whenComputeLogits_thenNullEntityIgnored() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        double[] entity = new double[] {1.0, 2.0, -1.0};

        double[] withoutNull = policy.computeLogits(new double[][] {entity});
        double[] withNull = policy.computeLogits(new double[][] {null, entity, null});

        assertThat(withNull).containsExactly(withoutNull);
    }

    @Test
    public void givenBatchInputs_whenForwardLogits_thenBatchShapeIsCorrect() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);

        ObservationPositionValue first = observation(1.0, 2.0);
        ObservationPositionValue second = observation(3.0, 4.0);

        when(wrapper.transform(first)).thenReturn(new double[] {
            1.0, 2.0, -1.0
        });
        when(wrapper.transform(second)).thenReturn(new double[] {
            2.0, 0.0, 3.0,
            1.0, -1.0, 1.0
        });

        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        double[][] logits = policy.forwardLogits(new ObservationPositionValue[] {first, second});

        assertThat(logits.length).isEqualTo(2);
        assertThat(logits[0]).hasSize(actions.length);
        assertThat(logits[1]).hasSize(actions.length);
    }

    @Test
    public void givenEmptyObservation_whenForwardLogits_thenZeroLogitsReturned() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObservationPositionValue observation = observation(1.0, 2.0);

        when(wrapper.transform(observation)).thenReturn(new double[0]);

        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        double[][] logits = policy.forwardLogits(new ObservationPositionValue[] {observation});

        assertThat(logits.length).isEqualTo(1);
        assertThat(logits[0]).containsExactly(0.0, 0.0, 0.0, 0.0);
    }

    @Test
    public void givenActionFromActionSet_whenActionIndex_thenCorrectIndexReturned() {
        Action up = Move2DInt.up();
        Action down = Move2DInt.down();
        Action left = Move2DInt.left();
        Action right = Move2DInt.right();

        Action[] actions = new Action[] {up, down, left, right};
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = new ObjectVotingNeuralPolicy(network, wrapper, actions, 1.0, false);

        assertThat(policy.actionIndex(up)).isEqualTo(0);
        assertThat(policy.actionIndex(down)).isEqualTo(1);
        assertThat(policy.actionIndex(left)).isEqualTo(2);
        assertThat(policy.actionIndex(right)).isEqualTo(3);
    }

    @Test
    public void givenUnknownAction_whenActionIndex_thenThrows() {
        Action[] actions = new Action[] {Move2DInt.up(), Move2DInt.down()};
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = new ObjectVotingNeuralPolicy(network, wrapper, actions, 1.0, false);

        assertThatThrownBy(() -> policy.actionIndex(Move2DInt.left()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown action");
    }

    @Test
    public void givenPolicy_whenGetSoftmaxTemperature_thenConfiguredTemperatureReturned() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = new ObjectVotingNeuralPolicy(network, wrapper, actions, 0.7, false);

        assertThat(policy.getSoftmaxTemperature()).isEqualTo(0.7);
    }

    @Test
    public void givenInvalidTemperature_whenConstruct_thenThrows() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);

        assertThatThrownBy(() -> new ObjectVotingNeuralPolicy(network, wrapper, actions, 0.0, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("temperature");
    }

    @Test
    public void givenEmptyActions_whenConstruct_thenThrows() {
        NeuralNetwork network = network(3, 1);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);

        assertThatThrownBy(() -> new ObjectVotingNeuralPolicy(network, wrapper, new Action[0], 1.0, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("actions");
    }

    @Test
    public void givenNetworkOutputSizeMismatch_whenConstruct_thenThrows() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, 2);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);

        assertThatThrownBy(() -> new ObjectVotingNeuralPolicy(network, wrapper, actions, 1.0, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("output size");
    }

    @Test
    public void givenInputAndGradient_whenUpdateFromLogitsGradient_thenLogitsChange() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObservationPositionValue observation = observation(1.0, 2.0);

        when(wrapper.transform(observation)).thenReturn(new double[] {
            1.0, 2.0, -1.0,
            2.0, 0.0, 3.0
        });

        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        double[][] before = policy.forwardLogits(new ObservationPositionValue[] {observation});

        policy.updateFromLogitsGradient(
            new ObservationPositionValue[] {observation},
            new double[][] {{1.0, -1.0, 0.5, -0.5}},
            0.01
        );

        double[][] after = policy.forwardLogits(new ObservationPositionValue[] {observation});

        assertThat(after[0]).isNotEqualTo(before[0]);
    }

    @Test
    public void givenEmptyObservation_whenUpdateFromLogitsGradient_thenLogitsRemainZero() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObservationPositionValue observation = observation(1.0, 2.0);

        when(wrapper.transform(observation)).thenReturn(new double[0]);

        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        policy.updateFromLogitsGradient(
            new ObservationPositionValue[] {observation},
            new double[][] {{1.0, -1.0, 0.5, -0.5}},
            0.01
        );

        double[][] logits = policy.forwardLogits(new ObservationPositionValue[] {observation});

        assertThat(logits[0]).containsExactly(0.0, 0.0, 0.0, 0.0);
    }

    @Test
    public void givenLogitsFromPolicy_whenSoftmaxApplied_thenProbabilitiesSumToOne() {
        Action[] actions = actions();
        NeuralNetwork network = network(3, actions.length);
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ObjectVotingNeuralPolicy policy = initializedPolicy(network, wrapper, actions, 12345);

        double[] logits = policy.computeLogits(new double[][] {
            {1.0, 2.0, -1.0},
            {2.0, 0.0, 3.0}
        });

        double[] probabilities = VectorOperator.softmax(logits, policy.getSoftmaxTemperature());

        assertThat(sum(probabilities)).isCloseTo(1.0, Offset.offset(EPSILON));
        for (double probability : probabilities) {
            assertThat(probability).isBetween(0.0, 1.0);
        }
    }

    private ObjectVotingNeuralPolicy initializedPolicy(
            NeuralNetwork network,
            WrapperPolicyInputVector wrapper,
            Action[] actions,
            long seed
    ) {
        ObjectVotingNeuralPolicy policy = new ObjectVotingNeuralPolicy(network, wrapper, actions, 1.0, true);
        policy.init(agent(seed));
        return policy;
    }

    private NeuralNetwork network(int inputSize, int outputSize) {
        return new NeuralNetwork(
            new int[] {inputSize, 8, outputSize},
            NeuralNetwork.Activations.relu(),
            NeuralNetwork.Activations.identity()
        );
    }

    private Action[] actions() {
        return new Action[] {
            Move2DInt.up(),
            Move2DInt.down(),
            Move2DInt.left(),
            Move2DInt.right()
        };
    }

    private MLKAgent agent(long seed) {
        MLKAgent agent = mock(MLKAgent.class);
        when(agent.prng()).thenReturn(new Random(seed));
        return agent;
    }

    private ObservationPositionValue observation(double x, double y) {
        return new ObservationPositionValue(
            new Tuple(Arrays.asList(x, y)),
            1.0
        );
    }

    private double sum(double[] values) {
        double sum = 0.0;

        for (double value : values) {
            sum += value;
        }

        return sum;
    }
}