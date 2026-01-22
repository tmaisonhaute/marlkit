package marlkit.trade;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import util.MapProba;

public class UniteProduction {
	private final int initialStock;
	private final ResourceQuantify resourceStock;
	private final int maxStock;
	private final MapProba<Integer> probabilityProduction;
	private RandomGenerator prng;

	/**
     * Constructor for UniteProduction.
     * 
     * @param type The type of resource produced.
     * @param probabilityProduction The probability distribution for production quantities.
	 * @param initialStock The initial stock of the resource.
	*/
	public UniteProduction(ResourceType type, MapProba<Integer> probabilityProduction, int initialStock, int maxStock, RandomGenerator prng) {
		this.resourceStock = new ResourceQuantify(type, initialStock);
		this.maxStock = maxStock;
		this.probabilityProduction = probabilityProduction;
		this.initialStock = initialStock;
		this.prng = prng;
	}
	
	/**
	 * Reset the resource stock to its initial value.
	 */
	public void reset() {
		this.resourceStock.setValue(initialStock);
	}
	
	/**
	 * Produce resources according to the production probability map.
	 */
	public void productResource() {
		int producedQuantity = probabilityProduction.randomlySelectKey();
		if (resourceStock.getValue() + producedQuantity > maxStock) {
			resourceStock.setValue(maxStock);
			return;
		}
		resourceStock.addValue(producedQuantity);
	}
	
	/**
     * Take a specified quantity of resources from the stock.
     * 
     * @param quantity The int quantity of resources to take.
     * @return A ResourceQuantify object representing the taken resources.
     */
	public ResourceQuantify takeResource(int quantity) {
		int availableQuantity = Math.min(quantity, resourceStock.getValue());
		resourceStock.addValue(-availableQuantity);
		return new ResourceQuantify(resourceStock.getType(), availableQuantity);
	}
	
	/**
	 * Get the type of resource produced by this unit.
	 * 
	 * @return The ResourceType of the produced resource.
	 */
	public ResourceType getResourceType() {
        return resourceStock.getType();
    }
	
	/**	
	 * Get the current stock value of the resource.
	 * @return The current stock value.
	 */
	public int getStockValue() {
		return resourceStock.getValue();
	}
	
	/**
	 * Add a specified value to the resource stock.
	 * @param val The value to add to the stock.
	 */
	public void addStock(int val) {
		this.resourceStock.addValue(val);
	}

	/**
	 * take all requests from agents and process them. Accept all if enough stock, otherwise reject the first ones until stock is empty.
	 * 
	 * @param requests A map of agents and their requested ResourceQuantify.
	 * @param quantityPerRequest The int value per request unit.
	 * @return A map of agents and the ResourceQuantify they received.
	 */
	public Map<MLKAgent, ResourceQuantify> processRequests(List<MLKAgent> requestingAgents, int quantityPerRequest) {
		Map<MLKAgent, ResourceQuantify> allocations = new HashMap<>();
		
		Collections.shuffle(requestingAgents, prng);
		
		for (MLKAgent agent : requestingAgents) {
			if (getStockValue() >= quantityPerRequest) {
				ResourceQuantify allocatedResource = takeResource(quantityPerRequest);
				allocations.put(agent, allocatedResource);
			} else{
				int quantityLeft = getStockValue();
				ResourceQuantify allocatedResource = takeResource(quantityLeft);
				allocations.put(agent, allocatedResource);
			}
		}
		return allocations;
	}

		

}
