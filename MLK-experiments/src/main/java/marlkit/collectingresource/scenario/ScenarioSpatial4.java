package marlkit.collectingresource.scenario;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import marlkit.trade.ResourceType;
import util.MapProba;
import util.Position;

/**
 * Scenario 4 for Trade2D with spatial units.
 */
public class ScenarioSpatial4 extends ScenarioCollectingResource {

	private static final double DISTANCE_PENALTY_PER_UNIT = 1;

	@Override
	protected void configure(RandomGenerator prng, double width, double height) {
		MapProba<Integer> probaA = new MapProba<>(prng);
		probaA.put(0, 0.0);
		probaA.put(1, 1.0);
		addUnit(ResourceType.A, probaA, 1, new Position(2, 4));

		MapProba<Integer> probaB = new MapProba<>(prng);
		probaB.put(0, 0.0);
		probaB.put(1, 1.0);
		addUnit(ResourceType.B, probaB, 1,  new Position(2, 5));

		MapProba<Integer> probaC = new MapProba<>(prng);
		probaC.put(0, 0.0);
		probaC.put(1, 1.0);
		addUnit(ResourceType.C, probaC, 1,  new Position(2, 6));
		

		addUnit(ResourceType.D, probaC, 1,  new Position(9, 1));
		addUnit(ResourceType.E, probaC, 1,  new Position(9, 2));

		addAgentPosition(new Position(1.3, 4));
		addAgentPosition(new Position(1.3, 5));
		addAgentPosition(new Position(1.3, 6));
		
		addAgentPosition(new Position(8, 1));
		addAgentPosition(new Position(8, 2));
	}

	@Override
	public Map<ResourceType, Float> getBasePrices() {
		Map<ResourceType, Float> basePrices = new HashMap<>();
		basePrices.put(ResourceType.A, 30f);
		basePrices.put(ResourceType.B, 5f);
		basePrices.put(ResourceType.C, 3f);
		basePrices.put(ResourceType.D, 6f);
		basePrices.put(ResourceType.E, 2f);
		return basePrices;
	}

	@Override
	public double getDistancePenaltyPerUnit() {
		return DISTANCE_PENALTY_PER_UNIT;
	}
}
