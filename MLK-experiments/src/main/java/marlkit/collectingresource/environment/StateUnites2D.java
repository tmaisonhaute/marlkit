package marlkit.collectingresource.environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.observation.Observation;
import environment.state.State;
import marlkit.collectingresource.agent.CollectingResourceAgent;
import util.Position;

/**
 * State representation for the CollectingResource environment.
 */
public class StateUnites2D implements State {
	private final List<MLKAgent> agents;
	private final List<UniteProductionSpatial> unitesProductions;

	/**
	 * Create a state with agents and production units.
	 *
	 * @param agents Agents in the environment.
	 * @param unitesProductions Production units in the environment.
	 */
	public StateUnites2D(List<MLKAgent> agents, List<UniteProductionSpatial> unitesProductions) {
		this.agents = agents;
		this.unitesProductions = unitesProductions;
	}

	/**
	 * Reset all production units to their initial stock.
	 */
	@Override
	public void reset() {
		for (UniteProductionSpatial up : unitesProductions) {
			up.reset();
		}
	}

	/**
	 * Return the list of production units.
	 *
	 * @return Production units.
	 */
	public List<UniteProductionSpatial> getUnitesProductions() {
		return unitesProductions;
	}

	/**
	 * Update production for all units.
	 */
	public void updateState() {
		for (UniteProductionSpatial up : unitesProductions) {
			up.productResource();
		}
	}

	/**
	 * Return all agent positions.
	 *
	 * @return Map of agent to position.
	 */
	/**
	 * Return the position of a specific agent.
	 *
	 * @param agent Agent to locate.
	 * @return Agent position or null if not set.
	 */
	public Position getAgentPosition(MLKAgent agent) {
		if (agent instanceof CollectingResourceAgent tradeAgent) {
			return tradeAgent.getPosition();
		}
		return null;
	}

	/**
	 * Build observations for each agent.
	 *
	 * @return Observation map per agent.
	 */
	@Override
	public Map<MLKAgent, Observation> getObservations() {
		Map<MLKAgent, Observation> observations = new HashMap<>();
		for (MLKAgent agent : agents) {
			observations.put(agent, new ObservationUnites2D(unitesProductions));
		}
		return observations;
	}

	/**
	 * Return the list of agents.
	 *
	 * @return Agents list.
	 */
	public List<MLKAgent> getAgents() {
		return agents;
	}

	/**
	 * Print a textual view of the state.
	 */
	@Override
	public void print() {
		System.out.println("StateUnites2D:");
		for (UniteProductionSpatial unite : unitesProductions) {
			Position pos = unite.getPosition();
			System.out.println("  UniteProduction of type " + unite.getResourceType()
					+ " has stock: " + unite.getStockValue() + " at " + pos);
		}
	}
}
