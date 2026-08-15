package marlkit.preyhunter.agent;

import agent.action.Action;
import agent.action.ActionContinuousVector;
import agent.action.Move2DDouble;
import learning.ContinuousActionExplorationStrategy;
import learning.actionexplorationstrategies.GaussianNoise;
import learning.algorithms.DDPG;
import learning.nn.ActionValueCritic;
import learning.policies.MLPDeterministicPolicy;
import learning.policies.PolicyInput;
import marlkit.preyhunter.environment.WrapperPreyHunterObservationVector;

/**
 * Hunter agent using Deep Deterministic Policy Gradient with a continuous
 * two-dimensional movement policy.
 *
 * <p>The actor produces an {@link ActionContinuousVector} containing the
 * horizontal and vertical movement components. The selected action is converted
 * into a {@link Move2DDouble} before being sent to the PreyHunter
 * environment.</p>
 */
public class HunterAgentDDPG extends HunterAgent {

	protected static final int DEFAULT_MAX_VISIBLE_HUNTERS = 1;
	protected static final int DEFAULT_MAX_VISIBLE_PREYS = 1;

	protected static final int[] DEFAULT_ACTOR_HIDDEN_LAYERS = new int[] { 64, 64 };
	protected static final int DEFAULT_CRITIC_HIDDEN_SIZE = 64;

	protected static final int ACTION_SIZE = 2;
	protected static final double DEFAULT_SPEED = 0.2;
	protected static final double NOISE_COEFFFICIENT = 0.4;

	protected static final double DEFAULT_ACTOR_LEARNING_RATE = 0.0001;
	protected static final double DEFAULT_CRITIC_LEARNING_RATE = 0.001;
    protected static final double DEFAULT_GAMMA = 0.99;
    protected static final double DEFAULT_TAU = 0.005;

    protected static final int DEFAULT_LEARNING_BATCH_SIZE = 64;
    protected static final int DEFAULT_REPLAY_BUFFER_CAPACITY = 100_000;

    /**
     * Creates a DDPG hunter with the default observation capacity.
     */
    public HunterAgentDDPG() {
        this(DEFAULT_MAX_VISIBLE_HUNTERS, DEFAULT_MAX_VISIBLE_PREYS);
    }

    /**
     * Creates a DDPG hunter with the specified observation capacity.
     *
     * @param maxVisibleHunters the maximum number of other hunters represented
     *                          in an observation
     * @param maxVisiblePreys the maximum number of preys represented in an
     *                        observation
     */
    public HunterAgentDDPG(int maxVisibleHunters, int maxVisiblePreys) {
        this(maxVisibleHunters, maxVisiblePreys, DEFAULT_SPEED);
    }
    
    /**
     * Creates a DDPG hunter with the specified observation capacity and movement speed.
     * @param maxVisibleHunters the maximum number of other hunters represented in an observation
     * @param maxVisiblePreys the maximum number of preys represented in an observation
     * @param speed the maximum speed of the hunter
     */
    public HunterAgentDDPG(int maxVisibleHunters, int maxVisiblePreys, double speed) {
        super();

        WrapperPreyHunterObservationVector wrapper = new WrapperPreyHunterObservationVector(maxVisibleHunters, maxVisiblePreys);
        int observationSize = wrapper.getVectorSize();
        
        ContinuousActionExplorationStrategy actionNoiseStrategy = new GaussianNoise(speed * NOISE_COEFFFICIENT);

        MLPDeterministicPolicy actor = createActor(wrapper, observationSize, speed, actionNoiseStrategy);
        MLPDeterministicPolicy targetActor = createActor(wrapper, observationSize, speed);

        ActionValueCritic critic = new ActionValueCritic(observationSize, ACTION_SIZE, DEFAULT_CRITIC_HIDDEN_SIZE, wrapper);
        ActionValueCritic targetCritic = new ActionValueCritic(observationSize, ACTION_SIZE, DEFAULT_CRITIC_HIDDEN_SIZE, wrapper);

        DDPG algorithm = new DDPG(actor, targetActor, critic, targetCritic, DEFAULT_ACTOR_LEARNING_RATE, DEFAULT_CRITIC_LEARNING_RATE, DEFAULT_GAMMA, DEFAULT_TAU, DEFAULT_LEARNING_BATCH_SIZE, DEFAULT_REPLAY_BUFFER_CAPACITY);

        setPolicy(actor);
        setAlgorithm(algorithm);
    }

    /**
     * Creates an actor producing two-dimensional movement vectors bounded by
     * the hunter's maximum speed.
     *
     * @param wrapper the observation vector wrapper
     * @param observationSize the observation vector size
     * @param speed the maximum speed of the hunter
     * @param actionNoise the exploration strategy for continuous actions
     * @return a deterministic continuous actor
     */
    protected MLPDeterministicPolicy createActor(WrapperPreyHunterObservationVector wrapper, int observationSize, 
    		double speed, ContinuousActionExplorationStrategy actionNoise) {
        return new MLPDeterministicPolicy(wrapper, observationSize, DEFAULT_ACTOR_HIDDEN_LAYERS, ACTION_SIZE, -speed, speed, actionNoise);
    }
    
    /**
     * Creates an actor producing two-dimensional movement vectors bounded by
     * the hunter's maximum speed.
     * 
     * <p> The continuous action exploration strategy is null, meaning no exploration noise is added to the actor's output. 
     * </p>
     *
     * @param wrapper the observation vector wrapper
     * @param observationSize the observation vector size
     * @param speed the maximum speed of the hunter
     * @return a deterministic continuous actor
     */
    protected MLPDeterministicPolicy createActor(WrapperPreyHunterObservationVector wrapper, int observationSize, double speed) {
        return new MLPDeterministicPolicy(wrapper, observationSize, DEFAULT_ACTOR_HIDDEN_LAYERS, ACTION_SIZE, -speed, speed, null);
    }

    /**
     * Selects a continuous action and converts it into the movement type
     * expected by the PreyHunter environment.
     *
     * <p>The conversion preserves both continuous action components without
     * rounding or normalization.</p>
     *
     * @param input the current policy input
     * @return the corresponding continuous two-dimensional movement
     * @throws IllegalStateException if the policy does not return an
     *                               {@link ActionContinuousVector}
     */
    @Override
    public Action selectAction(PolicyInput input) {
        Action action = super.selectAction(input);

        if (!(action instanceof ActionContinuousVector vector)) {
            throw new IllegalStateException("HunterAgentDDPG requires its policy to produce ActionContinuousVector actions.");
        }

        if (vector.getSize() != ACTION_SIZE) {
            throw new IllegalStateException("HunterAgentDDPG requires action vectors of size 2.");
        }

        return Move2DDouble.fromActionContinuousVector(vector);
    }
}