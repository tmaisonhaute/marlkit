package learning.algorithms;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.ActionContinuousVector;
import environment.observation.Observation;
import experience.Experience;
import experience.TransitionExperience;
import learning.Batch;
import learning.Critic;
import learning.Policy;
import learning.nn.ActionValueCritic;
import learning.policies.DeterministicPolicyGradient;
import learning.policies.Parameterized;
import madkit.kernel.AgentLogger;

/**
 * Implements Deep Deterministic Policy Gradient with an independent
 * action-value critic.
 *
 * <p>The actor and critic use local observations and local continuous actions.
 * Multi-agent centralized-critic behavior is implemented separately by
 * {@link MADDPG}.</p>
 */
public class DDPG implements ActorCritic {

    private final double gamma;
    private final double tau;
    private final double actorLearningRate;
    private final double criticLearningRate;
    private final int learningBatchSize;
    private final int replayBufferCapacity;

    private MLKAgent agent;

    private DeterministicPolicyGradient actor;
    private final DeterministicPolicyGradient targetActor;

    private final ActionValueCritic critic;
    private final ActionValueCritic targetCritic;

    /**
     * Creates a DDPG algorithm.
     *
     * @param actor the deterministic actor to train
     * @param targetActor the target actor
     * @param critic the action-value critic to train
     * @param targetCritic the target action-value critic
     * @param actorLearningRate the actor learning rate
     * @param criticLearningRate the critic learning rate
     * @param gamma the reward discount factor
     * @param tau the target-network soft-update coefficient
     * @param learningBatchSize the sampled mini-batch size
     * @param replayBufferCapacity the maximum replay-buffer capacity
     */
    public DDPG(DeterministicPolicyGradient actor, DeterministicPolicyGradient targetActor, 
    		ActionValueCritic critic, ActionValueCritic targetCritic, 
    		double actorLearningRate, double criticLearningRate, double gamma, double tau, int learningBatchSize, int replayBufferCapacity) {
        this.actor = Objects.requireNonNull(actor, "actor");
        this.targetActor = Objects.requireNonNull(targetActor, "targetActor");
        this.critic = Objects.requireNonNull(critic, "critic");
        this.targetCritic = Objects.requireNonNull(targetCritic, "targetCritic");
        this.actorLearningRate = actorLearningRate;
        this.criticLearningRate = criticLearningRate;
        this.gamma = gamma;
        this.tau = tau;
        this.learningBatchSize = learningBatchSize;
        this.replayBufferCapacity = replayBufferCapacity;

        validateParameters();
    }

    /**
     * Creates a DDPG algorithm with default hyperparameters.
     *
     * @param actor the deterministic actor to train
     * @param targetActor the target actor
     * @param critic the action-value critic to train
     * @param targetCritic the target action-value critic
     */
    public DDPG(DeterministicPolicyGradient actor, DeterministicPolicyGradient targetActor, ActionValueCritic critic, ActionValueCritic targetCritic) {
        this(actor, targetActor, critic, targetCritic, 0.0001, 0.001, 0.99, 0.005, 64, 100_000);
    }

    /**
     * Initializes the algorithm and its critics for the specified agent.
     *
     * <p>The main actor is expected to have already been initialized as the
     * agent's policy. The target networks are initialized by copying the
     * parameters of their corresponding main networks.</p>
     *
     * @param agent the agent using this algorithm
     */
    @Override
    public void init(MLKAgent agent) {
        setAgent(agent);
        critic.init(agent);
        targetActor.init(agent);
        targetCritic.init(agent);
        copyMainNetworksToTargets();
    }

    /**
     * Replaces the actor used by this algorithm.
     *
     * @param policy the new deterministic policy
     * @throws IllegalArgumentException if the policy does not implement
     *                                  {@link DeterministicPolicyGradient}
     */
    @Override
    public void setPolicy(Policy policy) {
        if (!(policy instanceof DeterministicPolicyGradient deterministicPolicy)) {
            throw new IllegalArgumentException("DDPG requires a DeterministicPolicyGradient.");
        }

        actor = deterministicPolicy;

        if (agent != null) {
            actor.init(agent);
            targetActor.setParameters(actor.getParameters());
        }
    }

    @Override
    public DeterministicPolicyGradient getPolicy() {
        return actor;
    }

    @Override
    public DeterministicPolicyGradient getActor() {
        return actor;
    }

    @Override
    public Critic getCritic() {
        return critic;
    }

    /**
     * Returns the action-value critic used by DDPG.
     *
     * @return the action-value critic
     */
    protected ActionValueCritic getActionValueCritic() {
        return critic;
    }

    /**
     * Returns the target action-value critic.
     *
     * @return the target critic
     */
    protected ActionValueCritic getTargetCritic() {
        return targetCritic;
    }

    /**
     * Returns the target deterministic actor.
     *
     * @return the target actor
     */
    protected DeterministicPolicyGradient getTargetActor() {
        return targetActor;
    }

    /**
     * Returns the reward discount factor.
     *
     * @return the reward discount factor
     */
    protected double getGamma() {
        return gamma;
    }

    @Override
    public void setAgent(MLKAgent agent) {
        this.agent = Objects.requireNonNull(agent, "agent");
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    /**
     * Returns the learning frequency.
     *
     * @return a value {@code X} indicating that learning may occur every {@code X} steps
     */
    @Override
    public int getLearningFrequency() {
        return 4;
    }

    /**
     * Indicates whether enough experiences have been accumulated and whether the
     * current timestep permits a DDPG update.
     *
     * @param timestep the current simulation step
     * @param batch the batch of accumulated transition experiences
     * @return {@code true} when the batch contains enough transitions and the learning frequency condition is satisfied
    **/
    @Override
    public boolean shouldLearn(int timestep, Batch batch) {
        return batch.size() >= learningBatchSize && timestep % getLearningFrequency() == 0;
    }

    /**
     * Updates the critic and actor from a batch of transition experiences.
     *
     * <p>For each transition, the critic target is:</p>
     *
     * <pre>
     * y = r                                      if terminal
     * y = r + gamma * Q_target(o', actor_target(o')) otherwise
     * </pre>
     *
     * <p>The actor is then updated using the deterministic policy gradient
     * derived from the current critic.</p>
     *
     * @param batch the batch of transition experiences
     * @param logger the agent logger
     * @throws IllegalArgumentException if the batch contains an experience that
     *                                  is not a {@link TransitionExperience} or
     *                                  an action that is not an
     *                                  {@link ActionContinuousVector}
     */
    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        List<Experience> removedExperiences = batch.retainLatest(replayBufferCapacity);

        for (Experience removedExperience : removedExperiences) {
            critic.removeEnrichedExperience(removedExperience);
        }

        if (batch.size() < learningBatchSize) {
            return;
        }

        Batch sampledBatch = batch.sample(learningBatchSize, pnrg());
        learnOnSample(sampledBatch, logger);
    }

    /**
     * Performs one critic batch update and one actor batch update.
     *
     * @param sampledBatch the mini-batch sampled from the replay buffer
     * @param logger the agent logger
     */
    protected void learnOnSample(Batch sampledBatch, AgentLogger logger) {
        int batchSize = sampledBatch.size();

        Observation[] actorObservations = new Observation[batchSize];
        Observation[] criticObservations = new Observation[batchSize];
        Action[] criticSourceActions = new Action[batchSize];
        ActionContinuousVector[] criticActions = new ActionContinuousVector[batchSize];
        double[] targetValues = new double[batchSize];

        int index = 0;

        for (Experience experience : sampledBatch.getExperiences()) {
            TransitionExperience actorTransition = requireTransition(experience);
            TransitionExperience criticTransition = requireTransition(getCriticExperience(experience));

            actorObservations[index] = actorTransition.getObservation();
            criticObservations[index] = criticTransition.getObservation();
            criticSourceActions[index] = criticTransition.getAction();
            criticActions[index] = requireContinuousAction(criticTransition.getAction());
            targetValues[index] = computeTargetValue(actorTransition, criticTransition);

            index++;
        }

        critic.updateTowardTargets(criticObservations, criticActions, targetValues, criticLearningRate);

        ActionContinuousVector[] actorActions = actor.forwardActions(actorObservations);
        double[][] actorLossGradients = new double[batchSize][];

        for (int i = 0; i < batchSize; i++) {
            actorLossGradients[i] = computeActorLossGradient(actorObservations[i], criticObservations[i], criticSourceActions[i], actorActions[i]);
        }

        actor.updateFromActionGradient(actorObservations, actorLossGradients, actorLearningRate);
        softUpdateTargetNetworks();
    }

    /**
     * Returns the critic-specific experience associated with an original
     * experience.
     *
     * <p>An independent critic normally returns the original experience.</p>
     *
     * @param originalExperience the original local experience
     * @return the experience used by the critic
     */
    protected Experience getCriticExperience(Experience originalExperience) {
        return critic.getEnrichedExperience(originalExperience);
    }

    /**
     * Computes the TD target for an independent critic.
     *
     * @param actorTransition the local transition used by the actor
     * @param criticTransition the transition used by the critic
     * @return the TD target
     */
    protected double computeTargetValue(TransitionExperience actorTransition, TransitionExperience criticTransition) {
        double reward = criticTransition.getRewardValue();

        if (criticTransition.isTerminal()) {
            return reward;
        }

        ActionContinuousVector nextAction = targetActor.forwardAction(actorTransition.getNextObservation());
        double nextValue = targetCritic.getValue(criticTransition.getNextObservation(), nextAction);

        return reward + gamma * nextValue;
    }

    /**
     * Computes the loss gradient used to update the independent actor.
     *
     * @param actorObservation the local actor observation
     * @param criticObservation the observation used by the critic
     * @param criticSourceAction the historical action stored for the critic
     * @param actorAction the current action produced by the actor
     * @return the actor loss gradient with respect to its action
     */
    protected double[] computeActorLossGradient(Observation actorObservation, Observation criticObservation, Action criticSourceAction, ActionContinuousVector actorAction) {
        double[] actionGradient = critic.actionGradient(criticObservation, actorAction);
        return negate(actionGradient);
    }

    /**
     * Converts an experience into a transition experience.
     *
     * @param experience the experience to validate
     * @return the validated transition
     */
    protected TransitionExperience requireTransition(Experience experience) {
        if (!(experience instanceof TransitionExperience transition)) {
            throw new IllegalArgumentException("DDPG requires TransitionExperience instances.");
        }

        return transition;
    }

    /**
     * Converts an action into a continuous action vector.
     *
     * @param action the action to convert
     * @return the continuous action vector
     */
    protected ActionContinuousVector requireContinuousAction(Action action) {
        if (action instanceof ActionContinuousVector actionContinuousVector) {
            return actionContinuousVector;
        }

        throw new IllegalArgumentException("DDPG requires ActionContinuousVector actions.");
    }

    /**
     * Negates every component of a gradient.
     *
     * @param gradient the gradient to negate
     * @return the negated gradient
     */
    protected double[] negate(double[] gradient) {
        return Arrays.stream(gradient).map(value -> -value).toArray();
    }

    /**
     * Copies the main network parameters into their target networks.
     */
    protected void copyMainNetworksToTargets() {
        targetActor.setParameters(actor.getParameters());
        targetCritic.setParameters(critic.getParameters());
    }

    /**
     * Applies a soft update to the actor and critic target networks.
     */
    protected void softUpdateTargetNetworks() {
        softUpdate(actor.getParameters(), targetActor.getParameters(), targetActor);
        softUpdate(critic.getParameters(), targetCritic.getParameters(), targetCritic);
    }

    /**
     * Softly updates one target component.
     *
     * @param sourceParameters the main component parameters
     * @param targetParameters the current target parameters
     * @param target the target component to update
     */
    protected void softUpdate(double[] sourceParameters, double[] targetParameters, Parameterized target) {
        if (sourceParameters.length != targetParameters.length) {
            throw new IllegalArgumentException("Source and target parameter sizes must match.");
        }

        double[] updatedParameters = new double[sourceParameters.length];

        for (int i = 0; i < sourceParameters.length; i++) {
            updatedParameters[i] = tau * sourceParameters[i] + (1.0 - tau) * targetParameters[i];
        }

        target.setParameters(updatedParameters);
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        actor.updateExplorationStrategy();
    }

    /**
     * Validates the DDPG hyperparameters.
     */
    private void validateParameters() {
        if (actorLearningRate <= 0.0) {
            throw new IllegalArgumentException("actorLearningRate must be greater than 0.");
        }
        if (criticLearningRate <= 0.0) {
            throw new IllegalArgumentException("criticLearningRate must be greater than 0.");
        }
        if (gamma < 0.0 || gamma > 1.0) {
            throw new IllegalArgumentException("gamma must be in [0, 1].");
        }
        if (tau <= 0.0 || tau > 1.0) {
            throw new IllegalArgumentException("tau must be in (0, 1].");
        }
        if (learningBatchSize <= 0) {
            throw new IllegalArgumentException("learningBatchSize must be strictly positive.");
        }
        if (replayBufferCapacity < learningBatchSize) {
            throw new IllegalArgumentException("replayBufferCapacity must be greater than or equal to learningBatchSize.");
        }
    }
}