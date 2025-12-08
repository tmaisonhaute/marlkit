package marlkit.trade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.observation.Observation;
import environment.state.State;

/**
 * State representation for the trading environment, focusing on production units. It gives all UniteProduction stock information.
 */
public class StateUnites implements State {
	private final List<MLKAgent> agents;
	private final List<UniteProduction> unitesProductions;

	public StateUnites(List<MLKAgent> agents, List<UniteProduction> unitesProductions) {
		this.agents = agents;
		this.unitesProductions = unitesProductions;
	}

	public List<UniteProduction> getUnitesProductions() {
		return unitesProductions;
	}
	public void updateState() {
		for (UniteProduction up : unitesProductions) {
			up.productResource();
		}
	}

	@Override
	public Map<MLKAgent, Observation> getObservations() {
		ObservationUnites obs = new ObservationUnites(unitesProductions);
		Map<MLKAgent, Observation> observations = new HashMap<>();
		for (MLKAgent agent : agents) {
			observations.put(agent, obs);
		}

		return observations;
	}

	@Override
	public void print() {
		System.out.println("StateUnites:");
		for (UniteProduction unite : unitesProductions) {
			System.out.println("  UniteProduction of type " + unite.getResourceType() + " has stock: " + unite.getStockValue());
		}

	}

}
