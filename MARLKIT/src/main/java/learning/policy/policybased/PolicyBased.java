package learning.policy.policybased;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.policy.Policy;
import madkit.kernel.AgentLogger;

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
