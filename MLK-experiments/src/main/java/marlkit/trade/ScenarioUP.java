package marlkit.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.MapProba;

public abstract class ScenarioUP {
    protected List<UPConfig> configs;
    
    public ScenarioUP() {
        this.configs = new ArrayList<>();
        setup();
    }
    
    /**
     * Abstract method to be implemented by concrete scenarios.
     * This is where production units should be configured.
     */
    protected abstract void setup();
    
    /**
     * Add a production unit configuration to the scenario.
     * 
     * @param type The type of resource produced.
     * @param probabilityProduction The probability map for production.
     * @param initialStock The initial stock value.
     * @return This ScenarioUP instance for method chaining.
     */
    protected ScenarioUP addUnit(ResourceType type, MapProba<Integer> probabilityProduction, int initialStock) {
        configs.add(new UPConfig(type, probabilityProduction, initialStock));
        return this;
    }
    
    /**
     * Create the list of UniteProduction based on the configured scenario.
     * 
     * @return A list of UniteProduction instances.
     */
    public List<UniteProduction> createUnites() {
        List<UniteProduction> unites = new ArrayList<>();
        for (UPConfig config : configs) {
            unites.add(new UniteProduction(config.type, config.probabilityProduction, config.initialStock));
        }
        return unites;
    }
    
    /**
     * Get the number of production units in this scenario.
     * 
     * @return The number of units.
     */
    public int getNbUnits() {
        return configs.size();
    }
 
    public abstract Map<ResourceType, Float> getBasePrices();

    /**
     * Internal class to store configuration for a single production unit.
     */
    protected static class UPConfig {
        ResourceType type;
        MapProba<Integer> probabilityProduction;
        int initialStock;
        
        UPConfig(ResourceType type, MapProba<Integer> probabilityProduction, int initialStock) {
            this.type = type;
            this.probabilityProduction = probabilityProduction;
            this.initialStock = initialStock;
        }
    }

}
