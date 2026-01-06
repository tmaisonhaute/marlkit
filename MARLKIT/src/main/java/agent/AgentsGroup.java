package agent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import agent.action.Action;
import environment.observation.Observation;

/**
 * Represents a collection of agents in the multi-agent system.
 * Provides methods for managing and coordinating multiple agents.
 */
public class AgentsGroup {
	private List<MLKAgent> agents;
	
	/**
	 * Creates a new agents group with the specified list of agents.
	 *
	 * @param agents the list of agents to include in the group
	 */
	public AgentsGroup(List<MLKAgent> agents) {
		this.agents = agents;
	}

	/**
	 * Creates an empty agents group.
	 */
	public AgentsGroup() {
		this.agents = new ArrayList<>();
	}
	
	/**
     * All agents in the group take actions based on their respective observations.
     * 
     * @param agentsObservations a map of agents to their respective observations
     * @return a map of agents to their respective actions
     */
	public Map<MLKAgent, Action> allAgentsTakeAction(Map<MLKAgent, Observation> agentsObservations){
		Map<MLKAgent, Action> actions = new HashMap<>();
		for(MLKAgent ag : agents) {
			Observation obs = agentsObservations.get(ag);
			Action action = ag.takeAction(obs);
			actions.put(ag, action);
		}
		return actions;
	}
	
	/**
     * Returns the list of agents in the group.
     * 
     * @return the list of agents
     */
	public List<MLKAgent> getAgents(){
		return agents;
	}

	/**
	 * Adds an agent to the group.
	 *
	 * @param agent the agent to add
	 */
	public void addAgent(MLKAgent agent) {
		agents.add(agent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(agents);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentsGroup other = (AgentsGroup) obj;
		return Objects.equals(agents, other.agents);
	}
	
	

}
