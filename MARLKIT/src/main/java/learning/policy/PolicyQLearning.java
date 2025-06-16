package learning.policy;

import agent.AgentStandard;
import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.Experience;
import madkit.kernel.AgentLogger;
import util.Pair;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.random.RandomGenerator;

/**
 * Q-Learning policy implementation.
 * This policy uses Q-learning to select actions based on the learned Q-values.
 * It maintains a Q-table mapping state-action pairs to their expected rewards.
 */
public class PolicyQLearning extends PolicyEpsilon {

    private Map<Pair<Observation, Action>, Double> q;
    private final List<Action> actionsSet;
    private final double gamma;
    private final double alpha;
    private AgentStandard agent;
    private final double defaultQValue;

    public PolicyQLearning(List<Action> actionsSet, double epsilon, double epsilonDecrease, double defaultQValue, double alpha, double gamma)  {
        super(epsilon, epsilonDecrease);
        this.actionsSet = actionsSet;
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
        this.q = new HashMap<>();
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
    public Action takeAction(Observation observation) {
        RandomGenerator random = pnrg();
        if (random.nextDouble() < getEpsilon()) {
            return actionsSet.get(random.nextInt(actionsSet.size()));
        }
        Action bestAction = null;
        double maxScore = Double.NEGATIVE_INFINITY;
        for (Action act : actionsSet) {
            Pair<Observation, Action> stateActionPair = new Pair<>(observation, act);
            if (!q.containsKey(stateActionPair)) {
                bestAction = act;
                break;
            }

            if (q.get(stateActionPair) > maxScore) {
                bestAction = act;
                maxScore = q.get(stateActionPair);
            }
        }
        return bestAction;
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
        for (Action act : actionsSet) {
            double qVal = q.getOrDefault(new Pair<>(nextObs, act), defaultQValue);
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
        double prevQ = q.getOrDefault(stateAction, defaultQValue);
        double maxNextQ = getMaxNextQ(nextObs);
        double updatedQ = prevQ + alpha * (reward + gamma * maxNextQ - prevQ);
        q.put(stateAction, updatedQ);
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        updateEpsilon();
        logger.info("size Q: " + q.size());
        logger.info("Epsilon : " + getEpsilon());
        batch.clear();
    }
}
