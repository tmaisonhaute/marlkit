package learning.policies;

import agent.MLKAgent;
import agent.action.Action;
import learning.Policy;
import util.MapProba;

public class StochasticFixedPolicy implements Policy {

    protected MLKAgent agent;
    protected final MapProba<Action> actionProbabilities;

    public StochasticFixedPolicy(MapProba<Action> actionProbabilities) {
        if (actionProbabilities == null || actionProbabilities.isEmpty()) {
            throw new IllegalArgumentException("actionProbabilities must not be null or empty.");
        }
        this.actionProbabilities = actionProbabilities;
        this.actionProbabilities.normalize();
    }

    @Override
    public void init(MLKAgent agent) {
        this.agent = agent;
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    @Override
    public Action selectAction(PolicyInput input) {
        Action action = actionProbabilities.randomlySelectKey();
        if (action == null) {
            throw new IllegalStateException("No action could be selected from the stochastic policy.");
        }
        return action.copy();
    }
}