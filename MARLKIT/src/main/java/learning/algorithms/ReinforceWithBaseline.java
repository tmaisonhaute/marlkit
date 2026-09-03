package learning.algorithms;

import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import learning.Algorithm;
import learning.Batch;
import learning.Policy;
import learning.policies.CategoricalPolicyGradient;
import madkit.kernel.AgentLogger;

public class ReinforceWithBaseline implements Algorithm {

    private CategoricalPolicyGradient policy;
    private MLKAgent agent;

    private final double alpha;
    private final double gamma;
    private final double baselineUpdateRate;

    private double baseline;

    public ReinforceWithBaseline(CategoricalPolicyGradient policy, double alpha, double gamma) {
        this(policy, alpha, gamma, 0.05, 0.0);
    }

    public ReinforceWithBaseline(
            CategoricalPolicyGradient policy,
            double alpha,
            double gamma,
            double baselineUpdateRate,
            double initialBaseline
    ) {
        this.policy = Objects.requireNonNull(policy);
        this.alpha = alpha;
        this.gamma = gamma;
        this.baselineUpdateRate = baselineUpdateRate;
        this.baseline = initialBaseline;
        validateParameters();
    }


    @Override
    public void setPolicy(Policy policy) {
        if (policy instanceof CategoricalPolicyGradient pgPolicy) {
            this.policy = pgPolicy;
        } else {
            throw new IllegalArgumentException("ReinforceWithBaseline requires a PolicyGradientPolicy.");
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

        double[] returns = batch.computeCumulativeRewards(gamma);
        double[] advantages = computeAdvantages(returns);

        double[][] logitsBatch = policy.forwardLogits(batch.getAllObservations());
        double[][] probsBatch = policy.softmax(logitsBatch);
        int[] actionIndices = toActionIndices(batch.getAllActions());
        double[][] gradients = computePolicyGradientSignal(probsBatch, actionIndices, advantages);

        policy.updateFromLogitsGradient(batch.getAllObservations(), gradients, alpha);
        updateBaseline(returns);

        logger.info("Total rewards: " + batch.totalRewards());
        logger.info("Baseline: " + baseline);
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        learnOnBatch(batch, logger);
        batch.clear();
    }

    /**
     * Validates algorithm parameters.
     */
    private void validateParameters() {
        if (alpha <= 0.0) {
            throw new IllegalArgumentException("alpha must be > 0.");
        }
        if (gamma < 0.0 || gamma > 1.0) {
            throw new IllegalArgumentException("gamma must be in [0, 1].");
        }
        if (baselineUpdateRate < 0.0 || baselineUpdateRate > 1.0) {
            throw new IllegalArgumentException("baselineUpdateRate must be in [0, 1].");
        }
    }

    /**
     * Computes advantages by subtracting the current baseline from returns.
     */
    private double[] computeAdvantages(double[] returns) {
        double[] advantages = new double[returns.length];

        for (int i = 0; i < returns.length; i++) {
            advantages[i] = returns[i] - baseline;
        }

        return advantages;
    }

    /**
     * Updates the moving-average baseline.
     */
    private void updateBaseline(double[] returns) {
        double meanReturn = mean(returns);
        baseline += baselineUpdateRate * (meanReturn - baseline);
    }

    /**
     * Computes the mean of a vector.
     */
    private double mean(double[] values) {
        double sum = 0.0;

        for (double value : values) {
            sum += value;
        }

        return sum / values.length;
    }

    /**
     * Converts actions into policy action indices.
     */
    private int[] toActionIndices(Action[] actions) {
        int[] indices = new int[actions.length];

        for (int i = 0; i < actions.length; i++) {
            indices[i] = policy.actionIndex(actions[i]);
        }

        return indices;
    }

    /**
     * Computes dL/dlogits using advantages.
     */
    private double[][] computePolicyGradientSignal(double[][] probs, int[] actionIndices, double[] advantages) {
        double[][] gradients = new double[probs.length][probs[0].length];

        for (int i = 0; i < probs.length; i++) {
            fillSampleGradient(gradients[i], probs[i], actionIndices[i], advantages[i]);
        }

        return gradients;
    }

    /**
     * Fills the gradient for one sample.
     */
    private void fillSampleGradient(double[] gradient, double[] probs, int actionIndex, double advantage) {
        for (int j = 0; j < probs.length; j++) {
            gradient[j] = probs[j] * advantage;
        }

        gradient[actionIndex] -= advantage;
    }

    public double getBaseline() {
        return baseline;
    }
}