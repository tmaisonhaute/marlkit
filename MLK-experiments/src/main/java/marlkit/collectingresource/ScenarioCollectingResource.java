package marlkit.collectingresource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import marlkit.trade.ResourceType;
import util.MapProba;
import util.Position;

/**
 * Base scenario for Trade2D with spatial production units.
 */
public abstract class ScenarioCollectingResource {
	protected final List<UPConfig> configs;
	protected final List<Position> agentPositions;

	protected ScenarioCollectingResource() {
		this.configs = new ArrayList<>();
		this.agentPositions = new ArrayList<>();
	}

	/**
	 * Setup the scenario configuration.
	 *
	 * @param prng Random generator.
	 * @param width Environment width.
	 * @param height Environment height.
	 */
	public void setup(RandomGenerator prng, double width, double height) {
		configs.clear();
		agentPositions.clear();
		configure(prng, width, height);
	}

	protected abstract void configure(RandomGenerator prng, double width, double height);

	public void initAgents(RandomGenerator prng, double width, double height, List<MLKAgent> agents) {
		if (agents.size() > agentPositions.size()) {
			throw new IllegalStateException("Not enough agent positions configured for Trade2D scenario.");
		}
		for (int i = 0; i < agents.size(); i++) {
			MLKAgent agent = agents.get(i);
			if (agent instanceof CollectingResourceAgent tradeAgent) {
				tradeAgent.setPosition(agentPositions.get(i).copy());
			}
		}
	}

	protected ScenarioCollectingResource addUnit(ResourceType type, MapProba<Integer> probabilityProduction,
			int initialStock, int maxStock, Position position) {
		configs.add(new UPConfig(type, probabilityProduction, initialStock, maxStock, position));
		return this;
	}

	protected ScenarioCollectingResource addUnit(ResourceType type, MapProba<Integer> probabilityProduction,
			int initialStock, Position position) {
		return this.addUnit(type, probabilityProduction, initialStock, Integer.MAX_VALUE, position);
	}

	protected ScenarioCollectingResource addAgentPosition(Position position) {
		agentPositions.add(position);
		return this;
	}

	public List<UniteProductionSpatial> createUnites(RandomGenerator prng) {
		List<UniteProductionSpatial> unites = new ArrayList<>();
		for (UPConfig config : configs) {
			unites.add(new UniteProductionSpatial(config.type, config.probabilityProduction,
					config.initialStock, config.maxStock, prng, config.position));
		}
		return unites;
	}

	protected Position randomPosition(RandomGenerator prng, double width, double height) {
		double x = prng.nextDouble(0.0, width);
		double y = prng.nextDouble(0.0, height);
		return new Position(x, y);
	}

	public abstract Map<ResourceType, Float> getBasePrices();

	public abstract double getDistancePenaltyPerUnit();

	protected static class UPConfig {
		private final ResourceType type;
		private final MapProba<Integer> probabilityProduction;
		private final int initialStock;
		private final int maxStock;
		private final Position position;

		protected UPConfig(ResourceType type, MapProba<Integer> probabilityProduction,
				int initialStock, int maxStock, Position position) {
			this.type = type;
			this.probabilityProduction = probabilityProduction;
			this.initialStock = initialStock;
			this.maxStock = maxStock;
			this.position = position;
		}
	}
	
	public int getNumberOfAgents() {
		return agentPositions.size();
	}
}
