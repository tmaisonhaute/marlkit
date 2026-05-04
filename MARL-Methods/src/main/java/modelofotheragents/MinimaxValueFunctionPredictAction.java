package modelofotheragents;

import agent.action.Action;
import agent.action.ActionSpace;
import agent.action.JointAction;
import agent.modelofotheragent.GroupModelPredictAction;
import learning.policy.PolicyInput;
import learning.policy.valuefunction.ActionEvaluator;

public class MinimaxValueFunctionPredictAction implements GroupModelPredictAction {
	protected ActionEvaluator evaluator;
	protected final int predictingAgentIndex;
	protected final int defaultJointActionSize;
	
	public MinimaxValueFunctionPredictAction(ActionEvaluator evaluator) {
		this.evaluator = evaluator;
		predictingAgentIndex = 0;
		defaultJointActionSize = 1;
	}
	
	@Override
	public void updateModel(PolicyInput observation, Action predictedAction, Action actualAction) {
		// No model to update in this implementation
	}

	@Override
	public JointAction predictAction(PolicyInput observation) {
		throw new UnsupportedOperationException("This method should not be used. Minimax prediction requires the own action as input to filter the possible joint actions.");
	}

	@Override
	/**
	 * Predicts the joint action of the based other agents. The prediction is made with a minimax approach.
	 * <p>
	 * The method first filters the possible joint actions in the action space to those that are consistent with the given own 
	 * action of the predicting agent. 
	 * Then, it evaluates these filtered joint actions using the provided ActionEvaluator and selects the one with the lowest value 
	 * (worst for the predicting agent) as the prediction. 
	 * Finally, it removes the action of the predicting agent from the predicted joint action before returning it.
	 * </p>
	 * @param observation the observation for which to predict the joint action of the other agents
	 * @param action the action of the predicting agent that should be consistent with the predicted joint action
	 * @return a JointAction representing the predicted actions of the other agents, consistent with the given own action, and chosen to be worst for the predicting agent according to the evaluator
	 */
	public JointAction predictAction(PolicyInput observation, Action action) {
		ActionSpace filteredActionSpace = filterPossibleJointActions(evaluator.getActionSpace(observation), action);
		handleEmptyActionSpace(filteredActionSpace, action);
		JointAction predictedJointAction = getWorstJointAction(observation, filteredActionSpace);
		
		JointAction result = (JointAction) predictedJointAction.copy();
		result.removeActionAtIndex(predictingAgentIndex);
        
		return result;
	}
	
	/**
	 * Filters the possible JointActions in the action space to those that are consistent with the given own action.
	 * @param actionSpace the original action space containing JointActions
	 * @param ownAction the action of the predicting agent that we want to be consistent with
	 * @return an ActionSpace containing only JointActions whose action at predictingAgentIndex equals ownAction
	 */
	protected ActionSpace filterPossibleJointActions(ActionSpace actionSpace, Action ownAction) {
		ActionSpace filteredActionSpace = new ActionSpace();
		for (Action action : actionSpace.getActions()) {
			if (!(action instanceof JointAction)) {
				throw new IllegalArgumentException("ActionSpace must contain JointActions");
			}
			JointAction jointAction = (JointAction) action;
            if (jointAction.getActionAtIndex(predictingAgentIndex).equals(ownAction)) {
            	filteredActionSpace.addAction(jointAction);
            }
		}
		return filteredActionSpace;
	}
	
	/**
	 * Handles the case when the filtered action space is empty. In this case, it creates a default JointAction where all actions are copies of
	 * the given own action and adds it to the action space. This ensures that there is always at least one JointAction to evaluate.
	 * @param actionSpace the ActionSpace to check and potentially modify
	 * @param ownAction the action of the predicting agent that should be consistent with the JointActions in the action space
	 */
	protected void handleEmptyActionSpace(ActionSpace actionSpace, Action ownAction) {
		if (actionSpace.getActions().isEmpty()) {
			JointAction defaultJointAction = new JointAction();
			for (int i = 0; i < defaultJointActionSize; i++) {
				defaultJointAction.addAction(ownAction.copy());
			}
			actionSpace.addAction(defaultJointAction);
		}
	}
	
	/**
	 * Finds the JointAction in the given action space that has the lowest value according to the evaluator for the given observation.
	 * @param observation the observation for which to evaluate the JointActions
	 * @param actionSpace the ActionSpace containing the JointActions to evaluate
	 * @return the JointAction with the lowest value for the given observation according to the evaluator
	 */
	protected JointAction getWorstJointAction(PolicyInput observation, ActionSpace actionSpace) {
		JointAction worstJointAction = null;
		Double worstValue = Double.POSITIVE_INFINITY;
		for ( Action action : actionSpace.getActions()) {
            if (!(action instanceof JointAction)) {
                throw new IllegalArgumentException("ActionSpace must contain JointActions");
            }
            JointAction jointAction = (JointAction) action;
            Double value = evaluator.getValue(observation, jointAction);
            
			if (value < worstValue || worstJointAction == null) {
				worstValue = value;
				worstJointAction = jointAction;
			}
		}   
		
		return worstJointAction;
	}

}
