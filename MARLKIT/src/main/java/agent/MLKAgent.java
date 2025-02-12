package agent;

import agent.action.Action;
import environment.observation.Observation;
import learning.policy.Policy;

public interface MLKAgent {
	public Policy getPolicy();
	
	public void sendInfo();
	
	public void setPolicy(Policy policy);

	public Action takeAction(Observation obs);
	
}
