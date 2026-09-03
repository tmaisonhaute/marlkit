package learning.actionexplorationstrategies;

import java.util.random.RandomGenerator;

import agent.action.ActionContinuousVector;
import learning.ContinuousActionExplorationStrategy;

/**
 * Gaussian noise exploration strategy for continuous action spaces.
 * 
 * <p>
 * It adds Gaussian noise to the continuous action produced by the policy.
 * </p>
 */
public class GaussianNoise implements ContinuousActionExplorationStrategy {

    private double standardDeviation;

    public GaussianNoise(double standardDeviation) {
        if (standardDeviation < 0.0) {
            throw new IllegalArgumentException("standardDeviation must be non-negative.");
        }
        this.standardDeviation = standardDeviation;
    }

    @Override
    public ActionContinuousVector explore(ActionContinuousVector action, RandomGenerator prng) {
        ActionContinuousVector exploratoryAction = action.copy();

        for (int i = 0; i < exploratoryAction.getSize(); i++) {
            double noise = prng.nextGaussian() * standardDeviation;
            exploratoryAction.setValue(i, exploratoryAction.getValue(i) + noise);
        }

        return exploratoryAction;
    }

    @Override
    public void update() {
    }

    @Override
    public String getLoggerInfo() {
        return "GaussianNoise-standardDeviation=" + standardDeviation;
    }
}