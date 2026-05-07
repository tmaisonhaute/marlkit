package agent.modelofotheragent;

import agent.action.Action;

public class PredictionModelsManager implements ModelsManager {
	protected GroupModelPredictAction groupModelPredictAction;

	public PredictionModelsManager(GroupModelPredictAction groupModelPredictAction) {
		this.groupModelPredictAction = groupModelPredictAction;
	}
	
	public GroupModelPredictAction getGroupModelPredictAction() {
		return groupModelPredictAction;
	}
	
	public Action getLastPredictedAction() {
		return groupModelPredictAction.getLastPredictedJointAction();
	}
	
	
	
}
