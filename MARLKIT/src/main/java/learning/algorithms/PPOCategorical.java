package learning.algorithms;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Algorithm;
import learning.Batch;
import learning.Policy;
import learning.policies.CategoricalPolicyGradient;
import madkit.kernel.AgentLogger;

/**
 * Simplified PPO implementation for categorical policies.
 *
 * This implementation assumes:
 * - discrete action space,
 * - policy outputs logits,
 * - actions are sampled from a softmax distribution,
 * - advantages are computed from normalized discounted returns,
 * - no critic/value network yet.
 */
public class PPOCategorical implements Algorithm {

    private CategoricalPolicyGradient policy;
    private MLKAgent agent;

    private final double learningRate;
    private final double gamma;
    private final double clipEpsilon;
    private final int epochs;

    public PPOCategorical(
            CategoricalPolicyGradient policy,
            double learningRate,
            double gamma,
            double clipEpsilon,
            int epochs
    ) {
        this.policy = policy;
        this.learningRate = learningRate;
        this.gamma = gamma;
        this.clipEpsilon = clipEpsilon;
        this.epochs = epochs;

        validateParameters();
    }

    public PPOCategorical(CategoricalPolicyGradient policy) {
        this(policy, 0.001, 0.95, 0.2, 4);
    }


    @Override
    public void setPolicy(Policy policy) {
        if (!(policy instanceof CategoricalPolicyGradient pgPolicy)) {
            throw new IllegalArgumentException("PPOCategorical requires a PolicyGradientPolicy.");
        }

        this.policy = pgPolicy;
    }

    @Override
    public CategoricalPolicyGradient getPolicy() {
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

    /**
     * PPO is trained at the end of the episode in this implementation.
     */
    @Override
    public int getLearningFrequency() {
        return 0;
    }

    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        if (batch.size() == 0) {
            return;
        }

        Observation[] observations = batch.getAllObservations();
        Action[] actions = batch.getAllActions();

        int[] actionIndices = toActionIndices(actions);

        double[][] oldLogits = policy.forwardLogits(observations);
        double[][] oldProbs = policy.softmax(oldLogits);
        double[] oldActionProbs = extractActionProbabilities(oldProbs, actionIndices);

        double[] returns = batch.computeCumulativeRewards(gamma);
        double[] advantages = normalize(returns);

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[][] currentLogits = policy.forwardLogits(observations);
            double[][] currentProbs = policy.softmax(currentLogits);

            double[][] gradients = computeClippedPolicyGradients(
                    currentProbs,
                    oldActionProbs,
                    actionIndices,
                    advantages
            );

            policy.updateFromLogitsGradient(observations, gradients, learningRate);
        }

        logger.info("PPOCategorical total rewards: " + batch.totalRewards());
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        learnOnBatch(batch, logger);
        batch.clear();
    }

    private int[] toActionIndices(Action[] actions) {
        int[] indices = new int[actions.length];

        for (int i = 0; i < actions.length; i++) {
            indices[i] = policy.actionIndex(actions[i]);
        }

        return indices;
    }

    private double[] extractActionProbabilities(double[][] probs, int[] actionIndices) {
        double[] actionProbs = new double[probs.length];

        for (int i = 0; i < probs.length; i++) {
            actionProbs[i] = Math.max(probs[i][actionIndices[i]], 1e-12);
        }

        return actionProbs;
    }

    private double[][] computeClippedPolicyGradients(
            double[][] currentProbs,
            double[] oldActionProbs,
            int[] actionIndices,
            double[] advantages
    ) {
        double[][] gradients = new double[currentProbs.length][currentProbs[0].length];

        for (int i = 0; i < currentProbs.length; i++) {
            fillSampleGradient(
                    gradients[i],
                    currentProbs[i],
                    oldActionProbs[i],
                    actionIndices[i],
                    advantages[i]
            );
        }

        return gradients;
    }

    /**
     * Computes dLoss/dLogits for the PPO clipped surrogate.
     *
     * If the sample is clipped, gradient is zero.
     * Otherwise:
     * dLoss/dz = advantage * ratio * (probabilities - oneHot(action))
     */
    private void fillSampleGradient(
            double[] gradient,
            double[] currentProbs,
            double oldActionProb,
            int actionIndex,
            double advantage
    ) {
        double currentActionProb = Math.max(currentProbs[actionIndex], 1e-12);
        double ratio = currentActionProb / oldActionProb;

        if (isClipped(ratio, advantage)) {
            return;
        }

        double scale = advantage * ratio;

        for (int j = 0; j < currentProbs.length; j++) {
            gradient[j] = scale * currentProbs[j];
        }

        gradient[actionIndex] -= scale;
    }

    private boolean isClipped(double ratio, double advantage) {
        if (advantage > 0.0 && ratio > 1.0 + clipEpsilon) {
            return true;
        }

        if (advantage < 0.0 && ratio < 1.0 - clipEpsilon) {
            return true;
        }

        return false;
    }

    private double[] normalize(double[] values) {
        double mean = mean(values);
        double std = standardDeviation(values, mean);

        double[] normalized = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            normalized[i] = (values[i] - mean) / (std + 1e-8);
        }

        return normalized;
    }

    private double mean(double[] values) {
        double sum = 0.0;

        for (double value : values) {
            sum += value;
        }

        return sum / values.length;
    }

    private double standardDeviation(double[] values, double mean) {
        double sum = 0.0;

        for (double value : values) {
            double diff = value - mean;
            sum += diff * diff;
        }

        return Math.sqrt(sum / values.length);
    }

    private void validateParameters() {
        if (learningRate <= 0.0) {
            throw new IllegalArgumentException("learningRate must be > 0.");
        }

        if (gamma < 0.0 || gamma > 1.0) {
            throw new IllegalArgumentException("gamma must be in [0, 1].");
        }

        if (clipEpsilon <= 0.0) {
            throw new IllegalArgumentException("clipEpsilon must be > 0.");
        }

        if (epochs <= 0) {
            throw new IllegalArgumentException("epochs must be > 0.");
        }
    }
}
