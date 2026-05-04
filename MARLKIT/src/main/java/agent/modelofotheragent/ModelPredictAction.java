package agent.modelofotheragent;

import agent.action.Action;
import learning.policy.PolicyInput;

public interface ModelPredictAction {
	public Action predictAction(PolicyInput observation);
	public Action predictAction(PolicyInput observation, Action action);
	
	public void updateModel(PolicyInput observation, Action predictedAction, Action actualAction);
}
