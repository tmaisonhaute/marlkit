package learning.policy;

import java.util.List;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.wrapperactionvector.WrapperAction2DMoveVector;
import agent.action.wrapperactionvector.WrapperActionVector;
import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;
import environment.observation.wrapperobservationvector.WrapperObservationVectorPositionsValues;
import learning.Batch;
import learning.Experience;
import learning.nn.ActorNetwork;
import learning.nn.CriticNetwork;
import madkit.kernel.AgentLogger;

/**
 * Actor-Critic reinforcement learning policy implementation.
 * <p>
 * This policy uses two neural networks: an actor network that learns the policy
 * (action selection) and a critic network that estimates state values. The critic
 * provides TD-error signals to guide the actor's learning.
 * </p>
 * <p>
 * The algorithm performs online learning, updating both networks after each step
 * using temporal difference (TD) learning.
 * </p>
 *
 * @see ActorNetwork
 * @see CriticNetwork
 * @see Policy
 */
public class PolicyActorCritic implements Policy {
	private MLKAgent agent;

    private CriticNetwork critic;
    private ActorNetwork actor;
    
    
    private List<Action> actionsSet;
    private double gamma;
    private double totalRewards;
    
    // Wrappers for converting observations and actions to vectors
    private WrapperObservationVector observationWrapper;
    private WrapperActionVector actionWrapper;

	private int inputSize;

	private int hiddenSize;
    
    /**
     * Creates an Actor-Critic policy with the specified action set and input size.
     * <p>
     * Uses default wrappers for observation and action conversion to vectors,
     * a discount factor (gamma) of 0.95, and a hidden layer size of 64.
     * </p>
     *
     * @param actionsSet the list of possible actions the agent can take
     * @param inputSize  the size of the observation vector (neural network input)
     */
    public PolicyActorCritic(List<Action> actionsSet, int inputSize) {
        
        this.actionsSet = actionsSet;
        this.observationWrapper = new WrapperObservationVectorPositionsValues(false);
        this.actionWrapper = new WrapperAction2DMoveVector();
        this.gamma = 0.95;
        this.totalRewards = 0.0;
        
        this.inputSize = inputSize;
        this.hiddenSize = 64;
        }
    
    @Override
    public void init(MLKAgent agent) {
    	this.agent = agent;
    	
    	this.critic = new CriticNetwork(inputSize, hiddenSize, pnrg(), 0.001, observationWrapper);
        this.actor = new ActorNetwork(inputSize, hiddenSize, pnrg(), 0.001, observationWrapper, actionWrapper, actionsSet);
    }
    
    

    @Override
    public int getLearningFrequency() {
        return 1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates action selection to the actor network.
     * </p>
     */
    @Override
    public Action takeAction(Observation observation) {
        return actor.selectAction(observation);
    }

    /**
     * Updates the critic network and computes the TD-error.
     * <p>
     * Calculates the TD target as: reward + gamma * V(nextState) for non-terminal states,
     * or just reward for terminal states.
     * </p>
     *
     * @param state     the current state observation
     * @param reward    the reward received after taking the action
     * @param nextState the resulting state observation, or null if terminal
     * @return the TD-error (difference between TD target and current value estimate)
     */
    protected double updateCritic(Observation state, double reward, Observation nextState) {
        double tdTarget;
        if (nextState != null) {
            double vNext = critic.getValue(nextState);
            tdTarget = reward + gamma * vNext;
        } else {
            tdTarget = reward; 
        }
        
        return critic.update(state, tdTarget);
    }

    /**
     * Updates the actor network using the TD-error as an advantage estimate.
     *
     * @param state   the current state observation
     * @param action  the action that was taken
     * @param tdError the TD-error from the critic, used as advantage
     */
    protected void updateActor(Observation state, Action action, double tdError) {
        actor.update(state, action, tdError);
    }

    /**
     * Performs a complete Actor-Critic update for a single transition.
     * <p>
     * First updates the critic to get the TD-error, then uses that error
     * to update the actor network.
     * </p>
     *
     * @param state     the current state observation
     * @param action    the action taken
     * @param reward    the reward received
     * @param nextState the resulting state observation
     */
    protected void update(Observation state, Action action, double reward, Observation nextState) {
        double tdError = updateCritic(state, reward, nextState);
        updateActor(state, action, tdError);
    }

    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        while (batch.getExperiences().size() > 1) {
            totalRewards += learnOneStep(batch);
        }
    }

    /**
     * Learns from a single transition in the batch.
     * <p>
     * Extracts the current and next experience, performs an update,
     * and removes the processed experience from the batch.
     * </p>
     *
     * @param batch the batch containing at least two experiences
     * @return the reward from the current experience
     */
    protected double learnOneStep(Batch batch) {
        Experience currentExperience = batch.getExperiences().get(0);
        Experience nextExperience = batch.getExperiences().get(1);
        
        Observation currentState = currentExperience.getObservation();
        Action currentAction = currentExperience.getAction();
        double reward = currentExperience.getRewardValue();
        Observation nextState = nextExperience.getObservation();
        
        update(currentState, currentAction, reward, nextState);
        batch.getExperiences().remove(0);
        return reward;
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        learnOnBatch(batch, logger);
        
        if (batch.getExperiences().size() == 1) {
            Experience lastExperience = batch.getExperiences().get(0);
            Observation lastState = lastExperience.getObservation();
            Action lastAction = lastExperience.getAction();
            double lastReward = lastExperience.getRewardValue();
            
            update(lastState, lastAction, lastReward, null);
            batch.getExperiences().remove(0);
            totalRewards += lastReward;
        }
        
        logger.info("total rewards : " + totalRewards);
        totalRewards = 0.0;
    }

    /**
     * Returns the critic network used for value estimation.
     *
     * @return the critic neural network
     */
    public CriticNetwork getCritic() {
        return critic;
    }

    /**
     * Returns the actor network used for action selection.
     *
     * @return the actor neural network
     */
    public ActorNetwork getActor() {
        return actor;
    }
    
   
    @Override
    public MLKAgent getAgent() {
    	return agent;
    }

}
