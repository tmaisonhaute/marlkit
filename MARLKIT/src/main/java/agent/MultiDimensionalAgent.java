package agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import agent.action.Action;
import communication.CommunicationModule;
import communication.NoCommunication;
import environment.MLKEnvironment;
import environment.observation.Observation;
import learning.Batch;
import learning.Experience;
import learning.algorithm.Algorithm;
import learning.policy.Policy;
import learning.policy.PolicyInput;
import madkit.simulation.SimuAgent;
import reward.Reward;

/**
 * An agent that supports multiple policies for different dimensions or aspects of learning.
 * Each policy is associated with a tag and maintains its own batch of experiences.
 */
public class MultiDimensionalAgent extends SimuAgent implements MLKAgent {

	protected Map<String, Policy> policies;
	protected Map<String, Algorithm> algorithms;
	protected Map<String, CommunicationModule> communicationModules;
	protected Map<String, Batch> dimensionalBatches;
	static final String DEFAULT_TAG = "default";
	
	protected Observation registeredObservation;
	
	
	public MultiDimensionalAgent(Policy policy, Algorithm algorithm, CommunicationModule communicationModule){
		super();
		policies = new HashMap<>();
		algorithms = new HashMap<>();
		communicationModules = new HashMap<>();
		dimensionalBatches = new HashMap<>();
		this.setPolicy(policy);
		this.setAlgorithm(algorithm);
		this.communicationModules.put(DEFAULT_TAG, communicationModule);
	}
	/**
	 * Creates a new multi-dimensional agent with a default policy and algorithm.
	 *
	 * @param policy the default policy for this agent
	 * @param algorithm the default algorithm for this agent
	 */
	public MultiDimensionalAgent(Policy policy, Algorithm algorithm) {
		this(policy, algorithm, new NoCommunication());
	}
	
	/**
	 * Called when the agent is activated in the simulation.
	 * Requests the agent role and initializes all policies.
	 */
	@Override
	protected void onActivation() {
		requestRole(getCommunity(), getModelGroup(), getRole());
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
	 * Sets the default policy for this agent. Uses default algorithm.
	 *
	 * @param policy the policy to use as default
	 */
	@Override
	public void setPolicy(Policy policy) {
        addPolicyAlgo(DEFAULT_TAG, policy, getAlgorithm());
    }
	
	/**
	 * Sets the algorithm for the default policy.
	 * @param algorithm the algorithm to use
	 */
	@Override
	public void setAlgorithm(Algorithm algorithm) {
		addPolicyAlgo(DEFAULT_TAG, getPolicy(), algorithm);
	}

	/** 
	 * Sets the default communication module for this agent.
	 * @param communicationModule the communication module to use for default policy
	 */
	@Override
	public void setCommunicationModule(CommunicationModule communicationModule) {
		this.communicationModules.put(DEFAULT_TAG, communicationModule);
	}

	/**
	 * Adds a communication module with a specific tag identifier.
	 * @param tag the tag associated with the communication module
	 * @param communicationModule the communication module to add
	 */
	public void setCommunicationModule(String tag, CommunicationModule communicationModule) {
		this.communicationModules.put(tag, communicationModule);
	}

	/**
	 * Adds a policy with a specific tag identifier.
	 *
	 * @param tag the identifier for this policy
	 * @param policy the policy to add
	 * @param algorithm the algorithm to add
	 */
	public void addPolicyAlgo(String tag, Policy policy, Algorithm algorithm) {
        policies.put(tag, policy);
        algorithms.put(tag, algorithm);
		algorithm.setPolicy(policy);
        dimensionalBatches.put(tag, new Batch());
		communicationModules.put(tag, getCommunicationModule());
    }
	
	/**
	 * Returns the default policy.
	 *
	 * @return the default policy
	 */
	@Override
	public Policy getPolicy() {
		return getPolicy(DEFAULT_TAG);
	}
	
	/**
	 * Returns the policy associated with the given tag.
	 *
	 * @param tag the policy identifier
	 * @return the policy, or null if not found
	 */
	public Policy getPolicy(String tag) {
        return policies.get(tag);
	}

	/**
	 * Returns the default algorithm.
	 *
	 * @return the default algorithm
	 */
	@Override
	public Algorithm getAlgorithm() {
		return getAlgorithm(DEFAULT_TAG);
	}

	/**
	 * Returns the algorithm associated with the given tag.
	 *
	 * @param tag the algorithm identifier
	 * @return the algorithm, or null if not found
	 */
	public Algorithm getAlgorithm(String tag) {
		return algorithms.get(tag);
	}

	@Override
	public CommunicationModule getCommunicationModule() {
		return this.communicationModules.get(DEFAULT_TAG);
	}

	/**
	 * Returns the communication module associated with the given tag.
	 * @param tag the communication module identifier
	 * @return the communication module, or null if not found
	 */
	public CommunicationModule getCommunicationModule(String tag) {
		return this.communicationModules.get(tag);
	}

	/**
	 * Initializes all policies and algorithms with necessary parameters.
	 */
	@Override
	public void initializeAll() {
		for (Policy policy : policies.values()) {
			policy.init(this);
		}
		for (Algorithm algorithm : algorithms.values()) {
			algorithm.init(this);
		}
	}
	
	/**
	 * Returns an unmodifiable view of all policies in this agent.
	 *
	 * @return map of tag to policy
	 */
	public Map<String, Policy> getAllPolicies() {
        return Collections.unmodifiableMap(policies);
    }

	/**
	 * Selects an action using the default policy based on the given observation.
	 *
	 * @param input the PolicyInput to act upon
	 * @return the selected action
	 */
	public Action selectAction(PolicyInput input) {
		return selectAction(DEFAULT_TAG, input);
	}
	
	/**
	 * Selects an action using the policy associated with the given tag.
	 *
	 * @param tag the policy identifier
	 * @param input the PolicyInput to act upon
	 * @return the selected action
	 * @throws IllegalArgumentException if no policy exists for the tag
	 */
	protected Action selectAction(String tag, PolicyInput input) {
        Policy policy = getPolicy(tag);
		if (policy != null) {
			return policy.selectAction(input);
		} else {
			throw new IllegalArgumentException("No policy found for tag: " + tag);
		}
	}

	/**
	 * Executes the action selection and influence process for this agent.
	 * The agent observes the environment, selects an action, and influences the environment accordingly.
	 */
	@Override
	public void takeAction() {
		Observation obs = getMLKEnvironment().getObservation(this);
		Action action = selectAction(obs);
		getMLKEnvironment().influence(this, action);
	}

	/**
	 * Executes the action selection and influence process using the policy associated with the given tag.
	 *
	 * @param tag the policy identifier to use for action selection
	 */
	public void takeAction(String tag) {
		Observation obs = getMLKEnvironment().getObservation(this);
		Action action = selectAction(tag, obs);
		getMLKEnvironment().influence(this, action);
	}

	/**
	 * Retrieves the experience from the environment and records it.
	 */
	@Override
	public void collectExperience() {
		Experience experience = getEnvExperience();
		if (experience != null) {
			feedbackExperience(experience);
		}
	}
	
	@Override
	public Experience getEnvExperience() {
		return getMLKEnvironment().getExperience(this);
	}

	/**
	 * Returns the environment cast to MLKEnvironment.
	 * @return the MLK environment
	 */
	@Override
	public MLKEnvironment getMLKEnvironment() {
		return ((MLKEnvironment) getEnvironment());
	}

	/**
	 * Records an experience using the default policy.
	 *
	 * @param input the PolicyInput received
	 * @param act the action taken
	 * @param rew the reward received
	 */
	@Override
	public void feedbackExperience(PolicyInput input, Action act, Reward rew) {
		feedbackExperience(DEFAULT_TAG, input, act, rew);
	}

	/**
	 * Records a complete experience using the default policy.
	 *
	 * @param experience the experience to record
	 */
	@Override
	public void feedbackExperience(Experience experience) {
		feedbackExperience(DEFAULT_TAG, experience);
	}
	
	/**
	 * Records an experience for the policy associated with the given tag.
	 *
	 * @param tag the policy identifier
	 * @param experience the experience to record
	 * @throws IllegalArgumentException if no batch exists for the tag
	 */
	public void feedbackExperience(String tag, Experience experience) {
		Batch batch = dimensionalBatches.get(tag);
		if (batch != null) {
			batch.addExperience(experience);
		} else {
			throw new IllegalArgumentException("No batch found for tag: " + tag);
		}
	}
	
	/**
	 * Records an experience for the policy associated with the given tag.
	 *
	 * @param tag the policy identifier
	 * @param input the PolicyInput received
	 * @param act the action taken
	 * @param rew the reward received
	 */
	public void feedbackExperience(String tag, PolicyInput input, Action act, Reward rew) {
		feedbackExperience(tag, new Experience(input, act, rew));
	}

	/**
	 * Updates all policies at the given timestep based on their learning frequencies.
	 *
	 * @param timestep the current timestep
	 */
	@Override
	public void updatePolicy(int timestep) {
		for (String key : policies.keySet()) {
			Algorithm algorithm = algorithms.get(key);
            if (algorithm.getLearningFrequency() > 0 && timestep % algorithm.getLearningFrequency() == 0) {
                learnOnBatch(key);
            }
        }
	}

	/**
	 * Performs learning on the default policy's batch.
	 */
	@Override
	public void learnOnBatch() {
		learnOnBatch(DEFAULT_TAG);
	}
	
	/**
	 * Performs learning on the batch associated with the given tag.
	 *
	 * @param tag the policy identifier
	 * @throws IllegalArgumentException if no policy or batch exists for the tag
	 */
	public void learnOnBatch(String tag) {
		Algorithm algorithm = getAlgorithm(tag);
		Batch batch = dimensionalBatches.get(tag);
		if (algorithm != null && batch != null) {
			algorithm.learnOnBatch(batch, getLogger());
		} else {
			throw new IllegalArgumentException("No policy or batch found for tag: " + tag);
		}
	}

	/**
	 * Signals the end of an episode for all policies.
	 */
	@Override
	public void endEpisode() {
		for (String key : policies.keySet()) {
			endEpisode(key);
		}
	}
	
	/**
	 * Signals the end of an episode for the policy associated with the given tag.
	 *
	 * @param tag the policy identifier
	 */
	public void endEpisode(String tag) {
        Algorithm algorithm = getAlgorithm(tag);
        Batch batch = dimensionalBatches.get(tag);
        if (algorithm != null && batch != null) {
            algorithm.endEpisode(batch, getLogger());
        }
    }
	@Override
	public SimuAgent getSimuAgent() {
		return this;
	}
	
	@Override
	public Observation getRegisteredObservation() {
		return registeredObservation;
	}
	
	@Override
	public void setRegisteredObservation(Observation registeredObservation) {
		this.registeredObservation = registeredObservation;
	}

	

}
