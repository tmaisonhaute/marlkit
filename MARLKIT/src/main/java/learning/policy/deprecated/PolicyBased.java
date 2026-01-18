package learning.policy.deprecated;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import madkit.kernel.AgentLogger;

/**
 * Abstract base class for policy-based reinforcement learning methods.
 * <p>
 * Policy-based methods directly parameterize and optimize the policy,
 * as opposed to value-based methods that derive policies from value functions.
 * </p>
 * <p>
 * This class provides a skeleton implementation with stub methods to be
 * overridden by concrete policy-based algorithms.
 * </p>
 *
 * @see Policy
 */
public abstract class PolicyBased implements Policy {

	@Override
	public void init(MLKAgent agent) {
		// TODO Auto-generated method stub

	}

	@Override
	public MLKAgent getAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLearningFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Action takeAction(Observation observation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
		// TODO Auto-generated method stub

	}

}
