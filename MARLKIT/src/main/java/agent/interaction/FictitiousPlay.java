package agent.interaction;

import java.util.HashMap;
import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import agent.action.Action;
import agent.interaction.wrapper.WapperActionProbabilities;
import environment.observation.Observation;
import experience.Experience;

/**
 * Implementation of Fictitious Play interaction model using the Predictions class.
 * This model tracks other agents' behaviors and creates predictions based on past observations.
 */
public class FictitiousPlay implements MLKInteraction {
    private Map<MLKAgent, Predictions> agentsPredictions;
    private WapperActionProbabilities wrapper;
    
    public FictitiousPlay() {
        agentsPredictions = new HashMap<>();
    }
    
    /**
     * Set the wrapper used to transform action probabilities to observations
     * 
     * @param wrapper the wrapper implementation to use
     */
    public void setWrapper(WapperActionProbabilities wrapper) {
        this.wrapper = wrapper;
    }
    
    @Override
    public void setAgentsGroup(AgentsGroup agentsGroup) {
        // Initialize predictions for each agent
    	agentsPredictions.clear();
        for (MLKAgent agent : agentsGroup.getAgents()) {
            if (!agentsPredictions.containsKey(agent)) {
                agentsPredictions.put(agent, new Predictions());
            }
            
            // Add all other agents to each agent's predictions
            for (MLKAgent otherAgent : agentsGroup.getAgents()) {
                if (!agent.equals(otherAgent)) {
                    agentsPredictions.get(agent).addAgent(otherAgent);
                }
            }
        }
    }

    @Override
    public Map<MLKAgent, Observation> getInteractionInformation(Map<MLKAgent, Observation> observationAgents) {
        Map<MLKAgent, Observation> interactionInfo = new HashMap<>();
        
        if (wrapper == null) {
            // Can't transform without a wrapper
            return interactionInfo;
        }
        
        // For each agent, provide predictions about other agents
        for (MLKAgent agent : agentsPredictions.keySet()) {
            Observation agentObservation = observationAgents.get(agent);
            if (agentObservation != null) {
                // Get predictions for all other agents tracked by this agent
                Predictions predictions = agentsPredictions.get(agent);
                Observation predictionObservation = predictions.getPredictionsAsObservation(agentObservation, wrapper);
                
                if (predictionObservation != null) {
                    // Store the prediction observation for this agent
                    interactionInfo.put(agent, predictionObservation);
                }
            }
        }
        
        return interactionInfo;
    }

    @Override
    public void update(Map<MLKAgent, Experience> experiences) {
        // For each agent's experience
        for (Map.Entry<MLKAgent, Experience> entry : experiences.entrySet()) {
            MLKAgent activeAgent = entry.getKey();
            Experience experience = entry.getValue();
            
            // Update all other agents' predictions about this agent's behavior
            for (MLKAgent observingAgent : agentsPredictions.keySet()) {
                if (!observingAgent.equals(activeAgent)) {
                    Predictions predictions = agentsPredictions.get(observingAgent);
                    predictions.recordObservationAction(activeAgent, experience.getObservation(), experience.getAction());
                }
            }
        }
    }
    
    /**
     * Gets the predictions that an agent has about other agents.
     * 
     * @param agent the agent whose predictions to retrieve
     * @return the predictions object for the specified agent
     */
    public Predictions getAgentPredictions(MLKAgent agent) {
        return agentsPredictions.get(agent);
    }
    
    /**
     * Get predicted action probabilities for a specific agent from another agent's perspective.
     * 
     * @param observingAgent the agent making the prediction
     * @param targetAgent the agent being predicted
     * @param observation the observation to base the prediction on
     * @return a map of actions to their probabilities
     */
    public Map<Action, Double> getPredictedActionProbabilities(MLKAgent observingAgent, MLKAgent targetAgent, Observation observation) {
        Predictions predictions = agentsPredictions.get(observingAgent);
        if (predictions != null) {
            return predictions.predictActionProbabilities(targetAgent, observation);
        }
        return new HashMap<>();
    }
}
