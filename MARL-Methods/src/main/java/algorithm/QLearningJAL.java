package algorithm;

import java.util.List;
import java.util.Optional;

import agent.action.Action;
import agent.action.JointAction;
import agent.modelofotheragent.GroupModelPredictAction;
import learning.policy.PolicyInput;
import learning.policy.QValueBasedPolicy;

public class QLearningJAL extends QValueBasedPolicy {
	
	protected GroupModelPredictAction groupModelPredictAction;
	protected final int selfIndex;

	public QLearningJAL(List<Action> actionsSet, GroupModelPredictAction moaGroupPredictAction) {
		super(actionsSet);
		this.groupModelPredictAction = moaGroupPredictAction;
		selfIndex = 0;
	}
	
	public void setGroupModelPredictAction(GroupModelPredictAction groupModel) {
		this.groupModelPredictAction = groupModel;
	}

	@Override
	public Action selectAction(PolicyInput input) {
		Optional<Action> exploratoryAction = getExplorationStrategy().getExploratoryAction(actionsSet, pnrg());
		if (!exploratoryAction.isEmpty()) {
			return exploratoryAction.get();
		}
		
	
	    Action bestAction = null;
	    double bestValue = Double.NEGATIVE_INFINITY;
	
	    for (Action ownAction : actionsSet) {
	    	JointAction predictedOthersActions = groupModelPredictAction.predictAction(input, ownAction);
	    	
	        JointAction jointAction = predictedOthersActions.withActionAtIndex(ownAction, selfIndex);
	
	        double value = qTable.getValue(input, jointAction);
	
	        if (bestAction == null || value > bestValue) {
	            bestValue = value;
	            bestAction = ownAction;
	        }
	    }
	
	    return bestAction;
	}
	
	
}
