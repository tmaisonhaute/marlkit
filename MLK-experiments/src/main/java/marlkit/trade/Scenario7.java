package marlkit.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import util.MapProba;

/**
 * Scenario 1: Two production units with balanced production probabilities.
 */
public class Scenario7 extends ScenarioUP {

    protected Scenario7() {
		super();
	}

	@Override
	public void setup(RandomGenerator prng) {
        // Unit A: balanced production
        MapProba<Integer> probaA = new MapProba<>(prng);
        probaA.put(0, 0.0);
        probaA.put(3, 1.0);
        addUnit(ResourceType.A, probaA, 3, 10);
        
        // Unit B: balanced production
        MapProba<Integer> probaB = new MapProba<>(prng);
        probaB.put(0, 0.0);
        probaB.put(6, 1.0);
        addUnit(ResourceType.B, probaB, 6, 20);
        

        // Unit C: balanced production
        MapProba<Integer> probaC = new MapProba<>(prng);
        probaC.put(3, 1.0);
        addUnit(ResourceType.C, probaC, 3, 8);
        

        // Unit D: balanced production
        MapProba<Integer> probaD = new MapProba<>(prng);
        probaD.put(3, 1.0);
        addUnit(ResourceType.C, probaD, 1, 8);
        
        // Unit E: balanced production
        MapProba<Integer> probaE = new MapProba<>(prng);
        probaE.put(3, 1.0);
        addUnit(ResourceType.C, probaE, 1, 8);
        
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