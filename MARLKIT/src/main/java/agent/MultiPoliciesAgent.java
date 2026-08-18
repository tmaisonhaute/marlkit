package agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import agent.action.Action;
import environment.MLKEnvironment;
import environment.observation.Observation;
import experience.Experience;
import learning.Algorithm;
import learning.Batch;
import learning.Policy;
import madkit.simulation.SimuAgent;

/**
 * An agent supporting multiple tagged policy-algorithm pairs.
 *
 * <p>Each pair maintains its own experience buffer. The associated algorithm
 * determines whether the buffer is consumed, cleared, retained, or used as a
 * replay buffer.</p>
 */
public abstract class MultiPoliciesAgent extends SimuAgent implements MLKAgent {

	protected Map<String, Policy> policies;
	protected Map<String, Algorithm> algorithms;
	protected Map<String, Batch> experienceBuffers;
	static final String DEFAULT_TAG = "default";
	
	protected Observation registeredObservation;
	
	/**
	 * Creates a new multi-dimensional agent with a default policy and algorithm.
	 *
	 * @param policy the default policy for this agent
	 * @param algorithm the default algorithm for this agent
	 */
	protected MultiPoliciesAgent(Policy policy, Algorithm algorithm){
		super();
		policies = new HashMap<>();
		algorithms = new HashMap<>();
		experienceBuffers = new HashMap<>();
		addPolicyAlgo(DEFAULT_TAG, policy, algorithm);
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
		policies.put(DEFAULT_TAG, policy);
		Algorithm algorithm = getAlgorithm();
		if (algorithm != null && policy != null) {
			algorithm.setPolicy(policy);
		}
		if (!experienceBuffers.containsKey(DEFAULT_TAG)) {
			experienceBuffers.put(DEFAULT_TAG, new Batch());
		}
    }
	
	/**
	 * Sets the algorithm for the default policy.
	 * @param algorithm the algorithm to use
	 */
	@Override
	public void setAlgorithm(Algorithm algorithm) {
		algorithms.put(DEFAULT_TAG, algorithm);
		Policy policy = getPolicy();
		if (algorithm != null && policy != null) {
			algorithm.setPolicy(policy);
		}
		if (!experienceBuffers.containsKey(DEFAULT_TAG)) {
			experienceBuffers.put(DEFAULT_TAG, new Batch());
		}
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
		if (algorithm != null && policy != null) {
			algorithm.setPolicy(policy);
		}
		if (!experienceBuffers.containsKey(tag)) {
			experienceBuffers.put(tag, new Batch());
		}
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


	/**
	 * Initializes all policies and algorithms with necessary parameters.
	 */
	@Override
	public void initializeAll() {
		for (Policy policy : policies.values()) {
			if (policy != null) {
				policy.init(this);
			}
		}
		for (Algorithm algorithm : algorithms.values()) {
			if (algorithm != null) {
				algorithm.init(this);
			}
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
	 * @param input the Observation to act upon
	 * @return the selected action
	 */
	public Action selectAction(Observation input) {
		return selectAction(DEFAULT_TAG, input);
	}
	
	/**
	 * Selects an action using the policy associated with the given tag.
	 *
	 * @param tag the policy identifier
	 * @param input the Observation to act upon
	 * @return the selected action
	 * @throws IllegalArgumentException if no policy exists for the tag
	 */
	protected Action selectAction(String tag, Observation input) {
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
		Observation obs = getRegisteredObservation();
		Action action = selectAction(obs);
		getMLKEnvironment().influence(this, action);
	}

	/**
	 * Executes the action selection and influence process using the policy associated with the given tag.
	 *
	 * @param tag the policy identifier to use for action selection
	 */
	public void takeAction(String tag) {
		Observation obs = getRegisteredObservation();
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
		Batch buffer = experienceBuffers.get(tag);
		if (buffer == null) {
			throw new IllegalArgumentException("No experience buffer found for tag: " + tag);
		}
		
		buffer.addExperience(experience);
	}


	/**
	 * Updates all policies at the given timestep based on their learning frequencies.
	 *
	 * @param timestep the current timestep
	 */
	@Override
	public void updatePolicy(int timestep) {
	    for (String tag : policies.keySet()) {
	        Algorithm algorithm = algorithms.get(tag);
	        Batch experienceBuffer = experienceBuffers.get(tag);

	        if (algorithm != null && experienceBuffer != null && algorithm.shouldLearn(timestep, experienceBuffer)) {
	            learnOnBatch(tag);
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
		Batch buffer = experienceBuffers.get(tag);
		
		if (algorithm == null || buffer == null)  {
			throw new IllegalArgumentException("No algorithm or buffer found for tag: " + tag);
		}
		algorithm.learnOnBatch(buffer, getLogger());
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
        Batch buffer = experienceBuffers.get(tag);
        
        if (algorithm != null && buffer != null) {
            algorithm.endEpisode(buffer, getLogger());
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
