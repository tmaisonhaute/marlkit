package learning.algorithm;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.Experience;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedy;
import madkit.kernel.AgentLogger;
import util.Pair;

/**
 * SARSA (State-Action-Reward-State-Action) algorithm implementation.
 * <p>
 * SARSA is an on-policy temporal-difference learning algorithm.
 * Unlike Q-Learning which uses the max Q-value for the next state,
 * SARSA uses the Q-value of the action actually taken, making it
 * more conservative as it accounts for exploration.
 * </p>
 * <p>
 * Update rule: Q(s, a) = Q(s, a) + α * (r + γ * Q(s', a') - Q(s, a))
 * </p>
 *
 * @see Algorithm
 * @see QValueBasedPolicy
 * @see QLearning
 */
public class Sarsa implements Algorithm {

    private final double gamma;
    private final double alpha;
    private MLKAgent agent;
    private final QValueBasedPolicy policy;
    private double totalRewards;

    /**
     * Creates a SARSA algorithm with specified parameters.
     *
     * @param policy     the Q-value based policy to use and update
     * @param alpha      the learning rate
     * @param gamma      the discount factor
     */
    public Sarsa(QValueBasedPolicy policy, double alpha, double gamma) {
        this.policy = policy;
        this.gamma = gamma;
        this.alpha = alpha;
        this.totalRewards = 0.0;
    }

    /**
     * Creates a SARSA algorithm with default learning rate (0.1) and discount factor (0.95).
     *
     * @param policy     the Q-value based policy to use and update
     */
    public Sarsa(QValueBasedPolicy policy) {
        this(policy, 0.1, 0.95);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(MLKAgent agent) {
        this.agent = agent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QValueBasedPolicy getPolicy() {
        return this.policy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MLKAgent getAgent() {
        return this.agent;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns 1, meaning the algorithm learns after every timestep.
     * </p>
     */
    @Override
    public int getLearningFrequency() {
        return 1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Processes all consecutive experience pairs in the batch, applying
     * the SARSA update rule to each transition (s, a, r, s', a').
     * </p>
     */
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
    private double learnOneStep(Batch batch) {
        Experience currentExperience = batch.getExperiences().get(0);
        Experience nextExperience = batch.getExperiences().get(1);

        Observation currentState = currentExperience.getObservation();
        Action currentAction = currentExperience.getAction();
        double reward = currentExperience.getRewardValue();
        Observation nextState = nextExperience.getObservation();
        Action nextAction = nextExperience.getAction();

        updateQ(currentState, currentAction, reward, nextState, nextAction);
        batch.getExperiences().removeFirst();
        return reward;
    }

    /**
     * Updates the Q-value for a state-action pair using the SARSA rule.
     * <p>
     * Applies: Q(s, a) ← Q(s, a) + α * (r + γ * Q(s', a') - Q(s, a))
     * </p>
     * <p>
     * Unlike Q-Learning, SARSA uses the actual next action a' taken by the policy,
     * not the action with maximum Q-value.
     * </p>
     *
     * @param state      the current state observation
     * @param action     the action taken in the current state
     * @param reward     the received reward
     * @param nextState  the next state observation, or null if terminal
     * @param nextAction the action taken in the next state, or null if terminal
     */
    private void updateQ(Observation state, Action action, double reward, Observation nextState, Action nextAction) {
        double qNext = 0.0;
        if (nextState != null && nextAction != null) {
            qNext = policy.getTable().getValue(nextState, nextAction);
        }

        Pair<Observation, Action> stateAction = new Pair<>(state, action);
        double qValue = policy.getTable().getValue(stateAction);
        double updatedQ = qValue + alpha * (reward + gamma * qNext - qValue);
        policy.getTable().setValue(stateAction, updatedQ);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Processes remaining experiences, including the terminal state where the
     * next Q-value is 0. Updates the exploration rate (epsilon) if using
     * epsilon-greedy exploration, logs statistics, then clears the batch.
     * </p>
     */
    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        learnOnBatch(batch, logger);
        
        // Handle the last experience (terminal state)
        if (batch.getExperiences().size() == 1) {
            Experience lastExperience = batch.getExperiences().get(0);
            Observation lastState = lastExperience.getObservation();
            Action lastAction = lastExperience.getAction();
            double lastReward = lastExperience.getRewardValue();

            updateQ(lastState, lastAction, lastReward, null, null);
            batch.getExperiences().removeFirst();
            totalRewards += lastReward;
        }

        // Update epsilon if using epsilon-greedy exploration
        if (policy.getExplorationStrategy() instanceof EpsilonGreedy epsilonGreedy) {
            epsilonGreedy.updateEpsilon();
            logger.info("Epsilon : " + epsilonGreedy.getEpsilon());
        }
        
        logger.info("total rewards : " + totalRewards);
        logger.info("size Q: " + policy.getTable().size());
        
        totalRewards = 0.0;
        batch.clear();
    }
}
