package marlkit.gooryield.algorithm;

import agent.MLKAgent;
import learning.Algorithm;
import learning.Batch;
import learning.Policy;
import madkit.kernel.AgentLogger;
import marlkit.gooryield.policy.GoOrYieldSwitchingPolicy;

public class RandomPolicySwitchAlgorithm implements Algorithm {

    protected MLKAgent agent;
    protected GoOrYieldSwitchingPolicy policy;
    protected final int learningFrequency;

    public RandomPolicySwitchAlgorithm(GoOrYieldSwitchingPolicy policy, int learningFrequency) {
        if (policy == null) {
            throw new IllegalArgumentException("policy must not be null.");
        }
        if (learningFrequency <= 0) {
            throw new IllegalArgumentException("learningFrequency must be positive.");
        }
        this.policy = policy;
        this.learningFrequency = learningFrequency;
    }

    @Override
    public void setPolicy(Policy policy) {
        if (!(policy instanceof GoOrYieldSwitchingPolicy)) {
            throw new IllegalArgumentException("policy should be a GoOrYieldSwitchingPolicy.");
        }
        this.policy = (GoOrYieldSwitchingPolicy) policy;
    }

    @Override
    public Policy getPolicy() {
        return policy;
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    @Override
    public void setAgent(MLKAgent agent) {
        this.agent = agent;
    }

    @Override
    public int getLearningFrequency() {
        return learningFrequency;
    }

    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        policy.selectRandomPolicy();
        if (logger != null) {
            logger.info("Switched scripted policy to " + policy.getCurrentPolicy().getClass().getSimpleName());
        }
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
    }
}