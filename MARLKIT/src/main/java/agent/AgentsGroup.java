package agent;
import environment.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a group of agents in the multi-agent system.
 */
public abstract class AgentsGroup {
	private List<Agent> agents;
	
	/**
     * All agents in the group take actions based on their respective observations.
     * 
     * @param agentsObservations a map of agents to their respective observations
     * @return a map of agents to their respective actions
     */
	public Map<Agent, Action> allAgentsTakeAction(Map<Agent, Observation> agentsObservations){
		Map<Agent, Action> actions = new HashMap<Agent, Action>();
		for(Agent ag : agents) {
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
	public List<Agent> getAgents(){
		return agents;
		
	}

}
