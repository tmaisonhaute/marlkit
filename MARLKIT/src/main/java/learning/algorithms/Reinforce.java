package learning.algorithms;

import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import learning.Algorithm;
import learning.Batch;
import learning.Policy;
import learning.policies.PolicyGradientPolicy;
import madkit.kernel.AgentLogger;

public class Reinforce implements Algorithm {

    private PolicyGradientPolicy policy;
    private MLKAgent agent;

    private final double alpha;
    private final double gamma;

    public Reinforce(PolicyGradientPolicy policy, double alpha, double gamma) {
        this.policy = Objects.requireNonNull(policy);
        this.alpha = alpha;
        this.gamma = gamma;
        validateParameters();
    }


    @Override
    public void setPolicy(Policy policy) {
        if (policy instanceof PolicyGradientPolicy pgPolicy) {
            this.policy = pgPolicy;
        } else {
            throw new IllegalArgumentException("Reinforce requires a PolicyGradientPolicy.");
        }
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
        if (batch.size() == 0) {
            return;
        }

        double[][] logitsBatch = policy.forwardLogits(batch.getAllInputs());
        double[][] probsBatch = policy.softmax(logitsBatch);
        int[] actionIndices = toActionIndices(batch.getAllActions());
        double[] returns = batch.computeCumulativeRewards(gamma);
        double[][] gradients = computePolicyGradientSignal(probsBatch, actionIndices, returns);

        policy.updateFromLogitsGradient(batch.getAllInputs(), gradients, alpha);

        logger.info("Total rewards: " + batch.totalRewards());
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        learnOnBatch(batch, logger);
        batch.clear();
    }

    private void validateParameters() {
        if (alpha <= 0.0) {
            throw new IllegalArgumentException("alpha must be > 0.");
        }
        if (gamma < 0.0 || gamma > 1.0) {
            throw new IllegalArgumentException("gamma must be in [0, 1].");
        }
    }

    private int[] toActionIndices(Action[] actions) {
        int[] indices = new int[actions.length];

        for (int i = 0; i < actions.length; i++) {
            indices[i] = policy.actionIndex(actions[i]);
        }

        return indices;
    }

    private double[][] computePolicyGradientSignal(double[][] probs, int[] actionIndices, double[] returns) {
        double[][] gradients = new double[probs.length][probs[0].length];

        for (int i = 0; i < probs.length; i++) {
            fillSampleGradient(gradients[i], probs[i], actionIndices[i], returns[i]);
        }

        return gradients;
    }

    private void fillSampleGradient(double[] gradient, double[] probs, int actionIndex, double gain) {
        for (int j = 0; j < probs.length; j++) {
            gradient[j] = probs[j] * gain;
        }

        gradient[actionIndex] -= gain;
    }
}