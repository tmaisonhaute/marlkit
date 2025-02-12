package agent;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.List;

import agent.action.Action;
import environment.observation.Observation;
import learning.policy.Policy;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;

/**
 * Represents an agent in the multi-agent system.
 */
public class AgentStandard extends SimuAgent implements MLKAgent{
	public Policy policy;
	
	public AgentStandard(Policy policy) {
		this.policy = policy;
	}
	
	@Override
	protected void onActivation() {
		requestRole(getCommunity(), getModelGroup(), "mlkagent");
	}
	
	@Override
	public void sendInfo() {
		ObjectMessage<MLKAgent> messageAgent = new ObjectMessage<>(this);
		broadcast(messageAgent, getAgentsWithRole(getCommunity(), getModelGroup(), ENVIRONMENT_ROLE));
	}

	/**
     * Takes an action based on a single observation.
     * 
     * @param obs the observation based on which the action is taken
     * @return the action taken
     */
	public Action takeAction(Observation obs) {
		return policy.takeAction(obs);
		/*
		 * on get l'env, 
		 * On prend l'observation
		 * 
		 */
	}

	/**
     * Takes a list of actions based on a list of observations.
     * 
     * @param observations the list of observations based on which the actions are taken
     * @return the list of actions taken
     */
	public List<Action> takeActionList(List<Observation> observations){
		return policy.takeActionsList(observations);
	}

	@Override
	public Policy getPolicy() {
		return policy;
	}

	@Override
	public void setPolicy(Policy policy) {
		this.policy = policy;
		
	}
	
}
