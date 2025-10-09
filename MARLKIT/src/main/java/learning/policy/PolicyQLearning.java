package learning.policy;

import java.util.List;

import agent.AgentStandard;
import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.Experience;
import learning.policy.valuebased.PolicyQValueBased;
import madkit.kernel.AgentLogger;
import util.Pair;

/**
 * Q-Learning policy implementation.
 * This policy uses Q-learning to select actions based on the learned Q-values.
 * It maintains a Q-table mapping state-action pairs to their expected rewards.
 */
public class PolicyQLearning extends PolicyQValueBased {

    private final double gamma;
    private final double alpha;
    private AgentStandard agent;
    private final double defaultQValue;

    public PolicyQLearning(List<Action> actionsSet, double epsilon, double epsilonDecrease, double defaultQValue, double alpha, double gamma)  {
        super(actionsSet, epsilon, epsilonDecrease);
        this.defaultQValue = defaultQValue;
        this.gamma = gamma;
        this.alpha = alpha;
    }
    public PolicyQLearning(List<Action> actionsSet, double epsilon, double epsilonDecrease, double defaultQValue) {
        this(actionsSet, epsilon, epsilonDecrease, defaultQValue, 0.1, 0.95);
    }

    public PolicyQLearning(List<Action> actionsSet, double epsilon, double epsilonDecrease) {
        this(actionsSet, epsilon, epsilonDecrease, 0, 0.1, 0.95);
    }

    public PolicyQLearning(List<Action> actionsSet, double epsilon) {
        this(actionsSet, epsilon, 0.0);
    }

    public PolicyQLearning(List<Action> actionsSet) {
        this(actionsSet, 0.05);
    }

    @Override
    public void init(MLKAgent agent) {
        super.init(agent);
        this.agent = (AgentStandard) agent;
    }

    @Override
    public MLKAgent getAgent() {
        return this.agent;
    }

    @Override
    public int getLearningFrequency() {
        return 1;
    }


    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        while (batch.getExperiences().size() > 1) {
            handleBatch(batch);
        }
    }

    private void handleBatch(Batch batch) {
        Experience current = batch.getExperiences().get(0);
        Experience next = batch.getExperiences().get(1);

        Pair<Observation, Action> stateAction = new Pair<>(current.getObservation(), current.getAction());
        Observation nextObs = next.getObservation();
        double reward = current.getRewardValue();

        updateQ(stateAction, reward, nextObs);

        batch.getExperiences().removeFirst();
    }

    private double getMaxNextQ(Observation nextObs) {
        double maxNextQ = Double.NEGATIVE_INFINITY;
        for (Action act : getActionsSet()) {
            double qVal = getQ().getOrDefault(new Pair<>(nextObs, act), defaultQValue);
            if (qVal > maxNextQ) {
                maxNextQ = qVal;
            }
        }
        if (maxNextQ == Double.NEGATIVE_INFINITY) {
            maxNextQ = 0.0;
        }
        return maxNextQ;
    }

    private void updateQ(Pair<Observation, Action> stateAction, double reward, Observation nextObs) {
        double prevQ = getQ().getOrDefault(stateAction, defaultQValue);
        double maxNextQ = getMaxNextQ(nextObs);
        double updatedQ = prevQ + alpha * (reward + gamma * maxNextQ - prevQ);
        getQ().put(stateAction, updatedQ);
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        updateEpsilon();
        logger.info("size Q: " + getQ().size());
        logger.info("Epsilon : " + getEpsilon());
        batch.clear();
    }
}
