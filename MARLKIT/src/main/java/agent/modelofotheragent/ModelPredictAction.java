package agent.modelofotheragent;

import agent.action.Action;
import learning.policy.PolicyInput;

/**
 * Prediction model for other agents' actions.
 *
 * <p>Implementations estimate an action from observations (and optionally from a given action
 * of the predicting agent) and are updated with the actual action observed. This interface is
 * used by agents that maintain explicit models of other agents and by managers such as
 * {@link PredictionModelsManager}.</p>
 *
 * <p>For joint-action variants, see {@link GroupModelPredictAction}.</p>
 */
public interface ModelPredictAction {
	/**
	 * Predict an action from an observation.
	 *
	 * @param observation observation used as input.
	 * @return predicted action.
	 */
	Action predictAction(PolicyInput observation);

	/**
	 * Predict an action from an observation and an action.
	 *
	 * @param observation observation used as input.
	 * @param action action of the predicting agent.
	 * @return predicted action.
	 */
	Action predictAction(PolicyInput observation, Action action);
	
	/**
	 * Update the model using the observation, predicted action, and actual action.
	 *
	 * @param observation observation used as input.
	 * @param predictedAction action predicted by the model.
	 * @param actualAction action actually executed.
	 */
	void updateModel(PolicyInput observation, Action predictedAction, Action actualAction);
	
	/**
	 * Return the last predicted action.
	 *
	 * @return last predicted action.
	 */
	Action getLastPredictedJointAction();
}
