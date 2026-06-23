package marlkit.preyhunter.agent;

import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2DDouble;
import learning.Algorithm;
import learning.Policy;
import learning.algorithms.PPOCategorical;
import learning.policies.NeuralNetworkCategoricalPolicy;
import marlkit.preyhunter.environment.WrapperPreyHunterObservationVector;

/**
 * Hunter agent for the continuous PreyHunter environment.
 *
 * By default, hunters use a PPO categorical policy over a fixed set of
 * directional Move2DDouble actions.
 */
public class HunterAgent extends AgentStandard {

    private static final int DEFAULT_NUMBER_OF_DIRECTIONS = 16;
    private static final double DEFAULT_SPEED = 0.2;

    private static final int DEFAULT_MAX_VISIBLE_HUNTERS = 1;
    private static final int DEFAULT_MAX_VISIBLE_PREYS = 1;

    private static final double DEFAULT_SOFTMAX_TEMPERATURE = 1.0;

    private static final double DEFAULT_LEARNING_RATE = 0.001;
    private static final double DEFAULT_GAMMA = 0.95;
    private static final double DEFAULT_CLIP_EPSILON = 0.2;
    private static final int DEFAULT_PPO_EPOCHS = 4;

    private static final int[] DEFAULT_HIDDEN_LAYERS = new int[] { 32, 32 };

    /**
     * Creates a hunter agent with default PPO categorical policy.
     *
     * Default observation capacity:
     * - 1 other hunter
     * - 1 prey
     */
    public HunterAgent() {
        this(
                DEFAULT_MAX_VISIBLE_HUNTERS,
                DEFAULT_MAX_VISIBLE_PREYS,
                DEFAULT_NUMBER_OF_DIRECTIONS,
                DEFAULT_SPEED
        );
    }

    /**
     * Creates a hunter agent with PPO categorical policy and configurable
     * observation capacity.
     *
     * @param maxVisibleHunters maximum number of hunter slots in the observation vector
     * @param maxVisiblePreys maximum number of prey slots in the observation vector
     */
    public HunterAgent(int maxVisibleHunters, int maxVisiblePreys) {
        this(maxVisibleHunters, maxVisiblePreys, DEFAULT_NUMBER_OF_DIRECTIONS, DEFAULT_SPEED);
    }

    /**
     * Creates a hunter agent with PPO categorical policy.
     *
     * @param maxVisibleHunters maximum hunter slots
     * @param maxVisiblePreys maximum prey slots
     * @param numberOfDirections number of discrete movement directions
     * @param speed movement speed
     */
    public HunterAgent(int maxVisibleHunters, int maxVisiblePreys, int numberOfDirections, double speed) {
        super();

        List<Action> actions = Move2DDouble.getDirectionalMoves(numberOfDirections, speed);

        WrapperPreyHunterObservationVector wrapper =
                new WrapperPreyHunterObservationVector(maxVisibleHunters, maxVisiblePreys);

        NeuralNetworkCategoricalPolicy policy = new NeuralNetworkCategoricalPolicy(
                actions,
                wrapper,
                wrapper.getVectorSize(),
                DEFAULT_HIDDEN_LAYERS,
                DEFAULT_SOFTMAX_TEMPERATURE
        );

        PPOCategorical algorithm = new PPOCategorical(
                policy,
                DEFAULT_LEARNING_RATE,
                DEFAULT_GAMMA,
                DEFAULT_CLIP_EPSILON,
                DEFAULT_PPO_EPOCHS
        );

        setPolicy(policy);
        setAlgorithm(algorithm);
    }

    /**
     * Creates a hunter agent with custom policy and algorithm.
     *
     * @param policy policy
     * @param algorithm algorithm
     */
    public HunterAgent(Policy policy, Algorithm algorithm) {
        super(policy, algorithm);
    }
}