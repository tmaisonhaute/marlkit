package modelofotheragents;

import agent.action.Action;
import agent.modelofotheragent.ModelPredictAction;
import environment.observation.Observation;
import learning.Policy;

public class AccessOtherPolicy implements ModelPredictAction {
	protected Policy policy;
	protected Action lastPredictedAction;
	
	public AccessOtherPolicy(Policy policy) {
		this.policy = policy;
	}

	@Override
	public Action predictAction(Observation observation) {
		Action predictedAction = policy.selectAction(observation);
		lastPredictedAction = predictedAction.copy(); 
		return predictedAction;
	}

	@Override
	public Action predictAction(Observation observation, Action action) {
		return predictAction(observation);
	}

	@Override
	public void updateModel(Observation observation, Action predictedAction, Action actualAction) {
		// No update needed for accessing policies directly
	}

	@Override
	public Action getLastPredictedAction() {
		return lastPredictedAction;
	}

}
