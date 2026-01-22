package marlkit.trade;

import java.util.Map;
import java.util.random.RandomGenerator;

import util.MapProba;

/**
 * Scenario 2: Three production units with high production rates.
 */
public class Scenario2 extends ScenarioUP {

    protected Scenario2() {
		super();
	}

	@Override
	public void setup(RandomGenerator prng) {
        // Unit A: high production
        MapProba<Integer> probaA = new MapProba<>(prng);
        probaA.put(1, 0.3);
        probaA.put(2, 0.4);
        probaA.put(3, 0.3);
        addUnit(ResourceType.A, probaA, 3);
        
        // Unit B: medium production
        MapProba<Integer> probaB = new MapProba<>(prng);
        probaB.put(0, 0.3);
        probaB.put(1, 0.4);
        probaB.put(2, 0.3);
        addUnit(ResourceType.B, probaB, 3);
        
        // Unit C: low production
        MapProba<Integer> probaC = new MapProba<>(prng);
        probaC.put(0, 0.5);
        probaC.put(1, 0.4);
        probaC.put(2, 0.1);
        addUnit(ResourceType.C, probaC, 3);
    }

    @Override
    public Map<ResourceType, Float> getBasePrices() {
        Map<ResourceType, Float> basePrices = new java.util.HashMap<>();
        basePrices.put(ResourceType.A, 6f);
        basePrices.put(ResourceType.B, 10f);
        basePrices.put(ResourceType.C, 12f);
        return basePrices;
    }  
}
