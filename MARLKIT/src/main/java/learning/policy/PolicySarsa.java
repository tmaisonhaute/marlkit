
package learning.policy;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.Experience;
import learning.policy.valuebased.PolicyQValueBased;
import madkit.kernel.AgentLogger;
import util.Pair;

/**
 * SARSA (State-Action-Reward-State-Action) reinforcement learning policy.
 * <p>
 * This on-policy temporal difference method updates Q-values based on the action
 * actually taken in the next state, unlike Q-learning which uses the max Q-value.
 * This makes SARSA more conservative as it accounts for exploration.
 * </p>
 * <p>
 * Update rule: Q(s,a) = Q(s,a) + alpha * (r + gamma * Q(s',a') - Q(s,a))
 * </p>
 *
 * @see PolicyQValueBased
 * @see PolicyQLearning
 */
public class PolicySarsa extends PolicyQValueBased {

	private MLKAgent agent;
    private double gamma;
    private double alpha;
    private double totalRewards;

    /**
     * Creates a SARSA policy with epsilon-greedy exploration and decay.
     *
     * @param actionsSet      the list of possible actions
     * @param epsilon         the initial exploration rate
     * @param epsilonDecrease the rate at which epsilon decreases per episode
     */
    public PolicySarsa(List<Action> actionsSet, double epsilon, double epsilonDecrease) {
        super(actionsSet, epsilon, epsilonDecrease);
        this.gamma = 0.95;
        this.alpha = 0.1;
        this.totalRewards = 0.0;
    }

    /**
     * Creates a SARSA policy with epsilon-greedy exploration (no decay).
     *
     * @param actionsSet the list of possible actions
     * @param epsilon    the exploration rate
     */
    public PolicySarsa(List<Action> actionsSet, double epsilon) {
        this(actionsSet, epsilon, 0.0);
    }

    /**
     * Creates a SARSA policy with default epsilon of 0.05.
     *
     * @param actionsSet the list of possible actions
     */
    public PolicySarsa(List<Action> actionsSet) {
        this(actionsSet, 0.05);
    }
    

	@Override
	public void init(MLKAgent agent) {
		super.init(agent);
		this.agent = agent;
	}

    /**
     * {@inheritDoc}
     * Returns 1 since SARSA learns after each step.
     */
    @Override
	public int getLearningFrequency() {
        return 1;
    }

    /**
     * Updates the Q-value for a state-action pair using the SARSA update rule.
     * <p>
     * Q(s,a) = Q(s,a) + alpha * (reward + gamma * Q(s',a') - Q(s,a))
     * </p>
     *
     * @param state      the current state observation
     * @param action     the action taken in the current state
     * @param reward     the reward received
     * @param nextState  the resulting state observation, or null if terminal
     * @param nextAction the action taken in the next state, or null if terminal
     */
    protected void updateQ(Observation state, Action action, double reward, Observation nextState, Action nextAction) {
    	Map<Pair<Observation, Action>, Double> q = getQ();
        double qNext = 0;
        if (nextState != null && nextAction != null) {
            Pair<Observation, Action> nextStateAction = new Pair<>(nextState, nextAction);
            qNext = q.getOrDefault(nextStateAction, 0.0);
        }

        Pair<Observation, Action> stateAction = new Pair<>(state, action);
        double qValue = q.getOrDefault(stateAction, 0.0);
        q.put(stateAction, qValue + alpha * (reward + gamma * qNext - qValue));
    }

    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
    	while (batch.getExperiences().size() > 1) {
            totalRewards += learnOneStep(batch);
        }
    }

    /**
     * Learns from a single transition using the SARSA update.
     * <p>
     * Extracts current and next state-action pairs, performs the update,
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
		Action nextAction = nextExperience.getAction();

		updateQ(currentState, currentAction, reward, nextState, nextAction);
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

            updateQ(lastState, lastAction, lastReward, null, null);
            batch.getExperiences().remove(0);
            totalRewards += lastReward;
        }
        updateEpsilon();
        logger.info("total rewards : " + totalRewards);
	    logger.info("size Q: " + getQ().size());
	    logger.info("Epsilon : " + getEpsilon());
	    totalRewards = 0.0;
	}


	@Override
	public MLKAgent getAgent() {
		return agent;
	}
    
}
