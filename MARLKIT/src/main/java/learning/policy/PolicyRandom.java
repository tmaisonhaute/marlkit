package learning.policy;

import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import madkit.kernel.AgentLogger;

/**
 * A policy that selects actions uniformly at random from a predefined action set.
 * Does not perform any learning.
 */
public class PolicyRandom implements Policy {
	private MLKAgent agent;
	private List<Action> actionsSet;
	
	/**
	 * Creates a random policy with the specified action set.
	 *
	 * @param actionsSet the set of possible actions
	 */
	public PolicyRandom(List<Action> actionsSet) {
		this.actionsSet = actionsSet;
	}
	

	@Override
	public void init(MLKAgent agent) {
		this.agent = agent;
		
	}
	
	/**
	 * Selects a random action from the action set.
	 *
	 * @param observation the observation (ignored by random policy)
	 * @return a randomly selected action
	 */
	@Override
	public Action takeAction(Observation observation) {
		RandomGenerator random = pnrg();
		return actionsSet.get(random.nextInt(actionsSet.size()));
	}


	@Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
		//No learning
	}

	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
		// No learning
	}

	@Override
	public int getLearningFrequency() {
		return 0;
	}


	@Override
	public MLKAgent getAgent() {
		return agent;
	}

}
