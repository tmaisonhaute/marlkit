package marlkit.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import util.MapProba;

/**
 * Scenario 1: Two production units with balanced production probabilities.
 */
public class Scenario5 extends ScenarioUP {

    protected Scenario5() {
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
        probaB.put(2, 1.0);
        addUnit(ResourceType.B, probaB, 2);
        

        // Unit C: balanced production
        MapProba<Integer> probaC = new MapProba<>(prng);
        probaC.put(0, 1.0);
        addUnit(ResourceType.C, probaC, 0);
        

        // Unit D: balanced production
        MapProba<Integer> probaD = new MapProba<>(prng);
        probaD.put(0, 1.0);
        addUnit(ResourceType.C, probaD, 0);
        
        // Unit E: balanced production
        MapProba<Integer> probaE = new MapProba<>(prng);
        probaE.put(0, 1.0);
        addUnit(ResourceType.C, probaE, 0);
        
        // Unit F: balanced production
        MapProba<Integer> probaF = new MapProba<>(prng);
        probaF.put(0, 1.0);
        addUnit(ResourceType.C, probaF, 0);
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