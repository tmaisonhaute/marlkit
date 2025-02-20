package agent;
import java.util.List;

import agent.action.Action;
import environment.MLKEnvironment;
import environment.observation.Observation;
import environment.reward.Reward;
import learning.Batch;
import learning.Experience;
import learning.policy.Policy;
import madkit.simulation.SimuAgent;

/**
 * Represents an agent in the multi-agent system.
 */
public class AgentStandard extends SimuAgent implements MLKAgent{
	public Policy policy;
	public Batch pastExperiences;
	
	public AgentStandard(Policy policy) {
		this.policy = policy;
		pastExperiences = new Batch();
	}
	
	@Override
	protected void onActivation() {
		requestRole(getCommunity(), getModelGroup(), "mlkagent");
	}
	
	// TO DO : On launch, the agent should sendInfo()
	
	@Override
	public void sendInfo() {
		((MLKEnvironment) getEnvironment()).setupAgent(this);
	}

	/**
     * Takes an action based on a single observation.
     * 
     * @param obs the observation based on which the action is taken
     * @return the action taken
     */
	public Action takeAction(Observation obs) {
		return policy.takeAction(obs);
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
	
	public void feedbackExperience(Observation obs, Action act, Reward rew) {
		pastExperiences.addExperience(obs, act, rew);
	}
	public void feedbackExperience(Experience experience) {
		pastExperiences.addExperience(experience);
	}

	@Override
	public void learnOnBatch() {
		getLogger().info("Learning on batch");
		policy.learnOnBatch(pastExperiences, getLogger());
		pastExperiences.clear();
		
	}
	
}
