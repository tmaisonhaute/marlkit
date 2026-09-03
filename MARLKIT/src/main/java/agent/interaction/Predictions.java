package agent.interaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import agent.MLKAgent;
import agent.action.Action;
import agent.interaction.wrapper.WapperActionProbabilities;
import environment.observation.Observation;

/**
 * Manages predictions about multiple agents' behaviors.
 * Used by an agent to track and predict actions of other agents.
 */
public class Predictions {
    
    // Map from agents to their corresponding prediction models
    private Map<MLKAgent, Prediction> agentPredictions;
    
    /**
     * Creates a new predictions manager for the specified owner agent.
     *
     * @param owner the agent that owns and will use these predictions
     */
    public Predictions() {
        this.agentPredictions = new HashMap<>();
    }
    
    /**
     * Ensures that we have a prediction model for the specified agent.
     * 
     * @param agent the agent to track
     */
    public void addAgent(MLKAgent agent) {
        if (!agentPredictions.containsKey(agent)) {
            agentPredictions.put(agent, new Prediction());
        }
    }
    
    /**
     * Records an observation-action pair for an agent.
     * 
     * @param agent the agent that performed the action
     * @param Observation the input the agent received
     * @param action the action the agent took in response
     */
    public void recordObservationAction(MLKAgent agent, Observation input, Action action) {
        addAgent(agent); // Ensure the agent exists in our predictions
        Prediction prediction = agentPredictions.get(agent);
        prediction.recordObservationAction(input, action);
    }
    
    /**
     * Predicts action probabilities for the specified agent given an observation.
     * 
     * @param agent the agent to predict
     * @param observation the observation to base the prediction on
     * @return a map from possible actions to their probabilities
     */
    public Map<Action, Double> predictActionProbabilities(MLKAgent agent, Observation observation) {
        if (!agentPredictions.containsKey(agent)) {
            return new HashMap<>(); // Return empty map if no predictions exist for this agent
        }
        return agentPredictions.get(agent).predictActionProbabilities(observation);
    }
    
    /**
     * For all tracked agents, computes their action probabilities based on a given observation
     * and transforms these probabilities into a combined observation using the provided wrapper.
     * 
     * @param observation the current observation to base predictions on
     * @param wrapper the wrapper used to convert action probabilities to observations
     * @return a combined observation containing predictions for all tracked agents
     */
    public Observation getPredictionsAsObservation(Observation observation, WapperActionProbabilities wrapper) {
        Observation combinedObservation = null;
        
        for (MLKAgent agent : getTrackedAgents()) {
            Map<Action, Double> actionProbabilities = predictActionProbabilities(agent, observation);
            Observation agentPredictionObs = wrapper.transform(agent, actionProbabilities);
            
            if (combinedObservation == null) {
                combinedObservation = agentPredictionObs;
            } else {
                combinedObservation = combinedObservation.add(agentPredictionObs);
            }
        }
        
        // If no agents are tracked, return null
        return combinedObservation;
    }
    
    /**
     * Gets all agents being tracked by this predictions manager.
     * 
     * @return the set of tracked agents
     */
    public Set<MLKAgent> getTrackedAgents() {
        return new HashSet<>(agentPredictions.keySet());
    }
    
    /**
     * Gets the prediction model for a specific agent.
     * 
     * @param agent the agent to get predictions for
     * @return the prediction model, or null if the agent is not being tracked
     */
    public Prediction getPrediction(MLKAgent agent) {
        return agentPredictions.get(agent);
    }
    
}
