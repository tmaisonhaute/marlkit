package learning.policy;

import java.util.List;
import java.util.Random;

import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import madkit.kernel.AgentLogger;

public class PolicyRandom implements Policy {
	private List<Action> actionsSet;
	
	public PolicyRandom(List<Action> actionsSet) {
		this.actionsSet = actionsSet;
	}
	
	@Override
	public Action takeAction(Observation observation) {
		Random random = new Random();
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

}
