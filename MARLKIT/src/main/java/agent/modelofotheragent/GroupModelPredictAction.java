package agent.modelofotheragent;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import environment.observation.MappedJointObservation;
import learning.policies.PolicyInput;

/**
 * Prediction model that returns a joint action for a group of agents.
 *
 * <p>This specialization of {@link ModelPredictAction} produces a {@link MappedJointAction}
 * that can include actions for multiple agents. Implementations may optionally be informed of
 * the predicting (self) agent via {@link #setPredictingAgent(MLKAgent)} to filter or adjust
 * predictions. The last predicted joint action is available via
 * {@link #getLastPredictedAction()} and is typically consumed by learning algorithms
 * that model other agents.</p>
 */
public interface GroupModelPredictAction extends ModelPredictAction {
	/**
	 * Optionally informs the group model about the predicting (self) agent.
	 * Implementations that need to filter joint actions based on self can override this.
	 */
	default void setPredictingAgent(MLKAgent predictingAgent) {
	}
	
	/**
	 * Return the agent for which predictions are computed.
	 *
	 * @return predicting agent, or null if not set.
	 */
	default MLKAgent getPredictingAgent() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MappedJointAction predictAction(PolicyInput observation);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MappedJointAction predictAction(PolicyInput observation, Action action);
	
	/**
	 * Predict a joint action from a mapped joint observation.
	 * 
	 * <p> Each agent's observation is used to predict its action, and the resulting actions are combined into a mapped joint action.<p>
	 * @param observation mapped joint observation used as input.
	 * @return predicted joint action.
	 */
	public MappedJointAction predictActionFromMappedObservation(MappedJointObservation observation);
	
	/**
	 * Predict a joint action from a mapped joint observation and the action of the predicting agent.
	 * @param observation mapped joint observation used as input.
	 * @param action action of the predicting agent used as input.
	 * @return predicted joint action.
	 */
	public MappedJointAction predictActionFromMappedObservation(MappedJointObservation observation, Action action);
	
	/**
	 * Return the last predicted joint action.
	 *
	 * @return last predicted joint action.
	 */
	@Override
	public MappedJointAction getLastPredictedAction();

}
