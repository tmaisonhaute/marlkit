package marlkit.collectingresource.scenario;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import marlkit.collectingresource.environment.ResourceType;
import util.MapProba;
import util.Position;

/**
 * Scenario 5 for Trade2D with spatial units.
 */
public class ScenarioSpatial5 extends ScenarioCollectingResource {

	private static final double DISTANCE_PENALTY_PER_UNIT = 0.5;

	@Override
	protected void configure(RandomGenerator prng, double width, double height) {
		MapProba<Integer> probaA = new MapProba<>(prng);
		probaA.put(0, 0.0);
		probaA.put(1, 1.0);
		addUnit(ResourceType.A, probaA, 1, new Position(5, 4));

		MapProba<Integer> probaB = new MapProba<>(prng);
		probaB.put(0, 0.0);
		probaB.put(1, 1.0);
		addUnit(ResourceType.B, probaB, 1,  new Position(5, 5));

		MapProba<Integer> probaC = new MapProba<>(prng);
		probaC.put(0, 0.0);
		probaC.put(1, 1.0);
		addUnit(ResourceType.C, probaC, 1,  new Position(5, 6));

		addUnit(ResourceType.D, probaC, 1,  new Position(3, 4));

		addUnit(ResourceType.D, probaC, 1,  new Position(3, 5));

		addUnit(ResourceType.D, probaC, 1,  new Position(3, 6));

		addAgentPosition(new Position(4, 4));
		addAgentPosition(new Position(4, 5));
		addAgentPosition(new Position(4, 6));
	}

	@Override
	public Map<ResourceType, Float> getBasePrices() {
		Map<ResourceType, Float> basePrices = new HashMap<>();
		basePrices.put(ResourceType.A, 30f);
		basePrices.put(ResourceType.B, 5f);
		basePrices.put(ResourceType.C, 3f);
		basePrices.put(ResourceType.D, 0f);
		return basePrices;
	}

	@Override
	public double getDistancePenaltyPerUnit() {
		return DISTANCE_PENALTY_PER_UNIT;
	}
}
