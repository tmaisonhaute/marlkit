
package util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.random.RandomGenerator;

/**
 * Utility for selecting random elements based on weighted probabilities.
 */
public class MapSelector<K> {

    /**
     * Selects a random key from the map with probability proportional to its value.
     *
     * @param <K> the type of keys in the map
     * @param probabilitiesMap map of elements to their selection weights
     * @param random random number generator
     * @return randomly selected key based on weights
     * @throws IllegalArgumentException if the map is empty or contains negative weights
     */
    public static <K> K getRandomItem(Map<K, Double> probabilitiesMap, RandomGenerator random) {
        if (probabilitiesMap.isEmpty()) {
            throw new IllegalArgumentException("Probability map cannot be empty");
        }

        // Calculate total weight
        double totalWeight = 0.0;
        for (Double weight : probabilitiesMap.values()) {
            if (weight < 0) {
                throw new IllegalArgumentException("Weights cannot be negative");
            }
            totalWeight += weight;
        }

        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Sum of weights must be positive");
        }

        // Generate random value and find corresponding element
        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;

        for (Entry<K, Double> entry : probabilitiesMap.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }

        // Should never reach here if weights are positive and non-zero
        return probabilitiesMap.keySet().iterator().next();
    }
}
