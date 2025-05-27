package agent.interaction;

import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import environment.observation.Observation;
import learning.Experience;

public interface MLKInteraction {
	public abstract void getAgentsGroup(AgentsGroup agentsGroup);
	public abstract Map<MLKAgent, Observation> getInteractionInformation(Map<MLKAgent, Observation> observationAgents);
	public abstract void update(Map<MLKAgent, Experience> experiences);
}

