package learning.algorithms;

import java.util.Arrays;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.ActionContinuousVector;
import agent.action.JointAction;
import agent.action.MappedJointAction;
import experience.Experience;
import experience.TransitionExperience;
import learning.Batch;
import learning.Critic;
import learning.Policy;
import learning.nn.ActionValueCritic;
import learning.policies.DeterministicPolicyGradient;
import learning.policies.PolicyInput;
import madkit.kernel.AgentLogger;

/**
 * Implements the Deep Deterministic Policy Gradient algorithm for continuous
 * action spaces.
 *
 * <p>DDPG combines a deterministic actor with an action-value critic. The
 * critic learns {@code Q(o, a)} from transition experiences, while the actor
 * learns to produce actions that maximize the value estimated by the critic.</p>
 *
 * <p>Target copies of the actor and critic are used to compute stable temporal
 * difference targets. Their parameters are updated through soft updates after
 * each learning batch.</p>
 *
 * <p>This class does not manage the replay buffer itself. The supplied
 * {@link Batch} is expected to contain transition experiences sampled from a
 * replay buffer.</p>
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
     * @param targetActor the target copy of the actor
     * @param critic the action-value critic to train
     * @param targetCritic the target copy of the critic
     * @param actorLearningRate the actor learning rate
     * @param criticLearningRate the critic learning rate
     * @param gamma the reward discount factor
     * @param tau the target-network soft-update coefficient
     * @throws NullPointerException if an actor or critic is {@code null}
     * @throws IllegalArgumentException if a learning rate is not positive, if
     *                                  {@code gamma} is outside {@code [0, 1]},
     *                                  or if {@code tau} is outside
     *                                  {@code (0, 1]}
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

        if (replayBufferCapacity < learningBatchSize) {
            throw new IllegalArgumentException("replayBufferCapacity must be greater than or equal to learningBatchSize.");
        }
        this.learningBatchSize = learningBatchSize;
        this.replayBufferCapacity = replayBufferCapacity;
        
        
        validateParameters();
    }

    /**
     * Creates a DDPG algorithm with default hyperparameters.
     *
     * <p>The default values are:</p>
     * <ul>
     *   <li>actor learning rate: {@code 0.0001}</li>
     *   <li>critic learning rate: {@code 0.001}</li>
     *   <li>discount factor: {@code 0.99}</li>
     *   <li>soft-update coefficient: {@code 0.005}</li>
     * </ul>
     *
     * @param actor the deterministic actor to train
     * @param targetActor the target copy of the actor
     * @param critic the action-value critic to train
     * @param targetCritic the target copy of the critic
     */
    public DDPG(DeterministicPolicyGradient actor, DeterministicPolicyGradient targetActor, ActionValueCritic critic, ActionValueCritic targetCritic) {
        this(actor, targetActor, critic, targetCritic, 0.0001, 0.001, 0.99, 0.005, 64, 100000);
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

    /**
     * Returns the actor used by the algorithm.
     *
     * @return the deterministic actor
     */
    @Override
    public DeterministicPolicyGradient getPolicy() {
        return actor;
    }

    /**
     * Returns the actor used by the algorithm.
     *
     * @return the deterministic actor
     */
    @Override
    public DeterministicPolicyGradient getActor() {
        return actor;
    }

    /**
     * Returns the action-value critic used by the algorithm.
     *
     * @return the action-value critic
     */
    @Override
    public Critic getCritic() {
        return critic;
    }

    /**
     * Associates this algorithm with an agent.
     *
     * @param agent the agent using this algorithm
     */
    @Override
    public void setAgent(MLKAgent agent) {
        this.agent = Objects.requireNonNull(agent, "agent");
    }

    /**
     * Returns the agent using this algorithm.
     *
     * @return the associated agent
     */
    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    /**
     * Returns the learning frequency.
     *
     * @return A {@code 10} indicating that learning may occur every {@code 10} steps
     */
    @Override
    public int getLearningFrequency() {
        return 4;
    }
    
    /**
     * Indicates whether enough experiences have been accumulated to perform a DDPG
     * update.
     *
     * @param timestep the current simulation step
     * @param batch the batch of accumulated transition experiences
     * @return {@code true} when the batch contains enough transitions
     */
    @Override
    public boolean shouldLearn(int timestep, Batch batch) {
        return batch.size() >= learningBatchSize && (timestep % getLearningFrequency() == 0);
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
        batch.retainLatest(replayBufferCapacity);

        if (batch.size() < learningBatchSize) {
            return;
        }

        Batch sampledBatch = batch.sample(learningBatchSize, pnrg());
        learnOnSample(sampledBatch, logger);

    }
    
    /**
     * Computes all critic and actor gradients for a sampled mini-batch, then
     * performs one batch update of the critic and one batch update of the actor.
     *
     * @param sampledBatch the mini-batch sampled from the replay buffer
     * @param logger the agent logger used for profiling information
     */
    private void learnOnSample(Batch sampledBatch, AgentLogger logger) {

        int batchSize = sampledBatch.size();

        PolicyInput[] actorObservations = new PolicyInput[batchSize];
        
        PolicyInput[] criticObservations = new PolicyInput[batchSize];
        Action[] criticSourceActions = new Action[batchSize];
        ActionContinuousVector[] criticActions = new ActionContinuousVector[batchSize];
        double[] targetValues = new double[batchSize];

        int index = 0;

        for (Experience experience : sampledBatch.getExperiences()) {
        	TransitionExperience actorTransition = requireTransition(experience);
        	
        	Experience criticExperience = getCritic().getEnrichedExperience(experience);
            TransitionExperience criticTransition = requireTransition(criticExperience);

            actorObservations[index] = actorTransition.getInput();
            
            criticObservations[index] = criticTransition.getInput();
            criticSourceActions[index] = criticTransition.getAction();
            criticActions[index] = requireContinuousAction(criticTransition);
            targetValues[index] = computeTargetValue(criticTransition, criticTransition.getNextObservation());

            index++;
        }

        critic.updateTowardTargets(criticObservations, criticActions, targetValues, criticLearningRate);
        ActionContinuousVector[] actorActions = actor.forwardActions(actorObservations);
        
        double[][] actorLossGradients = new double[batchSize][];

        for (int i = 0; i < batchSize; i++) {
        	Action criticSourceAction = criticSourceActions[i];
        	
        	if (criticSourceAction instanceof MappedJointAction mappedJointAction) {
                MappedJointAction actorJointAction = mappedJointAction.withAction(getAgent(), actorActions[i]);
                double[] localActionGradient = critic.actionGradient(criticObservations[i], actorJointAction, getAgent());
                actorLossGradients[i] = negate(localActionGradient);
            } else if (criticSourceAction instanceof ActionContinuousVector) {
                double[] localActionGradient = critic.actionGradient(criticObservations[i], actorActions[i]);
                actorLossGradients[i] = negate(localActionGradient);
            } else {
                throw new IllegalArgumentException("DDPG requires ActionContinuousVector or MappedJointAction actions.");
            }
        	
        }

        actor.updateFromActionGradient(actorObservations, actorLossGradients, actorLearningRate);

        softUpdateTargetNetworks();
        
    }


    /**
     * Computes the temporal-difference target for one transition.
     *
     * @param transition the current transition
     * @param nextObservation the next observation
     * @return the temporal-difference target
     */
    private double computeTargetValue(TransitionExperience transition, PolicyInput nextObservation) {
        double reward = transition.getRewardValue();

        if (transition.isTerminal()) {
            return reward;
        }

        ActionContinuousVector nextAction = targetActor.forwardAction(nextObservation);
        double nextValue = targetCritic.getValue(nextObservation, nextAction);

        return reward + gamma * nextValue;
    }

    /**
     * Converts an experience to a transition experience.
     *
     * @param experience the experience to validate
     * @return the validated transition experience
     * @throws IllegalArgumentException if the experience is not a transition
     */
    private TransitionExperience requireTransition(Experience experience) {
        if (!(experience instanceof TransitionExperience transition)) {
            throw new IllegalArgumentException("DDPG requires TransitionExperience instances.");
        }

        return transition;
    }

    /**
     * Returns the continuous action stored in a transition.
     *
     * @param transition the transition containing the action
     * @return the continuous action
     * @throws IllegalArgumentException if the action is not continuous
     */
    private ActionContinuousVector requireContinuousAction(TransitionExperience transition) {
    	Action action = transition.getAction();
	    if (action instanceof JointAction jointAction) {
	    	try {
	    		action = ActionContinuousVector.fromJointAction(jointAction);
	    	} catch (IllegalArgumentException e) {
	    		throw new IllegalArgumentException("DDPG requires non-empty JointAction that contain only ActionContinuousVector actions.");
	    	}	
    	}
        if (action instanceof ActionContinuousVector actionContinuousVector) {
        	return actionContinuousVector;
		} 
        throw new IllegalArgumentException("DDPG requires ActionContinuousVector actions or continuous JointAction actions.");


    }

    /**
     * Negates every component of a gradient vector.
     *
     * <p>DDPG maximizes the critic value, while the neural network applies
     * gradient descent. Consequently, the negative action-value gradient is
     * used as the actor loss gradient.</p>
     *
     * @param gradient the gradient to negate
     * @return the negated gradient
     */
    private double[] negate(double[] gradient) {
        return Arrays.stream(gradient).map(value -> -value).toArray();
    }

    /**
     * Copies the main actor and critic parameters to their target networks.
     */
    private void copyMainNetworksToTargets() {
        targetActor.setParameters(actor.getParameters());
        targetCritic.setParameters(critic.getParameters());
    }

    /**
     * Applies a soft update to both target networks.
     *
     * <p>Each target parameter is updated according to:</p>
     *
     * <pre>
     * target = tau * source + (1 - tau) * target
     * </pre>
     */
    private void softUpdateTargetNetworks() {
        softUpdate(actor.getParameters(), targetActor.getParameters(), targetActor);
        softUpdate(critic.getParameters(), targetCritic.getParameters(), targetCritic);
    }

    /**
     * Computes and assigns the soft-updated parameters of a target component.
     *
     * @param sourceParameters the trainable component parameters
     * @param targetParameters the current target component parameters
     * @param target the target component receiving the updated parameters
     * @throws IllegalArgumentException if the parameter arrays have different
     *                                  sizes
     */
    private void softUpdate(double[] sourceParameters, double[] targetParameters, learning.policies.Parameterized target) {
        if (sourceParameters.length != targetParameters.length) {
            throw new IllegalArgumentException("Source and target parameter sizes must match.");
        }

        double[] updatedParameters = new double[sourceParameters.length];

        for (int i = 0; i < sourceParameters.length; i++) {
            updatedParameters[i] = tau * sourceParameters[i] + (1.0 - tau) * targetParameters[i];
        }

        target.setParameters(updatedParameters);
    }

    /**
     * Performs no episode-specific learning.
     *
     * <p>DDPG is an off-policy algorithm and normally learns from replay-buffer
     * samples during the episode. The supplied batch is therefore not cleared
     * by this method.</p>
     *
     * @param batch the current batch
     * @param logger the agent logger
     */
    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
    	actor.updateExplorationStrategy();
    }

    /**
     * Validates the algorithm hyperparameters.
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
    }
}