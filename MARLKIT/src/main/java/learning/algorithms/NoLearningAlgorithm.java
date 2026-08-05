package learning.algorithms;

import agent.MLKAgent;
import learning.Algorithm;
import learning.Batch;
import learning.Policy;
import madkit.kernel.AgentLogger;

/**
 * Algorithm implementation for non-learning agents.
 *
 * This is useful for scripted or heuristic agents.
 */
public class NoLearningAlgorithm implements Algorithm {

    private MLKAgent agent;
    private Policy policy;

    public NoLearningAlgorithm(Policy policy) {
        this.policy = policy;
    }

    @Override
    public void setPolicy(Policy policy) {
        this.policy = policy;
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
        return 0;
    }

    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        // No learning.
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        batch.clear();
    }
}