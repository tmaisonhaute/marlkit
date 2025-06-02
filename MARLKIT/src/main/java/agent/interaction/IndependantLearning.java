package agent.interaction;

import java.util.Collections;
import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import environment.observation.Observation;
import learning.Experience;

public class IndependantLearning implements MLKInteraction {

	@Override
	public void setAgentsGroup(AgentsGroup agentsGroup) {
		// No specific group handling for independent learning
	}

	@Override
	public Map<MLKAgent, Observation> getInteractionInformation(Map<MLKAgent, Observation> observationAgents) {
		return Collections.emptyMap();
	}

	@Override
	public void update(Map<MLKAgent, Experience> experiences) {
		// No interaction updates needed for independent learning
	}	

}
