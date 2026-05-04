package agent.modelofotheragent;

import agent.action.Action;
import agent.action.JointAction;
import learning.policy.PolicyInput;

public interface GroupModelPredictAction extends ModelPredictAction {
	
	@Override
	public JointAction predictAction(PolicyInput observation) ;
	
	@Override
	public JointAction predictAction(PolicyInput observation, Action action) ;

}
