package marlkit.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import util.MapProba;

/**
 * Scenario 1: Two production units with balanced production probabilities.
 */
public class Scenario6 extends ScenarioUP {

    protected Scenario6() {
		super();
	}

	@Override
	public void setup(RandomGenerator prng) {
        // Unit A: balanced production
        MapProba<Integer> probaA = new MapProba<>(prng);
        probaA.put(0, 0.0);
        probaA.put(1, 1.0);
        addUnit(ResourceType.A, probaA, 1, 5);
        
        // Unit B: balanced production
        MapProba<Integer> probaB = new MapProba<>(prng);
        probaB.put(0, 0.0);
        probaB.put(2, 1.0);
        addUnit(ResourceType.B, probaB, 2, 10);
        

        // Unit C: balanced production
        MapProba<Integer> probaC = new MapProba<>(prng);
        probaC.put(1, 1.0);
        addUnit(ResourceType.C, probaC, 1, 3);
        

        // Unit D: balanced production
        MapProba<Integer> probaD = new MapProba<>(prng);
        probaD.put(1, 1.0);
        addUnit(ResourceType.C, probaD, 1, 3);
        
        // Unit E: balanced production
        MapProba<Integer> probaE = new MapProba<>(prng);
        probaE.put(1, 1.0);
        addUnit(ResourceType.C, probaE, 1, 3);
        
    }

    @Override
    public Map<ResourceType, Float> getBasePrices() {
        Map<ResourceType, Float> basePrices = new HashMap<>();
        basePrices.put(ResourceType.A, 30f);
        basePrices.put(ResourceType.B, 12f);
        basePrices.put(ResourceType.C, -5f);
        return basePrices;
    }   
}