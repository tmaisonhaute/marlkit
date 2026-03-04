package agent;
import java.util.List;

import agent.action.Action;
import communication.CommunicationModule;
import communication.NoCommunication;
import environment.MLKEnvironment;
import environment.observation.Observation;
import environment.reward.Reward;
import learning.Batch;
import learning.Experience;
import learning.algorithm.Algorithm;
import learning.policy.Policy;
import learning.policy.PolicyInput;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;

/**
 * Standard implementation of an agent in the multi-agent reinforcement learning system.
 * This agent maintains a policy and accumulates experiences in a batch for learning.
 */
public class AgentStandard extends SimuAgent implements MLKAgent{
	protected Policy policy;
	protected Algorithm algorithm;
	protected CommunicationModule communicationModule;
	protected Batch pastExperiences;
	
	/**
	 * Creates a new standard agent with the specified policy and algorithm.
	 *
	 * @param policy the learning policy for this agent
	 * @param algorithm the learning algorithm for this agent
	 * @param communicationModule the communication module for this agent
	 */
	public AgentStandard(Policy policy, Algorithm algorithm, CommunicationModule communicationModule) {
		super();
		this.policy = policy;
		this.algorithm = algorithm;
		this.communicationModule = communicationModule;
		pastExperiences = new Batch();
	}

	/**
	 * Creates a new standard agent with the specified policy and algorithm, and no communication module.
	 *
	 * @param policy the learning policy for this agent
	 * @param algorithm the learning algorithm for this agent
	 */
	public AgentStandard(Policy policy, Algorithm algorithm) {
		this(policy, algorithm, new NoCommunication());
	}
	
	/**
	 * Called when the agent is activated in the simulation.
	 * Requests the agent role, registers itself with the environment, and initializes its policy and algorithm.
	 */
	@Override
	protected void onActivation() {
		requestRole(getCommunity(), getModelGroup(), "mlkagent");
        notifySelfToEnvironment();
		initializeAll();
	}
	
	/**
	 * Registers this agent with its environment so the environment can track it.
	 */
	@Override
	public void notifySelfToEnvironment() {
		getMLKEnvironment().addAgent(this);
	}

	/**
	 * Selects and returns an action based on the given observation.
	 *
	 * @param input the observation to act upon
	 * @return the selected action
	 */
	protected Action selectAction(PolicyInput input) {
		return policy.selectAction(input);
	}

	/**
	 * Executes the action selection and influence process for this agent.
	 * The agent observes the environment, selects an action, and influences the environment accordingly.
	 */
	@Override
	public void takeAction(){
		Observation obs = getExtendedObservation();
		Action action = selectAction(obs);
		getMLKEnvironment().influence(this, action);
	}

	/**
	 * Retrieves the experience from the environment and records it.
	 */
	@Override
	public void collectExperience() {
		Experience experience = getMLKEnvironment().getExperience(this);
		if (experience != null) {
			feedbackExperience(experience);
		}
	}

	/**
	 * Initializes the agent's policy and algorithm with necessary parameters.
	 */
	@Override
	public void initializeAll() {
		this.policy.init(this);
		this.algorithm.init(this);
	}
	
	/**
	 * Records an experience composed of observation, action, and reward.
	 *
	 * @param input the PolicyInput received
	 * @param act the action taken
	 * @param rew the reward received
	 */
	@Override
	public void feedbackExperience(PolicyInput input, Action act, Reward rew) {
		pastExperiences.addExperience(input, act, rew);
	}
	
	/**
	 * Records a complete experience object.
	 *
	 * @param experience the experience to record
	 */
	@Override
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
		if(getAlgorithm().getLearningFrequency() > 0 && timestep % getAlgorithm().getLearningFrequency() == 0) {
			learnOnBatch();
		}
	}

	/**
	 * Performs learning on the batch of accumulated experiences.
	 */
	@Override
	public void learnOnBatch() {
		algorithm.learnOnBatch(pastExperiences, getLogger());
	}
	
	/**
	 * Signals the end of an episode and performs episode-level learning.
	 */
	@Override
	public void endEpisode() {
		algorithm.endEpisode(pastExperiences, getLogger());
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

	@Override
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public CommunicationModule getCommunicationModule() {
		return communicationModule;
	}
	
	@Override
	public void setCommunicationModule(CommunicationModule communicationModule) {
		this.communicationModule = communicationModule;
	}

	@Override
	public MLKEnvironment getMLKEnvironment() {
		return ((MLKEnvironment) getEnvironment());
	}
	
	protected Observation getExtendedObservation() {
		Observation obs = getObservation();
		//TODO : get only Observation messages.
		List<ObjectMessage<Observation>> messagesObservations = getMailbox().getAll(null);
		return getCommunicationModule().extendObservation(obs, messagesObservations);
	}
	
}
