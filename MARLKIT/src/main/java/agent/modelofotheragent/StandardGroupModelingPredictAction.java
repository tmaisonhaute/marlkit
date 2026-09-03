package agent.modelofotheragent;

import java.util.LinkedHashMap;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import environment.observation.MappedJointObservation;
import environment.observation.Observation;

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
	public void updateModel(Observation observation, Action predictedAction, Action actualAction) {
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
	public MappedJointAction predictAction(Observation observation) {
	    return predictActions(observation, null);
	}

	@Override
	public MappedJointAction predictAction(Observation observation, Action action) {
	    return predictActions(observation, action);
	}
	
	@Override
	public MappedJointAction predictActionFromMappedObservation(MappedJointObservation observation) {
		return predictActions(observation, null);
	}

	@Override
	public MappedJointAction predictActionFromMappedObservation(MappedJointObservation observation, Action action) {
	    return predictActions(observation, action);
	}
	
	/**
	 * 
	 * @param observation
	 * @param action
	 * @return
	 */
	private MappedJointAction predictActions(Observation observation, Action action) {
	    MappedJointAction predictedJointAction = new MappedJointAction();

	    for (Map.Entry<MLKAgent, ModelPredictAction> entry : modelPredictActionsByAgent.entrySet()) {
	        MLKAgent agent = entry.getKey();
	        ModelPredictAction model = entry.getValue();
	        Observation modelObservation;

	        if (observation instanceof MappedJointObservation mappedObservation) {
	            modelObservation = mappedObservation.getObservation(agent);

	            if (modelObservation == null) {
	                throw new IllegalArgumentException("No observation found for agent: " + agent);
	            }
	        } else {
	        	modelObservation = observation;
	        }

	        Action predictedAction;

	        if (action == null) {
	            predictedAction = model.predictAction(modelObservation);
	        } else {
	            predictedAction = model.predictAction(modelObservation, action);
	        }

	        predictedJointAction.addAction(agent, predictedAction);
	    }

	    lastPredictedActions = predictedJointAction.copy();
	    return predictedJointAction;
	}

	@Override
	public MappedJointAction getLastPredictedAction() {
		return lastPredictedActions;
	}

}
