package learning.policy;

import java.util.List;
import java.util.Random;

import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;

public class PolicyRandom implements Policy {
	private List<Action> actionsSet;
	
	@Override
	public Action takeAction(Observation obs) {
		Random random = new Random();
		return actionsSet.get(random.nextInt(actionsSet.size()));
	}


	@Override
	public void learn_on_batch(Batch batch) {
		//No learning
		return;
	}

}
