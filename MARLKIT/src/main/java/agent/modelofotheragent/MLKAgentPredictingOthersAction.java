package agent.modelofotheragent;

import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import environment.MLKEnvironment;
import environment.observation.Observation;

public interface MLKAgentPredictingOthersAction extends MLKAgentModelingOthers {

	@Override
	public PredictionModelsManager getModelsManager();
	
	@Override
	public default void updateModelsOfOtherAgents() {
		MLKEnvironment env = getMLKEnvironment();
		
		Map<MLKAgent, Action> actions = env.getAgentsActions();
		MappedJointAction actualActions = new MappedJointAction(actions);
		Observation observation = env.getObservation(this);
		
		getModelsManager().getGroupModelPredictAction().updateModel(observation, getModelsManager().getLastPredictedAction(), actualActions);
		
		
	}
	
}
