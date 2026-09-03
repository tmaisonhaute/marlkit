package marlkit.collectingresource.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import marlkit.collectingresource.agent.ActionRequestResource;
import marlkit.collectingresource.agent.CollectingResourceAgent;
import marlkit.collectingresource.environment.ResourceType;
import marlkit.collectingresource.environment.UniteProductionSpatial;
import util.MapProba;
import util.Position;

/**
* Creates an empty collecting-resource scenario.
* Units and agent positions are defined when {@link #setup(RandomGenerator, double, double)} 
* invokes {@link #configure(RandomGenerator, double, double)}.
*/
public abstract class ScenarioCollectingResource {

    protected final List<UPConfig> configs;
    protected final List<Position> agentPositions;
    protected final List<UniteProductionSpatial> unites;

    protected ScenarioCollectingResource() {
        this.configs = new ArrayList<>();
        this.agentPositions = new ArrayList<>();
        this.unites = new ArrayList<>();
    }

    /**
     * Setup the scenario configuration.
     *
     * @param prng Random generator.
     * @param width Environment width.
     * @param height Environment height.
     */
    public void setup(RandomGenerator prng, double width, double height) {
        configs.clear();
        agentPositions.clear();
        unites.clear();

        configure(prng, width, height);
        
        validateNumberOfAgents();
    }

    protected abstract void configure(RandomGenerator prng, double width, double height);

    public void initAgents(RandomGenerator prng, double width, double height, List<MLKAgent> agents) {
        if (agents.size() > agentPositions.size()) {
            throw new IllegalStateException("Not enough agent positions configured for Trade2D scenario.");
        }

        for (int i = 0; i < agents.size(); i++) {
            MLKAgent agent = agents.get(i);

            if (agent instanceof CollectingResourceAgent tradeAgent) {
                tradeAgent.setPosition(agentPositions.get(i).copy());
            }
        }
    }

    protected ScenarioCollectingResource addUnit(ResourceType type, MapProba<Integer> probabilityProduction,
            int initialStock, int maxStock, Position position) {
        configs.add(new UPConfig(type, probabilityProduction, initialStock, maxStock, position));
        return this;
    }

    protected ScenarioCollectingResource addUnit(ResourceType type, MapProba<Integer> probabilityProduction,
            int initialStock, Position position) {
        return addUnit(type, probabilityProduction, initialStock, Integer.MAX_VALUE, position);
    }

    protected ScenarioCollectingResource addAgentPosition(Position position) {
        agentPositions.add(position);
        return this;
    }

    /**
     * Creates the production units defined by this scenario.
     * <p>
     * The created units are stored internally and can be retrieved with {@link #getUnites()}.
     * This ensures that the environment and action specifications can refer to the same
     * production unit instances.
     * </p>
     *
     * @param prng random generator used by production units
     */
    public void createUnites(RandomGenerator prng) {
        unites.clear();

        for (UPConfig config : configs) {
            unites.add(new UniteProductionSpatial(
                    config.type,
                    config.probabilityProduction,
                    config.initialStock,
                    config.maxStock,
                    prng,
                    config.position
            ));
        }
    }

    /**
     * Returns the production units created by {@link #createUnites(RandomGenerator)}.
     *
     * @return immutable view of the created production units
     */
    public List<UniteProductionSpatial> getUnites() {
    	if (unites.isEmpty()) {
    		throw new IllegalStateException("Production units have not been created yet. createUnites() should have been called first.");
    	}
        return Collections.unmodifiableList(unites);
    }

    protected Position randomPosition(RandomGenerator prng, double width, double height) {
        double x = prng.nextDouble(0.0, width);
        double y = prng.nextDouble(0.0, height);
        return new Position(x, y);
    }

    public abstract Map<ResourceType, Float> getBasePrices();

    public abstract double getDistancePenaltyPerUnit();

    protected static class UPConfig {

        private final ResourceType type;
        private final MapProba<Integer> probabilityProduction;
        private final int initialStock;
        private final int maxStock;
        private final Position position;

        protected UPConfig(ResourceType type, MapProba<Integer> probabilityProduction,
                int initialStock, int maxStock, Position position) {
            this.type = type;
            this.probabilityProduction = probabilityProduction;
            this.initialStock = initialStock;
            this.maxStock = maxStock;
            this.position = position;
        }
    }

    public abstract int getNumberOfAgents();
    
    
    public List<Action> getPossibleActions() {
        List<Action> actions = new ArrayList<>();

        for (UniteProductionSpatial unite : getUnites()) {
            actions.add(new ActionRequestResource(unite));
        }

        return actions;
    }
    
    /**
     * Validates that the number of agent positions defined in the scenario matches the declared number of agents.
     * 
     * @throws IllegalStateException if the number of agent positions does not match the declared number of agents
     */
    protected void validateNumberOfAgents() {
        if (agentPositions.size() != getNumberOfAgents()) {
            throw new IllegalStateException(
                    "Scenario declares " + getNumberOfAgents()
                            + " agents, but defines " + agentPositions.size()
                            + " initial positions."
            );
        }
    }
    
    /**
     * Clears all scenario configurations, including production unit configurations, agent positions, and created production units.
     * Reset to an empty state.
     */
    public void clearAll() {
		configs.clear();
		agentPositions.clear();
		unites.clear();
    }
    
}