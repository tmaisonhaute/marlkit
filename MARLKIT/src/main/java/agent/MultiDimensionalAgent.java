package agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import agent.action.Action;
import environment.MLKEnvironment;
import environment.reward.Reward;
import learning.Batch;
import learning.Experience;
import learning.algorithm.Algorithm;
import learning.policy.Policy;
import learning.policy.PolicyInput;
import madkit.simulation.SimuAgent;

/**
 * An agent that supports multiple policies for different dimensions or aspects of learning.
 * Each policy is associated with a tag and maintains its own batch of experiences.
 */
public class MultiDimensionalAgent extends SimuAgent implements MLKAgent {

	private Map<String, Policy> policies;
	private Map<String, Algorithm> algorithms;
	private Map<String, Batch> dimensionalBatches;
	static final String DEFAULT_TAG = "default";
	
	/**
	 * Creates a new multi-dimensional agent with a default policy.
	 *
	 * @param policy the default policy for this agent
	 */
	public MultiDimensionalAgent(Policy policy, Algorithm algorithm) {
		super();
		policies = new HashMap<>();
		algorithms = new HashMap<>();
		dimensionalBatches = new HashMap<>();
		this.setPolicy(policy);
		this.setAlgorithm(algorithm);
	}
	
	/**
	 * Called when the agent is activated in the simulation.
	 * Requests the agent role and initializes all policies.
	 */
	@Override
	protected void onActivation() {
		requestRole(getCommunity(), getModelGroup(), "mlkagent");
        sendInfo();
		initializeAll();
	}


	/**
	 * Sends agent information to the environment for setup.
	 */
	@Override
	public void sendInfo() {
        ((MLKEnvironment) getEnvironment()).addAgent(this);
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

	public Algorithm getAlgorithm() {
		return getAlgorithm(DEFAULT_TAG);
	}

	public Algorithm getAlgorithm(String tag) {
		return algorithms.get(tag);
	}

	/**
	 * Initializes all policies with necessary parameters.
	 */
	@Override
	public void initializeAll() {
		for (Policy policy : policies.values()) {
			policy.init(this);
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
	 * Takes an action using the default policy based on the given observation.
	 *
	 * @param input the PolicyInput to act upon
	 * @return the selected action
	 */
	@Override
	public Action takeAction(PolicyInput input) {
		return takeAction(DEFAULT_TAG, input);
	}
	
	/**
	 * Takes an action using the policy associated with the given tag.
	 *
	 * @param tag the policy identifier
	 * @param input the PolicyInput to act upon
	 * @return the selected action
	 * @throws IllegalArgumentException if no policy exists for the tag
	 */
	public Action takeAction(String tag, PolicyInput input) {
        Policy policy = getPolicy(tag);
		if (policy != null) {
			return policy.takeAction(input);
		} else {
			throw new IllegalArgumentException("No policy found for tag: " + tag);
		}
	}

	/**
	 * Records an experience using the default policy.
	 *
	 * @param obs the observation received
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

	

}
