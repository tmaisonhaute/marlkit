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
 * Standard implementation of an agent in the multi-agent reinforcement learning system.
 * This agent maintains a policy and accumulates experiences in a batch for learning.
 */
public class AgentStandard extends SimuAgent implements MLKAgent{
	public Policy policy;
	public Batch pastExperiences;
	
	/**
	 * Creates a new standard agent with the specified policy.
	 *
	 * @param policy the learning policy for this agent
	 */
	public AgentStandard(Policy policy) {
		super();
		this.setPolicy(policy);
		pastExperiences = new Batch();
	}
	
	/**
	 * Called when the agent is activated in the simulation.
	 * Requests the agent role and initializes the policy.
	 */
	@Override
	protected void onActivation() {
		requestRole(getCommunity(), getModelGroup(), "mlkagent");
        sendInfo();
		initializePolicy();
	}
	
	/**
	 * Sends agent information to the environment for setup.
	 */
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

	/**
	 * Returns the policy used by this agent.
	 *
	 * @return the agent's policy
	 */
	@Override
	public Policy getPolicy() {
		return policy;
	}

	/**
	 * Sets the policy for this agent.
	 *
	 * @param policy the policy to use
	 */
	@Override
	public void setPolicy(Policy policy) {
		this.policy = policy;

	}

	/**
	 * Initializes the agent's policy with necessary parameters.
	 */
	public void initializePolicy() {
		this.policy.init(this);
	}
	
	/**
	 * Records an experience composed of observation, action, and reward.
	 *
	 * @param obs the observation received
	 * @param act the action taken
	 * @param rew the reward received
	 */
	public void feedbackExperience(Observation obs, Action act, Reward rew) {
		pastExperiences.addExperience(obs, act, rew);
	}
	
	/**
	 * Records a complete experience object.
	 *
	 * @param experience the experience to record
	 */
	public void feedbackExperience(Experience experience) {
		pastExperiences.addExperience(experience);
	}
	
	/**
	 * Updates the policy at the given timestep if learning frequency criteria are met.
	 *
	 * @param timestep the current timestep
	 */
	@Override
	public void updatePolicy(int timestep) {
		if (policy.getLearningFrequency() > 0 && timestep % policy.getLearningFrequency() == 0) {
			learnOnBatch();
		}
	}

	/**
	 * Performs learning on the batch of accumulated experiences.
	 */
	@Override
	public void learnOnBatch() {
		policy.learnOnBatch(pastExperiences, getLogger());
	}
	
	/**
	 * Signals the end of an episode and performs episode-level learning.
	 */
	@Override
	public void endEpisode() {
		policy.endEpisode(pastExperiences, getLogger());
	}
	
}
