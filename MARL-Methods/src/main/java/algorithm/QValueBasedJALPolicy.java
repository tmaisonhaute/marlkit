package algorithm;

import java.util.List;
import java.util.Optional;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import agent.modelofotheragent.GroupModelPredictAction;
import agent.modelofotheragent.MLKAgentPredictingOthersAction;
import environment.observation.Observation;
import learning.policies.QValueBasedPolicy;

/**
 * QLearningJAL is a Q-learning algorithm that incorporates a model of other agents to predict their actions.
 * 
 * It extends the QValueBasedPolicy and overrides the action selection method to consider predicted actions of other agents.
 */
public class QValueBasedJALPolicy extends QValueBasedPolicy {
	
	protected GroupModelPredictAction groupModelPredictAction;

	public QValueBasedJALPolicy(List<Action> actionsSet) {
		super(actionsSet);
	}
	
	@Override
	public void init(MLKAgent agent) {
		super.init(agent);
		if (agent instanceof MLKAgentPredictingOthersAction predictingAgent) {
			if (predictingAgent.getModelsManager() == null) {
				throw new IllegalStateException("The agent must have a models manager before initializing QValueBasedJALPolicy.");
				}
			groupModelPredictAction = predictingAgent.getModelsManager().getGroupModelPredictAction();
		} else {
			throw new IllegalArgumentException("Agent must be an instance of MLKAgentPredictingOthersAction to use QValueBasedJALPolicy.");
		}
		if (groupModelPredictAction != null) {
			groupModelPredictAction.setPredictingAgent(agent);
		}
	}
	
	/**
	 * Sets the group model used for predicting other agents' actions.
	 * 
	 * @param groupModel
	 */
	public void setGroupModelPredictAction(GroupModelPredictAction groupModel) {
		this.groupModelPredictAction = groupModel;
		if (getAgent() != null && this.groupModelPredictAction != null) {
			this.groupModelPredictAction.setPredictingAgent(getAgent());
		}
	}

	@Override
	public Action selectAction(Observation observation) {
		Optional<Action> exploratoryAction = getExplorationStrategy().getExploratoryAction(actionsSet, prng());
		if (!exploratoryAction.isEmpty()) {
			return exploratoryAction.get();
		}
	
	    Action bestAction = null;
	    double bestValue = Double.NEGATIVE_INFINITY;

	    if (getAgent() == null) {
	    	throw new IllegalStateException("QLearningJAL must be initialized with an agent before selecting actions.");
	    }
	
	    for (Action ownAction : actionsSet) {
	    	MappedJointAction predictedOthersActions = groupModelPredictAction.predictAction(observation, ownAction);
	        MappedJointAction jointAction = predictedOthersActions.withAction(getAgent(), ownAction);
	
	        double value = qTable.getValue(observation, jointAction);
	
	        if (bestAction == null || value > bestValue) {
	            bestValue = value;
	            bestAction = ownAction;
	        }
	    }
	
	    return bestAction;
	}
	
	
}
