package util;

import java.util.HashMap;
import java.util.random.RandomGenerator;

/**
 * HashMap allowing random key selection based on probabilities (weights).
 * Values represent the weights associated with each key.
 */
public class MapProba<K> extends HashMap<K, Double> {
    
    private final RandomGenerator prng;
    
    public MapProba(RandomGenerator prng) {
    	super();
    	this.prng = prng;
    	
    }
    
    /**
     * Calculates the total sum of weights in the map.
     * @return the sum of all values (weights)
     */
    private double getTotalWeight() {
        double sum = 0.0;
        for (Double v : values()) {
            if (v != null) {
                sum += v;
            }
        }
        return sum;
    }
    
    /**
     * Normalizes probabilities so their sum equals 1.0.
     */
    public void normalize() {
        double total = getTotalWeight();
        if (total > 0) {
            replaceAll((_, v) -> v / total);
        }
    }
    
    /**
     * Selects a key randomly according to associated weights.
     * Uses the roulette wheel method: a random number is generated
     * and the key corresponding to that position on the cumulative distribution is returned.
     * @return a randomly selected key, or null if the map is empty
     */
    public K randomlySelectKey() {
        double total = getTotalWeight();
        if (total <= 0 || isEmpty()) {
            return null;
        }
        
        double randomValue = prng.nextDouble() * total;
        double cumulative = 0.0;
        
        for (Entry<K, Double> entry : entrySet()) {
            cumulative += entry.getValue();
            if (randomValue <= cumulative) {
                return entry.getKey();
            }
        }
        
        return null;
    }
}
