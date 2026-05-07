package agent.modelofotheragent;

import java.util.LinkedHashMap;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import learning.policy.PolicyInput;

public class StandardGroupModelingPredictAction implements GroupModelPredictAction {
	protected Map<MLKAgent, ModelPredictAction> modelPredictActionsByAgent;
	protected MappedJointAction lastPredictedActions;
	
	public StandardGroupModelingPredictAction() {
		modelPredictActionsByAgent = new LinkedHashMap<>();
	}
	
	public StandardGroupModelingPredictAction(Map<MLKAgent, ModelPredictAction> modelPredictActionsByAgent) {
		this.modelPredictActionsByAgent = new LinkedHashMap<>(modelPredictActionsByAgent);
	}
	
	public void addModelPredictAction(MLKAgent modeledAgent, ModelPredictAction modelPredictAction) {
		modelPredictActionsByAgent.put(modeledAgent, modelPredictAction);
	}

	@Override
	public void updateModel(PolicyInput observation, Action predictedAction, Action actualAction) {
		MappedJointAction predictedMapped = predictedAction instanceof MappedJointAction m ? m : new MappedJointAction();
		if (!(actualAction instanceof MappedJointAction actualMapped)) {
			throw new IllegalArgumentException("Actual action must be an instance of MappedJointAction.");
		}
		for (Map.Entry<MLKAgent, ModelPredictAction> entry : modelPredictActionsByAgent.entrySet()) {
			MLKAgent agent = entry.getKey();
			ModelPredictAction modelPredictAction = entry.getValue();
			Action predicted = predictedMapped.getAction(agent);
			Action actual = actualMapped.getAction(agent);
			modelPredictAction.updateModel(observation, predicted, actual);
		}
	}

	@Override
	public MappedJointAction predictAction(PolicyInput observation) {
		MappedJointAction predictedJointAction = new MappedJointAction();
		for (Map.Entry<MLKAgent, ModelPredictAction> entry : modelPredictActionsByAgent.entrySet()) {
			MLKAgent agent = entry.getKey();
			ModelPredictAction modelPredictAction = entry.getValue();
			Action predicted = modelPredictAction.predictAction(observation);
			predictedJointAction.addAction(agent, predicted);
		}
		this.lastPredictedActions = predictedJointAction.copy();
		return predictedJointAction;
	}

	@Override
	public MappedJointAction predictAction(PolicyInput observation, Action action) {
		MappedJointAction predictedJointAction = new MappedJointAction();
		for (Map.Entry<MLKAgent, ModelPredictAction> entry : modelPredictActionsByAgent.entrySet()) {
			MLKAgent agent = entry.getKey();
			ModelPredictAction modelPredictAction = entry.getValue();
			Action predicted = modelPredictAction.predictAction(observation, action);
			predictedJointAction.addAction(agent, predicted);
		}
		this.lastPredictedActions = (MappedJointAction) predictedJointAction.copy();
		return predictedJointAction;
	}

	@Override
	public MappedJointAction getLastPredictedJointAction() {
		return lastPredictedActions;
	}

}
