package learning.policy;

import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import madkit.kernel.AgentLogger;

public class PolicyRandom implements Policy {
	private MLKAgent agent;
	private List<Action> actionsSet;
	
	public PolicyRandom(List<Action> actionsSet) {
		this.actionsSet = actionsSet;
	}
	

	@Override
	public void init(MLKAgent agent) {
		this.agent = agent;
		
	}
	
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
