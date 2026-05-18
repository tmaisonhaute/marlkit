package marlkit.collectingresource;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import marlkit.trade.ResourceType;
import util.MapProba;
import util.Position;

/**
 * Scenario 2 for Trade2D with spatial units.
 */
public class ScenarioSpatial2 extends ScenarioCollectingResource {
	private static final double DISTANCE_PENALTY_PER_UNIT = 1;

	@Override
	protected void configure(RandomGenerator prng, double width, double height) {
		MapProba<Integer> probaA = new MapProba<>(prng);
		probaA.put(0, 0.0);
		probaA.put(1, 1.0);
		addUnit(ResourceType.A, probaA, 1, new Position(8, 2));

		MapProba<Integer> probaB = new MapProba<>(prng);
		probaB.put(0, 0.0);
		probaB.put(2, 1.0);
		addUnit(ResourceType.B, probaB, 2,  new Position(2, 6));

		MapProba<Integer> probaC = new MapProba<>(prng);
		probaC.put(0, 0.0);
		probaC.put(1, 1.0);
		addUnit(ResourceType.C, probaC, 4,  new Position(9, 6));

		addAgentPosition(new Position(9, 2));
		addAgentPosition(new Position(1, 6));
		addAgentPosition(new Position(2, 7));
		addAgentPosition(new Position(8, 6));
	}

	@Override
	public Map<ResourceType, Float> getBasePrices() {
		Map<ResourceType, Float> basePrices = new HashMap<>();
		basePrices.put(ResourceType.A, 30f);
		basePrices.put(ResourceType.B, 12f);
		basePrices.put(ResourceType.C, 5f);
		return basePrices;
	}

	@Override
	public double getDistancePenaltyPerUnit() {
		return DISTANCE_PENALTY_PER_UNIT;
	}
}
