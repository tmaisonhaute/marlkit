package agent;

import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;
import learning.Experience;
import learning.policy.Policy;

public interface MLKAgent {
	public Policy getPolicy();
	
	public void sendInfo();
	
	public void setPolicy(Policy policy);
	
	public abstract void feedbackExperience(Observation obs, Action act, Reward rew);
	public abstract void feedbackExperience(Experience experience);
	
	public Action takeAction(Observation obs);
	
	public void updatePolicy(int timestep);
	public void learnOnBatch();
	public void endEpisode();

	
}
