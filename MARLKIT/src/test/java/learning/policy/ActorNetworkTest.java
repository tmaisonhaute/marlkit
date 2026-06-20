package learning.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2D;
import environment.observation.ObservationPositionValue;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.policies.ActorNetwork;
import util.Tuple;

public class ActorNetworkTest {

    @Test
    public void givenPolicy_whenInit_thenAgentSet() {
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);
        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        policy.init(agent);

        assertThat(policy.getAgent()).isEqualTo(agent);
    }

    @Test
    public void givenObservation_whenSelectAction_thenActionIsReturnedFromActionSet() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        when(wrapper.transform(observation)).thenReturn(new double[] {1.0, 2.0});

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);
        policy.init(agent);

        Action action = policy.selectAction(observation);

        assertThat(action).isNotNull();
        assertThat(actionSet).contains(action);
    }

    @Test
    public void givenObservation_whenForwardLogits_thenOutputSizeMatchesActionSetSize() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        when(wrapper.transform(observation)).thenReturn(new double[] {1.0, 2.0});

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);
        policy.init(agent);

        double[] logits = policy.forwardLogits(observation);

        assertThat(logits).hasSize(actionSet.size());
    }

    @Test
    public void givenBatchInputs_whenForwardLogits_thenBatchShapeIsCorrect() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        ObservationPositionValue firstObservation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        ObservationPositionValue secondObservation = new ObservationPositionValue(
            new Tuple(Arrays.asList(3.0, 4.0)), 2.0
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        when(wrapper.transform(firstObservation)).thenReturn(new double[] {1.0, 2.0});
        when(wrapper.transform(secondObservation)).thenReturn(new double[] {3.0, 4.0});

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);
        policy.init(agent);

        double[][] logits = policy.forwardLogits(new ObservationPositionValue[] {
            firstObservation,
            secondObservation
        });

        assertThat(logits.length).isEqualTo(2);
        assertThat(logits[0]).hasSize(actionSet.size());
        assertThat(logits[1]).hasSize(actionSet.size());
    }

    @Test
    public void givenObservation_whenForwardLogits_thenWrapperIsUsed() {
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        when(wrapper.transform(observation)).thenReturn(new double[] {1.0, 2.0});

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);
        policy.init(agent);

        policy.forwardLogits(observation);

        verify(wrapper).transform(observation);
    }

    @Test
    public void givenLogits_whenSoftmax_thenProbabilitiesAreValid() {
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);

        double[] probabilities = policy.softmax(new double[] {1.0, 2.0, 3.0});

        assertThat(probabilities).hasSize(3);
        assertThat(probabilities[0]).isBetween(0.0, 1.0);
        assertThat(probabilities[1]).isBetween(0.0, 1.0);
        assertThat(probabilities[2]).isBetween(0.0, 1.0);
        assertThat(probabilities[0] + probabilities[1] + probabilities[2]).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-12));
    }

    @Test
    public void givenLargeLogits_whenSoftmax_thenProbabilitiesAreFinite() {
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);

        double[] probabilities = policy.softmax(new double[] {1000.0, 1001.0, 1002.0});

        assertThat(probabilities).hasSize(3);
        assertThat(probabilities[0]).isFinite();
        assertThat(probabilities[1]).isFinite();
        assertThat(probabilities[2]).isFinite();
        assertThat(probabilities[0] + probabilities[1] + probabilities[2]).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-12));
    }

    @Test
    public void givenBatchLogits_whenSoftmax_thenEachRowIsProbabilityDistribution() {
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);

        double[][] probabilities = policy.softmax(new double[][] {
            {1.0, 2.0, 3.0},
            {-1.0, 0.0, 1.0}
        });

        assertThat(probabilities.length).isEqualTo(2);
        assertThat(probabilities[0]).hasSize(3);
        assertThat(probabilities[1]).hasSize(3);
        assertThat(probabilities[0][0] + probabilities[0][1] + probabilities[0][2]).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-12));
        assertThat(probabilities[1][0] + probabilities[1][1] + probabilities[1][2]).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-12));
    }

    @Test
    public void givenAction_whenActionIndex_thenCorrectIndexReturned() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);

        assertThat(policy.actionIndex(Move2D.up())).isEqualTo(0);
        assertThat(policy.actionIndex(Move2D.down())).isEqualTo(1);
        assertThat(policy.actionIndex(Move2D.left())).isEqualTo(2);
        assertThat(policy.actionIndex(Move2D.right())).isEqualTo(3);
    }

    @Test
    public void givenUnknownAction_whenActionIndex_thenThrows() {
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);

        assertThatThrownBy(() -> policy.actionIndex(Move2D.left()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown action");
    }

    @Test
    public void givenPolicy_whenGetActionSet_thenSameActionSetReturned() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);

        assertThat(policy.getActionSet()).isEqualTo(actionSet);
    }

    @Test
    public void givenPolicy_whenGetSoftmaxTemperature_thenOneReturned() {
        List<Action> actionSet = Arrays.asList(Move2D.up(), Move2D.down());
        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        ActorNetwork policy = new ActorNetwork(2, 8, wrapper, actionSet);

        assertThat(policy.getSoftmaxTemperature()).isEqualTo(1.0);
    }

    @Test
    public void givenInputAndGradient_whenUpdateFromLogitsGradient_thenLogitsChange() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        when(wrapper.transform(observation)).thenReturn(new double[] {1.0, 2.0});

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        ActorNetwork policy = new ActorNetwork(2, 16, wrapper, actionSet);
        policy.init(agent);

        double[] before = policy.forwardLogits(observation);

        policy.updateFromLogitsGradient(
            observation,
            new double[] {1.0, -1.0, 0.5, -0.5},
            0.01
        );

        double[] after = policy.forwardLogits(observation);

        assertThat(after).isNotEqualTo(before);
    }

    @Test
    public void givenBatchInputsAndGradients_whenUpdateFromLogitsGradient_thenAtLeastOneLogitVectorChanges() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        ObservationPositionValue firstObservation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        ObservationPositionValue secondObservation = new ObservationPositionValue(
            new Tuple(Arrays.asList(3.0, 4.0)), 2.0
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        when(wrapper.transform(firstObservation)).thenReturn(new double[] {1.0, 2.0});
        when(wrapper.transform(secondObservation)).thenReturn(new double[] {3.0, 4.0});

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        ActorNetwork policy = new ActorNetwork(2, 16, wrapper, actionSet);
        policy.init(agent);

        ObservationPositionValue[] inputs = new ObservationPositionValue[] {
            firstObservation,
            secondObservation
        };

        double[][] before = policy.forwardLogits(inputs);

        policy.updateFromLogitsGradient(
            inputs,
            new double[][] {
                {1.0, -1.0, 0.5, -0.5},
                {-0.5, 0.5, -1.0, 1.0}
            },
            0.01
        );

        double[][] after = policy.forwardLogits(inputs);

        assertThat(after[0]).isNotEqualTo(before[0]);
        assertThat(after[1]).isNotEqualTo(before[1]);
    }

    @Test
    public void givenSameSeed_whenTwoPoliciesInitialized_thenForwardLogitsAreIdentical() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        ObservationPositionValue observation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        WrapperPolicyInputVector firstWrapper = mock(WrapperPolicyInputVector.class);
        WrapperPolicyInputVector secondWrapper = mock(WrapperPolicyInputVector.class);
        when(firstWrapper.transform(observation)).thenReturn(new double[] {1.0, 2.0});
        when(secondWrapper.transform(observation)).thenReturn(new double[] {1.0, 2.0});

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        when(firstAgent.prng()).thenReturn(new Random(12345));
        when(secondAgent.prng()).thenReturn(new Random(12345));

        ActorNetwork firstPolicy = new ActorNetwork(2, 16, firstWrapper, actionSet);
        ActorNetwork secondPolicy = new ActorNetwork(2, 16, secondWrapper, actionSet);

        firstPolicy.init(firstAgent);
        secondPolicy.init(secondAgent);

        double[] firstLogits = firstPolicy.forwardLogits(observation);
        double[] secondLogits = secondPolicy.forwardLogits(observation);

        assertThat(firstLogits).containsExactly(secondLogits);
    }

    @Test
    public void givenDifferentInputs_whenForwardLogits_thenOutputsAreNotNecessarilyIdentical() {
        List<Action> actionSet = Arrays.asList(
            Move2D.up(),
            Move2D.down(),
            Move2D.left(),
            Move2D.right()
        );

        ObservationPositionValue firstObservation = new ObservationPositionValue(
            new Tuple(Arrays.asList(1.0, 2.0)), 1.0
        );

        ObservationPositionValue secondObservation = new ObservationPositionValue(
            new Tuple(Arrays.asList(-2.0, 5.0)), 2.0
        );

        WrapperPolicyInputVector wrapper = mock(WrapperPolicyInputVector.class);
        when(wrapper.transform(firstObservation)).thenReturn(new double[] {1.0, 2.0});
        when(wrapper.transform(secondObservation)).thenReturn(new double[] {-2.0, 5.0});

        MLKAgent agent = mock(MLKAgent.class);
        Random random = new Random(12345);
        when(agent.prng()).thenReturn(random);

        ActorNetwork policy = new ActorNetwork(2, 16, wrapper, actionSet);
        policy.init(agent);

        double[] firstLogits = policy.forwardLogits(firstObservation);
        double[] secondLogits = policy.forwardLogits(secondObservation);

        assertThat(firstLogits).isNotEqualTo(secondLogits);
    }
}