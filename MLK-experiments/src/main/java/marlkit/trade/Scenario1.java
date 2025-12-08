package marlkit.trade;

import java.util.Map;

import util.MapProba;

/**
 * Scenario 1: Two production units with balanced production probabilities.
 */
public class Scenario1 extends ScenarioUP {

    @Override
    protected void setup() {
        // Unit A: balanced production
        MapProba<Integer> probaA = new MapProba<>();
        probaA.put(0, 0.2);
        probaA.put(1, 0.5);
        probaA.put(2, 0.3);
        addUnit(ResourceType.A, probaA, 2);
        
        // Unit B: balanced production
        MapProba<Integer> probaB = new MapProba<>();
        probaB.put(0, 0.2);
        probaB.put(1, 0.5);
        probaB.put(2, 0.3);
        addUnit(ResourceType.B, probaB, 2);
    }

    @Override
    public Map<ResourceType, Float> getBasePrices() {
        Map<ResourceType, Float> basePrices = new java.util.HashMap<>();
        basePrices.put(ResourceType.A, 6f);
        basePrices.put(ResourceType.B, 10f);
        return basePrices;
    }   
}