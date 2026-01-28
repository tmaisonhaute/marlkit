package agent.interaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import agent.action.Action;
import environment.observation.Observation;
import learning.policy.PolicyInput;

/**
 * Stores predictions about an MLKAgent's future actions based on observations.
 */
public class Prediction {
    private Map<PolicyInput, Map<Action, Integer>> inputActionCounts;
    private Set<Action> observedActions;
    
    /**
     * Creates a new prediction model for the specified agent.
     *
     * @param agent the agent for which to make predictions
     */
    public Prediction() {
        this.inputActionCounts = new HashMap<>();
        this.observedActions = new HashSet<>();
    }
    
    /**
     * Records an observation-action pair for future predictions.
     * 
     * @param PolicyInput the input the agent received
     * @param action the action the agent took in response
     */
    public void recordObservationAction(PolicyInput input, Action action) {
    	if (!observedActions.contains(action)) {
    		observedActions.add(action);	
    	}
        
        Map<Action, Integer> actionCounts = inputActionCounts.getOrDefault(input, new HashMap<>());
        actionCounts.put(action, actionCounts.getOrDefault(action, 0) + 1);
        inputActionCounts.put(input, actionCounts);
    }
    
    /**
     * Predicts probabilities for each action the agent might take given an observation.
     * 
     * @param observation the observation to make predictions for
     * @return a map from actions to their predicted probabilities
     */
    public Map<Action, Double> predictActionProbabilities(Observation observation) {
        Map<Action, Double> probabilities = new HashMap<>();
        Map<Action, Integer> actionCounts = inputActionCounts.getOrDefault(observation, new HashMap<>());
        
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
