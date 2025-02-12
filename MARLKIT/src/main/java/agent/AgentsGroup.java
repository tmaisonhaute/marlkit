package agent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.action.Action;
import environment.observation.Observation;

/**
 * Represents a group of agents in the multi-agent system.
 */
public class AgentsGroup {
	private List<MLKAgent> agents;
	
	public AgentsGroup(List<MLKAgent> agents) {
		this.agents = agents;
	}

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

	public void addAgent(MLKAgent agent) {
		agents.add(agent);
	}

}
