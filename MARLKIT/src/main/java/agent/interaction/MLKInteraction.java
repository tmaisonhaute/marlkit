package agent.interaction;

import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import environment.observation.Observation;
import learning.Experience;

/**
 * Interface for defining interaction models between agents in a multi-agent system.
 * Different implementations represent different coordination strategies.
 */
public interface MLKInteraction {
	
	/**
	 * Sets the group of agents participating in this interaction.
	 *
	 * @param agentsGroup the group of agents
	 */
	public abstract void setAgentsGroup(AgentsGroup agentsGroup);
	
	/**
	 * Provides additional interaction-related observations for each agent.
	 *
	 * @param observationAgents the current observations for each agent
	 * @return a map of agents to their interaction-related observations
	 */
	public abstract Map<MLKAgent, Observation> getInteractionInformation(Map<MLKAgent, Observation> observationAgents);
	
	/**
	 * Updates the interaction model based on agents' experiences.
	 *
	 * @param experiences the experiences of each agent
	 */
	public abstract void update(Map<MLKAgent, Experience> experiences);
}

