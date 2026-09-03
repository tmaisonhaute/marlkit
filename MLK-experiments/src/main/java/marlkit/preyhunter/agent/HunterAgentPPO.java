package marlkit.preyhunter.agent;

import java.util.List;

import agent.action.Action;
import agent.action.Move2DDouble;
import learning.Algorithm;
import learning.Policy;
import learning.algorithms.PPOCategorical;
import learning.policies.NeuralNetworkCategoricalPolicy;
import marlkit.preyhunter.environment.WrapperPreyHunterObservationVector;

/**
 * Hunter agent using PPO with a categorical policy over a finite set of
 * continuous two-dimensional movements.
 *
 * <p>The movement directions are evenly distributed around the unit circle and
 * use a configurable movement speed.</p>
 */
public class HunterAgentPPO extends HunterAgent {

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
     * Creates a PPO hunter using the default observation capacity, movement
     * speed, and number of movement directions.
     */
    public HunterAgentPPO() {
        this(DEFAULT_MAX_VISIBLE_HUNTERS, DEFAULT_MAX_VISIBLE_PREYS, DEFAULT_NUMBER_OF_DIRECTIONS, DEFAULT_SPEED);
    }

    /**
     * Creates a PPO hunter with a configurable observation capacity.
     *
     * @param maxVisibleHunters the maximum number of other hunters represented
     *                          in an observation
     * @param maxVisiblePreys the maximum number of preys represented in an
     *                        observation
     */
    public HunterAgentPPO(int maxVisibleHunters, int maxVisiblePreys) {
        this(maxVisibleHunters, maxVisiblePreys, DEFAULT_NUMBER_OF_DIRECTIONS, DEFAULT_SPEED);
    }

    /**
     * Creates a PPO hunter with configurable observations and movements.
     *
     * @param maxVisibleHunters the maximum number of other hunters represented
     *                          in an observation
     * @param maxVisiblePreys the maximum number of preys represented in an
     *                        observation
     * @param numberOfDirections the number of available movement directions
     * @param speed the magnitude of each movement
     */
    public HunterAgentPPO(int maxVisibleHunters, int maxVisiblePreys, int numberOfDirections, double speed) {
        super();

        List<Action> actions = Move2DDouble.getDirectionalMoves(numberOfDirections, speed);
        WrapperPreyHunterObservationVector wrapper = new WrapperPreyHunterObservationVector(maxVisibleHunters, maxVisiblePreys);

        NeuralNetworkCategoricalPolicy policy = new NeuralNetworkCategoricalPolicy(actions, wrapper, wrapper.getVectorSize(), DEFAULT_HIDDEN_LAYERS, DEFAULT_SOFTMAX_TEMPERATURE);
        PPOCategorical algorithm = new PPOCategorical(policy, DEFAULT_LEARNING_RATE, DEFAULT_GAMMA, DEFAULT_CLIP_EPSILON, DEFAULT_PPO_EPOCHS);

        setPolicy(policy);
        setAlgorithm(algorithm);
    }

    /**
     * Creates a PPO hunter with custom learning components.
     *
     * <p>This constructor does not enforce that the supplied algorithm is
     * actually PPO and primarily serves as an extension point.</p>
     *
     * @param policy the policy used to select actions
     * @param algorithm the algorithm used to update the policy
     */
    public HunterAgentPPO(Policy policy, Algorithm algorithm) {
        super(policy, algorithm);
    }
}