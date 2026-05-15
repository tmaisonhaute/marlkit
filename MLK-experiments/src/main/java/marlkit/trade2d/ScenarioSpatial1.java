package marlkit.trade2d;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import marlkit.trade.ResourceType;
import util.MapProba;
import util.Position;

/**
 * Scenario 1 for Trade2D with spatial units.
 */
public class ScenarioSpatial1 extends ScenarioTrade2D {

	private static final double DISTANCE_PENALTY_PER_UNIT = 1;

	@Override
	protected void configure(RandomGenerator prng, double width, double height) {
		MapProba<Integer> probaA = new MapProba<>(prng);
		probaA.put(0, 0.0);
		probaA.put(1, 1.0);
		addUnit(ResourceType.A, probaA, 1, new Position(2, 2));

		MapProba<Integer> probaB = new MapProba<>(prng);
		probaB.put(0, 0.0);
		probaB.put(3, 1.0);
		addUnit(ResourceType.B, probaB, 3,  new Position(2, 5));

		MapProba<Integer> probaC = new MapProba<>(prng);
		probaC.put(0, 1.0);
		addUnit(ResourceType.C, probaC, 0,  new Position(2, 7));

		addAgentPosition(new Position(7, 2));
		addAgentPosition(new Position(7, 5));
		addAgentPosition(new Position(7, 7));
	}

	@Override
	public Map<ResourceType, Float> getBasePrices() {
		Map<ResourceType, Float> basePrices = new HashMap<>();
		basePrices.put(ResourceType.A, 30f);
		basePrices.put(ResourceType.B, 12f);
		basePrices.put(ResourceType.C, 0f);
		return basePrices;
	}

	@Override
	public double getDistancePenaltyPerUnit() {
		return DISTANCE_PENALTY_PER_UNIT;
	}
}
