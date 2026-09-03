package learning.algorithms;

import java.util.List;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import experience.Experience;
import learning.Algorithm;
import learning.Batch;
import learning.Policy;
import learning.policies.QValueBasedPolicy;
import madkit.kernel.AgentLogger;
import util.Pair;

/**
 * Q-Learning algorithm implementation.
 * <p>
 * Q-Learning is an off-policy temporal-difference learning algorithm.
 * It updates Q-values using the formula:
 * Q(s, a) = Q(s, a) + α * (r + γ * max_a' Q(s', a') - Q(s, a))
 * </p>
 *
 * @see Algorithm
 * @see QValueBasedPolicy
 */
public class QLearning implements Algorithm {

    private final double gamma;
    private final double alpha;
    private MLKAgent agent;
    private QValueBasedPolicy policy;
    private final List<Action> actionsSet;

    /**
     * Creates a Q-Learning algorithm with specified parameters.
     *
     * @param policy    the Q-value based policy to use and update
     * @param actionsSet the list of possible actions
     * @param alpha     the learning rate
     * @param gamma     the discount factor
     */
    public QLearning(QValueBasedPolicy policy, List<Action> actionsSet, double alpha, double gamma) {
        this.policy = policy;
        this.actionsSet = actionsSet;
        this.gamma = gamma;
        this.alpha = alpha;
    }

    /**
     * Creates a Q-Learning algorithm with default learning rate (0.1) and discount factor (0.95).
     *
     * @param policy    the Q-value based policy to use and update
     * @param actionsSet the list of possible actions
     */
    public QLearning(QValueBasedPolicy policy, List<Action> actionsSet) {
        this(policy, actionsSet, 0.1, 0.95);
    }


    @Override
    public void setPolicy(Policy policy) {
        if (policy instanceof QValueBasedPolicy qPolicy) {
            this.policy = qPolicy;
        } else {
            throw new IllegalArgumentException("QLearning requires a QValueBasedPolicy");
        }
    }

    @Override
    public QValueBasedPolicy getPolicy() {
        return this.policy;
    }

    @Override
	public void setAgent(MLKAgent agent) {
		this.agent = agent;
	}

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
     * the Q-learning update rule to each transition (s, a, r, s').
     * </p>
     */
    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        while (batch.getExperiences().size() > 1) {
            handleBatch(batch);
        }
    }

    /**
     * Processes a single transition from the batch.
     * <p>
     * Extracts the current and next experience, computes the Q-learning update,
     * and removes the processed experience from the batch.
     * </p>
     *
     * @param batch the batch containing experiences to process
     */
    private void handleBatch(Batch batch) {
        Experience current = batch.getExperiences().get(0);
        Experience next = (batch.getExperiences().size() > 1) ? batch.getExperiences().get(1) : null;
        

        Pair<Observation, Action> stateAction = new Pair<>(current.getObservation(), current.getAction());
        Observation nextInput = (next == null) ? null : next.getObservation();
        double reward = current.getRewardValue();

        updateQ(stateAction, reward, nextInput);

        batch.getExperiences().removeFirst();
    }

    /**
     * Computes the maximum Q-value over all actions for a given Observation.
     * <p>
     * This implements the "max" operator in the Q-learning update formula:
     * max_a' Q(s', a')
     * </p>
     *
     * @param nextObservation the next Observation (state s')
     * @return the maximum Q-value, or 0.0 if no actions are available
     */
    private double getMaxNextQ(Observation nextObservation) {
        double maxNextQ = Double.NEGATIVE_INFINITY;
        for (Action act : actionsSet) {
            double qVal = policy.getTable().getValue(nextObservation, act);
            if (qVal > maxNextQ) {
                maxNextQ = qVal;
            }
        }
        if (maxNextQ == Double.NEGATIVE_INFINITY) {
            maxNextQ = 0.0;
        }
        return maxNextQ;
    }

    /**
     * Updates the Q-value for a state-action pair using the Q-learning rule.
     * <p>
     * Applies: Q(s, a) ← Q(s, a) + α * (r + γ * max_a' Q(s', a') - Q(s, a))
     * </p>
     *
     * @param stateAction the state-action pair (s, a)
     * @param reward      the received reward r
     * @param nextObservation   the next observation (state s')
     */
    private void updateQ(Pair<Observation, Action> stateAction, double reward, Observation nextObservation) {
        double prevQ = policy.getTable().getValue(stateAction);
        double maxNextQ = (nextObservation != null) ? getMaxNextQ(nextObservation) : 0;
        double updatedQ = prevQ + alpha * (reward + gamma * maxNextQ - prevQ);
        policy.getTable().setValue(stateAction, updatedQ);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the exploration rate (epsilon) if using epsilon-greedy exploration,
     * logs the current Q-table size and epsilon value, then clears the batch.
     * </p>
     */
    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
    	while (!batch.getExperiences().isEmpty()) {
            handleBatch(batch);
        }
        policy.getExplorationStrategy().update();
        logger.info(policy.getExplorationStrategy().getLoggerInfo());
        logger.info("size Q: " + policy.getTable().size());
        batch.clear();
    }
}
