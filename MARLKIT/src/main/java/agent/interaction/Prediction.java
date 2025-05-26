package agent.interaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import agent.action.Action;
import environment.observation.Observation;

/**
 * Stores predictions about an MLKAgent's future actions based on observations.
 */
public class Prediction {
    private Map<Observation, Map<Action, Integer>> observationActionCounts;
    private Set<Action> observedActions;
    
    /**
     * Creates a new prediction model for the specified agent.
     *
     * @param agent the agent for which to make predictions
     */
    public Prediction() {
        this.observationActionCounts = new HashMap<>();
        this.observedActions = new HashSet<>();
    }
    
    /**
     * Records an observation-action pair for future predictions.
     * 
     * @param observation the observation the agent received
     * @param action the action the agent took in response
     */
    public void recordObservationAction(Observation observation, Action action) {
    	if (!observedActions.contains(action)) {
    		observedActions.add(action);	
    	}
        
        Map<Action, Integer> actionCounts = observationActionCounts.getOrDefault(observation, new HashMap<>());
        actionCounts.put(action, actionCounts.getOrDefault(action, 0) + 1);
        observationActionCounts.put(observation, actionCounts);
    }
    
    /**
     * Predicts probabilities for each action the agent might take given an observation.
     * 
     * @param observation the observation to make predictions for
     * @return a map from actions to their predicted probabilities
     */
    public Map<Action, Double> predictActionProbabilities(Observation observation) {
        Map<Action, Double> probabilities = new HashMap<>();
        Map<Action, Integer> actionCounts = observationActionCounts.getOrDefault(observation, new HashMap<>());
        
        int totalCount = actionCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (totalCount == 0) {
            // If we haven't seen this observation before, return uniform distribution
            double uniformProbability = 1.0 / Math.max(1, observedActions.size());
            for (Action action : observedActions) {
                probabilities.put(action, uniformProbability);
            }
        } else {
            // Calculate probabilities based on observed frequencies
            for (Map.Entry<Action, Integer> entry : actionCounts.entrySet()) {
                probabilities.put(entry.getKey(), (double) entry.getValue() / totalCount);
            }
        }
        
        return probabilities;
    }
    
    /**
     * Gets the set of actions that have been observed for the agent.
     * 
     * @return the set of observed actions
     */
    public Set<Action> getObservedActions() {
        return new HashSet<>(observedActions);
    }
}
