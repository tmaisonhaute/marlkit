package agent.interaction;

import java.util.Collections;
import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import environment.observation.Observation;
import learning.Experience;

/**
 * Implementation of independent learning where agents do not coordinate
 * or share information with each other during learning.
 */
public class IndependantLearning implements MLKInteraction {

	/**
	 * No group setup needed for independent learning.
	 *
	 * @param agentsGroup the group of agents (unused)
	 */
	@Override
	public void setAgentsGroup(AgentsGroup agentsGroup) {
		// No specific group handling for independent learning
	}

	/**
	 * Returns an empty map as no interaction information is needed.
	 *
	 * @param observationAgents the current observations (unused)
	 * @return an empty map
	 */
	@Override
	public Map<MLKAgent, Observation> getInteractionInformation(Map<MLKAgent, Observation> observationAgents) {
		return Collections.emptyMap();
	}

	/**
	 * No updates needed for independent learning.
	 *
	 * @param experiences the agents' experiences (unused)
	 */
	@Override
	public void update(Map<MLKAgent, Experience> experiences) {
		// No interaction updates needed for independent learning
	}	

}
