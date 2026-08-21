package marlkit.preyhunter.agent;

import java.util.List;
import java.util.Objects;

import agent.modelofotheragent.StandardGroupModelingPredictAction;
import centralizedtraining.CentralizedCriticTrainingExecutionStrategy;
import environment.observation.wrapperobservationvector.WrapperJointObservation;
import learning.ContinuousActionExplorationStrategy;
import learning.actionexplorationstrategies.GaussianNoise;
import learning.algorithms.MADDPG;
import learning.nn.ActionValueCritic;
import learning.policies.MLPDeterministicPolicy;
import marlkit.preyhunter.environment.WrapperPreyHunterObservationVector;
import modelofotheragents.AccessOtherPolicy;
import util.criteria.ReadOnlyCriterion;

/**
 * Hunter agent using MADDPG with a decentralized deterministic actor and a
 * centralized action-value critic.
 *
 * <p>Each hunter executes actions from its local observation. During training,
 * its critic receives the mapped observations and actions of all hunters.</p>
 */
public class HunterAgentMADDPG extends HunterAgentDDPG {

    private final MLPDeterministicPolicy targetActor;

    /**
     * Creates a MADDPG hunter.
     *
     * <p>The action predictor must be configured before activation by calling
     * {@link #setOtherAgents(List)}.</p>
     *
     * @param maxVisibleHunters the maximum number of other hunters represented
     *                          in each local observation
     * @param maxVisiblePreys the maximum number of preys represented in each
     *                        local observation
     * @param speed the maximum movement speed
     * @param numberOfHunters the number of hunters represented in centralized
     *                        observations and actions
     */
    public HunterAgentMADDPG(int maxVisibleHunters, int maxVisiblePreys, double speed, int numberOfHunters, ReadOnlyCriterion readOnlyEvaluationCriterion) {
        super();

        if (numberOfHunters <= 0) {
            throw new IllegalArgumentException("numberOfHunters must be strictly positive.");
        }

        WrapperPreyHunterObservationVector actorWrapper = new WrapperPreyHunterObservationVector(maxVisibleHunters, maxVisiblePreys);
        int actorObservationSize = actorWrapper.getVectorSize();

        ContinuousActionExplorationStrategy actionNoiseStrategy = new GaussianNoise(speed * NOISE_COEFFFICIENT);

        MLPDeterministicPolicy actor = createActor(actorWrapper, actorObservationSize, speed, actionNoiseStrategy);
        this.targetActor = createActor(actorWrapper, actorObservationSize, speed);

        WrapperJointObservation criticWrapper = new WrapperJointObservation(actorWrapper, actorObservationSize, numberOfHunters);
        int criticObservationSize = criticWrapper.getVectorSize();
        int criticActionSize = ACTION_SIZE * numberOfHunters;

        ActionValueCritic critic = new ActionValueCritic(criticObservationSize, criticActionSize, DEFAULT_CRITIC_HIDDEN_SIZE, criticWrapper);
        ActionValueCritic targetCritic = new ActionValueCritic(criticObservationSize, criticActionSize, DEFAULT_CRITIC_HIDDEN_SIZE, criticWrapper);

        MADDPG maddpg = new MADDPG(actor, targetActor, critic, targetCritic, DEFAULT_ACTOR_LEARNING_RATE, DEFAULT_CRITIC_LEARNING_RATE, DEFAULT_GAMMA, DEFAULT_TAU, DEFAULT_LEARNING_BATCH_SIZE, DEFAULT_REPLAY_BUFFER_CAPACITY);

        actor.setEvaluationCriterion(readOnlyEvaluationCriterion);
        maddpg.setEvaluationCriterion(readOnlyEvaluationCriterion);
        
        setPolicy(actor);
        setAlgorithm(maddpg);
    }

    /**
     * Configures access to the target actors of all hunters.
     *
     * <p>The iteration order of the supplied list defines the prediction order.
     * It must therefore match the stable order used to construct mapped joint
     * observations and actions.</p>
     *
     * <p>The current hunter is also registered. This ensures that replacing its
     * predicted action later does not change its insertion position in the
     * mapped joint action.</p>
     *
     * @param hunters the ordered list of MADDPG hunters
     * @throws NullPointerException if the list or one of its hunters is null
     */
    public void setOtherAgents(List<? extends HunterAgentMADDPG> hunters) {
        Objects.requireNonNull(hunters, "hunters");

        StandardGroupModelingPredictAction actionsPredictor = new StandardGroupModelingPredictAction();

        for (HunterAgentMADDPG hunter : hunters) {
            Objects.requireNonNull(hunter, "hunter");

            actionsPredictor.addModelPredictAction(hunter, new AccessOtherPolicy(hunter.getTargetActor()));
        }

        ((MADDPG) getAlgorithm()).setActionsPredictor(actionsPredictor);
    }

    /**
     * Returns this hunter's target actor.
     *
     * @return the target actor
     */
    public MLPDeterministicPolicy getTargetActor() {
        return targetActor;
    }

    /**
     * Returns this hunter's MADDPG algorithm.
     *
     * @return the MADDPG algorithm
     */
    public MADDPG getMADDPG() {
        return (MADDPG) getAlgorithm();
    }

    /**
     * Requests the role used for centralized-critic experience collection.
     */
    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), CentralizedCriticTrainingExecutionStrategy.CENTRALIZED_CRITIC_AGENT_ROLE);
    }
}