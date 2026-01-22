package marlkit.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import util.MapProba;

/**
 * Scenario 1: Two production units with balanced production probabilities.
 */
public class Scenario4 extends ScenarioUP {

    protected Scenario4() {
		super();
	}

	@Override
	public void setup(RandomGenerator prng) {
        // Unit A: balanced production
        MapProba<Integer> probaA = new MapProba<>(prng);
        probaA.put(0, 0.0);
        probaA.put(1, 1.0);
        addUnit(ResourceType.A, probaA, 1);
        
        // Unit B: balanced production
        MapProba<Integer> probaB = new MapProba<>(prng);
        probaB.put(0, 0.0);
        probaB.put(3, 1.0);
        addUnit(ResourceType.B, probaB, 3);
        

        // Unit C: balanced production
        MapProba<Integer> probaC = new MapProba<>(prng);
        probaC.put(0, 1.0);
        addUnit(ResourceType.C, probaC, 0);
    }

    @Override
    public Map<ResourceType, Float> getBasePrices() {
        Map<ResourceType, Float> basePrices = new HashMap<>();
        basePrices.put(ResourceType.A, 30f);
        basePrices.put(ResourceType.B, 12f);
        basePrices.put(ResourceType.C, 0f);
        return basePrices;
    }   
}