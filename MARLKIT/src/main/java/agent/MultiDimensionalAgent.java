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

public class MultiDimensionalAgent extends SimuAgent implements MLKAgent {

	private Map<String, Policy> policies;
	private Map<String, Batch> dimensionalBatches;
	static final String DEFAULT_TAG = "default";
	
	public MultiDimensionalAgent(Policy policy) {
		super();
		policies = new HashMap<>();
		dimensionalBatches = new HashMap<>();
		this.setPolicy(policy);
	}
	
	@Override
	protected void onActivation() {
		requestRole(getCommunity(), getModelGroup(), "mlkagent");
        sendInfo();
		initializePolicy();
	}


	@Override
	public void sendInfo() {
        ((MLKEnvironment) getEnvironment()).setupAgent(this);
    }

	@Override
	public void setPolicy(Policy policy) {
        addPolicy(DEFAULT_TAG, policy);
    }
	
	public void addPolicy(String tag, Policy policy) {
        policies.put(tag, policy);
        dimensionalBatches.put(tag, new Batch());
    }
	
	@Override
	public Policy getPolicy() {
		return getPolicy(DEFAULT_TAG);
	}
	
	public Policy getPolicy(String tag) {
        return policies.get(tag);
	}

	@Override
	public void initializePolicy() {
		for (Policy policy : policies.values()) {
			policy.init(this);
		}
	}
	
	public Map<String, Policy> getAllPolicies() {
        return Collections.unmodifiableMap(policies);
    }

	@Override
	public Action takeAction(Observation obs) {
		return takeAction(DEFAULT_TAG, obs);
	}
	
	public Action takeAction(String tag, Observation obs) {
        Policy policy = getPolicy(tag);
		if (policy != null) {
			return policy.takeAction(obs);
		} else {
			throw new IllegalArgumentException("No policy found for tag: " + tag);
		}
	}

	@Override
	public void feedbackExperience(Observation obs, Action act, Reward rew) {
		feedbackExperience(DEFAULT_TAG, obs, act, rew);
	}

	@Override
	public void feedbackExperience(Experience experience) {
		feedbackExperience(DEFAULT_TAG, experience);
	}
	
	public void feedbackExperience(String tag, Experience experience) {
		Batch batch = dimensionalBatches.get(tag);
		if (batch != null) {
			batch.addExperience(experience);
		} else {
			throw new IllegalArgumentException("No batch found for tag: " + tag);
		}
	}
	
	public void feedbackExperience(String tag, Observation obs, Action act, Reward rew) {
		feedbackExperience(tag, new Experience(obs, act, rew));
	}

	@Override
	public void updatePolicy(int timestep) {
		for (String key : policies.keySet()) {
            Policy policy = policies.get(key);
            if (policy.getLearningFrequency() > 0 && timestep % policy.getLearningFrequency() == 0) {
                learnOnBatch(key);
            }
        }
	}

	@Override
	public void learnOnBatch() {
		learnOnBatch(DEFAULT_TAG);
	}
	
	public void learnOnBatch(String tag) {
		Policy policy = getPolicy(tag);
		Batch batch = dimensionalBatches.get(tag);
		if (policy != null && batch != null) {
			policy.learnOnBatch(batch, getLogger());
		} else {
			throw new IllegalArgumentException("No policy or batch found for tag: " + tag);
		}
	}

	@Override
	public void endEpisode() {
		for (String key : policies.keySet()) {
			endEpisode(key);
		}
	}
	
	public void endEpisode(String tag) {
        Policy policy = policies.get(tag);
        Batch batch = dimensionalBatches.get(tag);
        if (policy != null && batch != null) {
            policy.endEpisode(batch, getLogger());
        }
    }

}
