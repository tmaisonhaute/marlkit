package learning.policy;

import java.util.List;

import agent.MLKAgent;
import agent.action.Action;
import learning.policy.valuefunction.QTable;
import util.Pair;

/**
 * Stochastic policy that samples actions using a softmax over Q-values.
 */
public class SoftmaxQPolicy implements Policy {
	private final QTable qTable;
	private final List<Action> actionsSet;
	private final double temperature;
	private MLKAgent agent;

	public SoftmaxQPolicy(List<Action> actionsSet, double temperature) {
		this(actionsSet, 0.0, temperature);
	}

	public SoftmaxQPolicy(List<Action> actionsSet, double defaultValue, double temperature) {
		if (temperature <= 0.0) {
			throw new IllegalArgumentException("temperature must be positive.");
		}
		this.actionsSet = actionsSet;
		this.qTable = new QTable(defaultValue);
		this.temperature = temperature;
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
		if (actionsSet.isEmpty()) {
			return null;
		}
		double[] qValues = collectQValues(input);
		double max = findMax(qValues);
		SoftmaxWeights weights = buildSoftmaxWeights(qValues, max);
		return drawAction(weights.weights, weights.sum);
	}

	/**
	 * Collect Q-values for each available action.
	 *
	 * @param input Policy input for the current state.
	 * @return Array of Q-values in action order.
	 */
	protected double[] collectQValues(PolicyInput input) {
		double[] values = new double[actionsSet.size()];
		for (int i = 0; i < actionsSet.size(); i++) {
			Action act = actionsSet.get(i);
			double qval = qTable.getValue(new Pair<>(input, act));
			if (Double.isNaN(qval)) {
			    throw new IllegalStateException("NaN Q-value detected");
			}
			values[i] = qval;
		}
		return values;
	}

	/**
	 * Find the maximum value in the Q-value array.
	 *
	 * @param values Q-values.
	 * @return Maximum value.
	 */
	protected double findMax(double[] values) {
		double max = Double.NEGATIVE_INFINITY;
		for (double value : values) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	/**
	 * Build softmax weights from Q-values.
	 *
	 * @param values Q-values.
	 * @param max Max value used for numerical stability.
	 * @return Softmax weights and their sum.
	 */
	protected SoftmaxWeights buildSoftmaxWeights(double[] values, double max) {
		double sum = 0.0;
		double[] weights = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			double scaled = (values[i] - max) / temperature;
			double weight = Math.exp(scaled);
			weights[i] = weight;
			sum += weight;
		}
		return new SoftmaxWeights(weights, sum);
	}

	/**
	 * Sample an action according to the provided weights.
	 *
	 * @param weights Softmax weights in action order.
	 * @param sum Sum of weights.
	 * @return Selected action.
	 */
	protected Action drawAction(double[] weights, double sum) {
		if (sum == 0.0 || Double.isNaN(sum)) {
		    return actionsSet.get(prng().nextInt(actionsSet.size()));
		}

		double draw = prng().nextDouble() * sum;
		double cumulative = 0.0;
		for (int i = 0; i < weights.length; i++) {
			cumulative += weights[i];
			if (draw <= cumulative) {
				return actionsSet.get(i);
			}
		}
		return actionsSet.get(weights.length - 1);
	}

	protected static class SoftmaxWeights {
		private final double[] weights;
		private final double sum;

		protected SoftmaxWeights(double[] weights, double sum) {
			this.weights = weights;
			this.sum = sum;
		}
	}

	public QTable getTable() {
		return qTable;
	}
}
