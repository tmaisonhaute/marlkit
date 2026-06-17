package modelofotheragents;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.ActionSpace;
import agent.action.MappedJointAction;
import agent.modelofotheragent.GroupModelPredictAction;
import learning.policies.PolicyInput;
import learning.policies.valuefunction.ActionEvaluator;

public class MinimaxValueFunctionPredictAction implements GroupModelPredictAction {
	protected ActionEvaluator evaluator;
	protected MLKAgent predictingAgent;
	protected MappedJointAction lastPredictedJointAction;
	
	public MinimaxValueFunctionPredictAction(ActionEvaluator evaluator) {
		this();
		setActionEvaluator(evaluator);
	}
	public MinimaxValueFunctionPredictAction() {
	}

	@Override
	public void setPredictingAgent(MLKAgent predictingAgent) {
		this.predictingAgent = predictingAgent;
	}

	@Override
	public MLKAgent getPredictingAgent() {
		return predictingAgent;
	}
	
	public void setActionEvaluator(ActionEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	@Override
	public void updateModel(PolicyInput observation, Action predictedAction, Action actualAction) {
		// No model to update in this implementation
	}

	@Override
	public MappedJointAction predictAction(PolicyInput observation) {
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
	public MappedJointAction predictAction(PolicyInput observation, Action action) {
		if (predictingAgent == null) {
			throw new IllegalStateException("Predicting agent must be set before calling predictAction.");
		}
		ActionSpace filteredActionSpace = filterPossibleJointActions(evaluator.getActionSpace(observation), action);
		if (filteredActionSpace.getActions().isEmpty()) {
			MappedJointAction empty = new MappedJointAction();
			this.lastPredictedJointAction = empty.copy();
			return empty;
		}
		MappedJointAction predictedJointAction = getWorstJointAction(observation, filteredActionSpace);
		MappedJointAction result = predictedJointAction.copy();
		result.removeAction(predictingAgent);
		this.lastPredictedJointAction = result.copy();
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
		for (Action actions : actionSpace.getActions()) {
			if (!(actions instanceof MappedJointAction jointAction)) {
				throw new IllegalArgumentException("ActionSpace must contain MappedJointActions");
			}
			Action selfAction = jointAction.getAction(predictingAgent);
			if (selfAction != null && selfAction.equals(ownAction)) {
				filteredActionSpace.addAction(jointAction);
			}
		}
		return filteredActionSpace;
	}
	
	
	/**
	 * Finds the JointAction in the given action space that has the lowest value according to the evaluator for the given observation.
	 * @param observation the observation for which to evaluate the JointActions
	 * @param actionSpace the ActionSpace containing the JointActions to evaluate
	 * @return the JointAction with the lowest value for the given observation according to the evaluator
	 */
	protected MappedJointAction getWorstJointAction(PolicyInput observation, ActionSpace actionSpace) {
		MappedJointAction worstJointAction = null;
		Double worstValue = Double.POSITIVE_INFINITY;
		for ( Action actions : actionSpace.getActions()) {
	        if (!(actions instanceof MappedJointAction jointAction)) {
	            throw new IllegalArgumentException("ActionSpace must contain MappedJointActions");
	        }
	        Double value = evaluator.getValue(observation, jointAction);
            
			if (value < worstValue || worstJointAction == null) {
				worstValue = value;
				worstJointAction = jointAction;
			}
		}   
		
		return worstJointAction;
	}

	@Override
	public MappedJointAction getLastPredictedJointAction() {
		return lastPredictedJointAction;
	}
	
	

}
