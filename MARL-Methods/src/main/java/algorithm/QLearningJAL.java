package algorithm;

import java.util.List;
import java.util.Optional;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import agent.modelofotheragent.GroupModelPredictAction;
import environment.observation.Observation;
import learning.policies.QValueBasedPolicy;

public class QLearningJAL extends QValueBasedPolicy {
	
	protected GroupModelPredictAction groupModelPredictAction;

	public QLearningJAL(List<Action> actionsSet, GroupModelPredictAction moaGroupPredictAction) {
		super(actionsSet);
		this.groupModelPredictAction = moaGroupPredictAction;
	}
	
	@Override
	public void init(MLKAgent agent) {
		super.init(agent);
		if (groupModelPredictAction != null) {
			groupModelPredictAction.setPredictingAgent(agent);
		}
	}
	
	public void setGroupModelPredictAction(GroupModelPredictAction groupModel) {
		this.groupModelPredictAction = groupModel;
		if (getAgent() != null && this.groupModelPredictAction != null) {
			this.groupModelPredictAction.setPredictingAgent(getAgent());
		}
	}

	@Override
	public Action selectAction(Observation input) {
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
	    	MappedJointAction predictedOthersActions = groupModelPredictAction.predictAction(input, ownAction);
	        MappedJointAction jointAction = predictedOthersActions.withAction(getAgent(), ownAction);
	
	        double value = qTable.getValue(input, jointAction);
	
	        if (bestAction == null || value > bestValue) {
	            bestValue = value;
	            bestAction = ownAction;
	        }
	    }
	
	    return bestAction;
	}
	
	
}
