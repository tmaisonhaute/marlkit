package agent.modelofotheragent;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import learning.policy.PolicyInput;

public interface GroupModelPredictAction extends ModelPredictAction {
	/**
	 * Optionally informs the group model about the predicting (self) agent.
	 * Implementations that need to filter joint actions based on self can override this.
	 */
	default void setPredictingAgent(MLKAgent predictingAgent) {
	}
	
	default MLKAgent getPredictingAgent() {
		return null;
	}
	
	@Override
	public MappedJointAction predictAction(PolicyInput observation) ;
	
	@Override
	public MappedJointAction predictAction(PolicyInput observation, Action action) ;
	
	@Override
	public MappedJointAction getLastPredictedJointAction();

}
