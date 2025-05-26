package agent.interaction.wrapper;

import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;

/**
 * Interface for converting agent action probabilities into observations.
 * Implementations should wrap probability distributions into appropriate observation formats.
 */
public interface WapperActionProbabilities {
    
    /**
     * Transforms a map of action probabilities for a specific agent into an observation.
     * 
     * @param agent the agent whose actions are being predicted
     * @param actionProbabilities the map of actions to their predicted probabilities
     * @return an observation representing the action probabilities
     */
    Observation transform(MLKAgent agent, Map<Action, Double> actionProbabilities);
}
