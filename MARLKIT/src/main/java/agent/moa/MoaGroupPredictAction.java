package agent.moa;

import agent.action.JointAction;
import learning.policy.PolicyInput;

public interface MoaGroupPredictAction extends MoaPredictAction {
	
	@Override
	public JointAction predictAction(PolicyInput observation);

}
