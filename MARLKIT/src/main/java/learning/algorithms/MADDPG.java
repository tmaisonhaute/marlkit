package learning.algorithms;

import agent.action.Action;
import agent.action.ActionContinuousVector;
import agent.action.MappedJointAction;
import agent.modelofotheragent.GroupModelPredictAction;
import environment.observation.MappedJointObservation;
import environment.observation.Observation;
import experience.TransitionExperience;
import learning.nn.ActionValueCritic;
import learning.policies.DeterministicPolicyGradient;
import madkit.simulation.SimuAgent;

/**
 * Implements the multi-agent extension of DDPG with decentralized actors and a
 * centralized action-value critic.
 *
 * <p>The centralized critic receives the ordered observations and actions of
 * all agents. The local actor continues to receive only its own observation.</p>
 *
 * <p>The target actions of the other agents may be supplied by a configured
 * group action predictor. Without a predictor, their historical actions are
 * reused as an approximation.</p>
 */
public class MADDPG extends DDPG {
	protected GroupModelPredictAction actionsPredictor;

    /**
     * Creates a MADDPG algorithm.
     *
     * @param actor the local deterministic actor
     * @param targetActor the local target actor
     * @param critic the centralized critic
     * @param targetCritic the centralized target critic
     * @param actorLearningRate the actor learning rate
     * @param criticLearningRate the critic learning rate
     * @param gamma the reward discount factor
     * @param tau the target-network soft-update coefficient
     * @param learningBatchSize the mini-batch size
     * @param replayBufferCapacity the replay-buffer capacity
     */
    public MADDPG(DeterministicPolicyGradient actor, DeterministicPolicyGradient targetActor, ActionValueCritic critic, ActionValueCritic targetCritic, double actorLearningRate, double criticLearningRate, double gamma, double tau, int learningBatchSize, int replayBufferCapacity) {
        super(actor, targetActor, critic, targetCritic, actorLearningRate, criticLearningRate, gamma, tau, learningBatchSize, replayBufferCapacity);
    }

    /**
     * Creates a MADDPG algorithm with the default DDPG hyperparameters.
     *
     * @param actor the local deterministic actor
     * @param targetActor the local target actor
     * @param critic the centralized critic
     * @param targetCritic the centralized target critic
     */
    public MADDPG(DeterministicPolicyGradient actor, DeterministicPolicyGradient targetActor, ActionValueCritic critic, ActionValueCritic targetCritic) {
        super(actor, targetActor, critic, targetCritic);
    }
    
    
    /**
     * Sets the group action predictor used to construct joint target actions.
     *
     * <p>The predictor may directly access the other agents' target actors or use
     * learned models of their policies. MADDPG only consumes its predictions and
     * does not update the predictor.</p>
     *
     * @param actionsPredictor the group action predictor
     */
    public void setActionsPredictor(GroupModelPredictAction actionsPredictor) {
        this.actionsPredictor = actionsPredictor;
    }

    /**
     * Returns the group action predictor used by MADDPG.
     *
     * @return the group action predictor, or {@code null} if none has been configured
     */
    public GroupModelPredictAction getActionsPredictor() {
        return actionsPredictor;
    }
    
    /**
     * Converts a centralized critic action into a continuous joint-action vector.
     *
     * @param action the centralized action to convert
     * @return the continuous vector containing all individual actions
     * @throws IllegalArgumentException if the action is not a mapped joint action
     *                                  or contains non-continuous actions
     */
    @Override
    protected ActionContinuousVector requireContinuousAction(Action action) {
        if (!(action instanceof MappedJointAction mappedJointAction)) {
            throw new IllegalArgumentException("MADDPG requires MappedJointAction critic actions.");
        }

        return ActionContinuousVector.fromJointAction(mappedJointAction);
    }

    /**
     * Computes a MADDPG TD target using the centralized next observation and a
     * joint target action.
     *
     * @param actorTransition the local transition of the current agent
     * @param criticTransition the centralized transition
     * @return the centralized TD target
     */
    @Override
    protected double computeTargetValue(TransitionExperience actorTransition, TransitionExperience criticTransition) {
        double reward = criticTransition.getRewardValue();

        if (criticTransition.isTerminal()) {
            return reward;
        }

        if (!(criticTransition.getNextObservation() instanceof MappedJointObservation jointNextObservation)) {
            throw new IllegalArgumentException("MADDPG requires a MappedJointObservation as the critic next observation.");
        }

        MappedJointAction jointTargetAction = buildJointTargetAction(criticTransition, jointNextObservation, actorTransition.getNextObservation());
        ActionContinuousVector jointTargetActionVector = ActionContinuousVector.fromJointAction(jointTargetAction);
        double nextValue = getTargetCritic().getValue(jointNextObservation, jointTargetActionVector);

        return reward + getGamma() * nextValue;
    }

    /**
     * Computes the local actor loss gradient from the centralized critic.
     *
     * <p>The current local actor action replaces the corresponding historical
     * action in the joint action. The critic then returns only the gradient
     * associated with the current agent.</p>
     *
     * @param actorObservation the local actor observation
     * @param criticObservation the centralized critic observation
     * @param criticSourceAction the historical joint action
     * @param actorAction the current local action produced by the actor
     * @return the local actor loss gradient
     */
    @Override
    protected double[] computeActorLossGradient(Observation actorObservation, Observation criticObservation, Action criticSourceAction, ActionContinuousVector actorAction) {
        if (!(criticSourceAction instanceof MappedJointAction mappedJointAction)) {
            throw new IllegalArgumentException("MADDPG requires MappedJointAction critic actions.");
        }

        MappedJointAction actorJointAction = mappedJointAction.withAction(getAgent(), actorAction);
        double[] localActionGradient = getActionValueCritic().actionGradient(criticObservation, actorJointAction, getAgent());

        return negate(localActionGradient);
    }

    /**
     * Builds the joint target action used by the centralized target critic.
     *
     * <p>If an action predictor is configured, its prediction is used as the base
     * joint action. Otherwise, the historical joint action stored in the
     * centralized transition is used as an approximation. The current agent's
     * action is then replaced by the output of its target actor.</p>
     *
     * @param criticTransition the centralized transition
     * @param jointNextObservation the mapped next observations of all agents
     * @param actorNextObservation the local next observation of the current agent
     * @return the joint target action
     * @throws IllegalArgumentException if the centralized transition does not contain a mapped joint action
     */
    protected MappedJointAction buildJointTargetAction(TransitionExperience criticTransition, MappedJointObservation jointNextObservation, Observation actorNextObservation) {
        MappedJointAction jointTargetAction;

        if (actionsPredictor != null) {
            jointTargetAction = actionsPredictor.predictActionFromMappedObservation(jointNextObservation);
        }
        else {
        	((SimuAgent) getAgent()).getLogger().warning("No action predictor configured for MADDPG. Using historical joint actions as an approximation.");
            if (!(criticTransition.getAction() instanceof MappedJointAction historicalJointAction)) {
                throw new IllegalArgumentException("MADDPG requires a MappedJointAction in the centralized transition.");
            }

            jointTargetAction = historicalJointAction.copy();
        }

        ActionContinuousVector localTargetAction = getTargetActor().forwardAction(actorNextObservation);
        return jointTargetAction.withAction(getAgent(), localTargetAction);
    }
}



