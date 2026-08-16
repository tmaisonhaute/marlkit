package agent.modelofotheragent;

import agent.action.Action;

/**
 * Models manager backed by a {@link GroupModelPredictAction}.
 *
 * <p>This manager acts as a lightweight wrapper used by learning agents to access the
 * group prediction model and its last predicted joint action. It is the default
 * {@link ModelsManager} implementation used by agents that maintain a single group-level
 * predictor, for example in joint-action learning workflows.</p>
 *
 * <p>For the model contract itself, see {@link ModelPredictAction} and
 * {@link GroupModelPredictAction}.</p>
 */
public class PredictionModelsManager implements ModelsManager {
	protected GroupModelPredictAction groupModelPredictAction;

	/**
	 * Create a manager for the given group prediction model.
	 *
	 * @param groupModelPredictAction group model used to predict joint actions.
	 */
	public PredictionModelsManager(GroupModelPredictAction groupModelPredictAction) {
		this.groupModelPredictAction = groupModelPredictAction;
	}
	
	/**
	 * Return the underlying group model.
	 *
	 * @return the group model.
	 */
	public GroupModelPredictAction getGroupModelPredictAction() {
		return groupModelPredictAction;
	}
	
	/**
	 * Return the last predicted joint action.
	 *
	 * @return last predicted joint action.
	 */
	public Action getLastPredictedAction() {
		return groupModelPredictAction.getLastPredictedAction();
	}
	
	
	
}
