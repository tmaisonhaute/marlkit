package agent.moa;

import agent.action.Action;
import learning.policy.PolicyInput;

public interface MoaPredictAction {
	public Action predictAction(PolicyInput observation);
}
