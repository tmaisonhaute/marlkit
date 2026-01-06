package agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import agent.action.Action;
import environment.MLKEnvironment;
import environment.observation.Observation;
import environment.reward.Reward;
import learning.Batch;
import learning.Experience;
import learning.policy.Policy;
import madkit.simulation.SimuAgent;

/**
 * An agent that supports multiple policies for different dimensions or aspects of learning.
 * Each policy is associated with a tag and maintains its own batch of experiences.
 */
public class MultiDimensionalAgent extends SimuAgent implements MLKAgent {

	private Map<String, Policy> policies;
	private Map<String, Batch> dimensionalBatches;
	static final String DEFAULT_TAG = "default";
	
	/**
	 * Creates a new multi-dimensional agent with a default policy.
	 *
	 * @param policy the default policy for this agent
	 */
	public MultiDimensionalAgent(Policy policy) {
		super();
		policies = new HashMap<>();
		dimensionalBatches = new HashMap<>();
		this.setPolicy(policy);
	}
	
	/**
	 * Called when the agent is activated in the simulation.
	 * Requests the agent role and initializes all policies.
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
	 * Sets the default policy for this agent.
	 *
	 * @param policy the policy to use as default
	 */
	@Override
	public void setPolicy(Policy policy) {
        addPolicy(DEFAULT_TAG, policy);
    }
	
	/**
	 * Adds a policy with a specific tag identifier.
	 *
	 * @param tag the identifier for this policy
	 * @param policy the policy to add
	 */
	public void addPolicy(String tag, Policy policy) {
        policies.put(tag, policy);
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

	/**
	 * Initializes all policies with necessary parameters.
	 */
	@Override
	public void initializePolicy() {
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
	 * @param obs the observation to act upon
	 * @return the selected action
	 */
	@Override
	public Action takeAction(Observation obs) {
		return takeAction(DEFAULT_TAG, obs);
	}
	
	/**
	 * Takes an action using the policy associated with the given tag.
	 *
	 * @param tag the policy identifier
	 * @param obs the observation to act upon
	 * @return the selected action
	 * @throws IllegalArgumentException if no policy exists for the tag
	 */
	public Action takeAction(String tag, Observation obs) {
        Policy policy = getPolicy(tag);
		if (policy != null) {
			return policy.takeAction(obs);
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
	public void feedbackExperience(Observation obs, Action act, Reward rew) {
		feedbackExperience(DEFAULT_TAG, obs, act, rew);
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
	 * @param obs the observation received
	 * @param act the action taken
	 * @param rew the reward received
	 */
	public void feedbackExperience(String tag, Observation obs, Action act, Reward rew) {
		feedbackExperience(tag, new Experience(obs, act, rew));
	}

	/**
	 * Updates all policies at the given timestep based on their learning frequencies.
	 *
	 * @param timestep the current timestep
	 */
	@Override
	public void updatePolicy(int timestep) {
		for (String key : policies.keySet()) {
            Policy policy = policies.get(key);
            if (policy.getLearningFrequency() > 0 && timestep % policy.getLearningFrequency() == 0) {
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
		Policy policy = getPolicy(tag);
		Batch batch = dimensionalBatches.get(tag);
		if (policy != null && batch != null) {
			policy.learnOnBatch(batch, getLogger());
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
        Policy policy = policies.get(tag);
        Batch batch = dimensionalBatches.get(tag);
        if (policy != null && batch != null) {
            policy.endEpisode(batch, getLogger());
        }
    }

}
