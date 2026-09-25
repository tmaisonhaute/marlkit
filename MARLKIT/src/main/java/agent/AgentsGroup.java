package agent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	 * <p>
	 * If the agent is null, an IllegalArgumentException is thrown.
	 * </p>
	 * <p>
	 * If the agent is already contained, an IllegalArgumentException is thrown.
	 * </p>
	 *
	 * @param agent the agent to add
	 * @throws IllegalArgumentException if the agent is null
	 */
	public void addAgent(MLKAgent agent) {
		if (agent == null) {
			throw new IllegalArgumentException("Agent cannot be null");
		}
		if (agents.contains(agent)) {
			throw new IllegalArgumentException("Agent already exists in the group");
		}
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
