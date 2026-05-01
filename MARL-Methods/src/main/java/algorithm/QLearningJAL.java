package algorithm;

import java.util.List;
import java.util.Optional;

import agent.action.Action;
import agent.action.JointAction;
import agent.moa.MoaGroupPredictAction;
import learning.policy.PolicyInput;
import learning.policy.QValueBasedPolicy;

public class QLearningJAL extends QValueBasedPolicy {
	
	protected MoaGroupPredictAction moaGroupPredictAction;

	public QLearningJAL(List<Action> actionsSet, MoaGroupPredictAction moaGroupPredictAction) {
		super(actionsSet);
		this.moaGroupPredictAction = moaGroupPredictAction;
	}
	

	@Override
	public Action selectAction(PolicyInput input) {
		Optional<Action> exploratoryAction = getExplorationStrategy().getExploratoryAction(actionsSet, pnrg());
		if (!exploratoryAction.isEmpty()) {
			return exploratoryAction.get();
		}
		
	    JointAction predictedOthersActions = moaGroupPredictAction.predictAction(input);
	
	    Action bestAction = null;
	    double bestValue = Double.NEGATIVE_INFINITY;
	
	    for (Action ownAction : actionsSet) {
	        JointAction jointAction = predictedOthersActions.withActionFirst(ownAction);
	
	        double value = qTable.getValue(input, jointAction);
	
	        if (bestAction == null || value > bestValue) {
	            bestValue = value;
	            bestAction = ownAction;
	        }
	    }
	
	    return bestAction;
	}
	
	
}
