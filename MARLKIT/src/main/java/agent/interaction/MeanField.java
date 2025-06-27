package agent.interaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.observation.Observation;
import environment.observation.ObservationOneHotEncoding;
import environment.observation.ObservationOneHotEncodings;
import environment.observation.Wrapperactionobservation.WrapperActionObservation;
import environment.observation.Wrapperactionobservation.WrapperActionObservationOneHotEncoding;
import learning.Experience;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Implementation of Mean Field interaction model.
 * Each agent observes the average action distribution of other agents
 * and updates it incrementally based on observations.
 */
public class MeanField implements MLKInteraction {
    private Map<MLKAgent, Map<Observation,ObservationOneHotEncoding>> observationMeanField;
    private WrapperActionObservation wrapper;

    public MeanField(AgentsGroup agentsGroup) { // besoin de revoir structure
        setAgentsGroup(agentsGroup);
        setWrapper(new WrapperActionObservationOneHotEncoding(getPossibleAction()));
    }
    /**
     * get a list of possible agent action
     *
     * to delete// v0.1: get Von Neuman Movement (not flexible)
     */
    public List<Action> getPossibleAction(){
        return Action2DMove.getVonNeumannmove();
    }

    public void setWrapper(WrapperActionObservation wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void setAgentsGroup(AgentsGroup agentsGroup) {
        ObservationMeanField.clear();
        for (MLKAgent agent : agentsGroup.getAgents()) {
            observationMeanField.put(agent,new HashMap<>());
        }
    }

    @Override
    public Map<MLKAgent, Observation> getInteractionInformation(Map<MLKAgent, Observation> observationAgents) {
        Map<MLKAgent, Observation> interactionInfo = new HashMap<>();
        if (wrapper == null) {
            return interactionInfo;
        }

        for (MLKAgent agent : observationAgents.keySet()) {
            Observation obs = observationAgents.get(agent);
            if (!(observationMeanField.get(agent).containsKey(obs))){
                interactionInfo.put(agent, new ObservationOneHotEncoding());
            }
            if (obs != null) {
                interactionInfo.put(agent,observationMeanField.get(agent).get(obs));
            }
        }
        return interactionInfo;
    }


    @Override
    public void update(Map<MLKAgent, Experience> experiences) {
        // Pour chaque agent qui a une expérience
        for (MLKAgent ag : experiences.keySet()) {
            Experience experience = experiences.get(ag);
            Action actionPlayed = experience.getAction();

            // Mettre à jour la distribution moyenne pour tous les autres agents
            for (Map.Entry<MLKAgent, ActionDistribution> entryDist : ObservationBuffer.entrySet()) {
                MLKAgent observingAgent = entryDist.getKey();

                if (!observingAgent.equals(activeAgent)) {
                    ActionDistribution dist = entryDist.getValue();
                    dist.incrementActionCount(actionPlayed);
                }
            }
        }
    }

    public List<Action> getActions() {

    }

    /**
     * Pour accéder à la distribution moyenne d'actions que voit un agent
     * @param agent l'agent observant
     * @return la distribution moyenne des actions des autres agents
     */
    public Map<Action, Double> getMeanActionDistribution(MLKAgent agent) {
        ActionDistribution dist = ObservationBuffer.get(agent);
        if (dist != null) {
            return dist.toProbabilities();
        }
        return new HashMap<>();
    }

    /**
     * Classe interne qui stocke les comptes d'actions et calcule la distribution.
     */
    private static class ActionDistribution {
        private Map<Action, Integer> actionCounts;
        private int totalCounts;

        public ActionDistribution() {
            actionCounts = new HashMap<>();
            totalCounts = 0;
        }

        public void incrementActionCount(Action action) {
            actionCounts.put(action, actionCounts.getOrDefault(action, 0) + 1);
            totalCounts++;
        }

        public Map<Action, Double> toProbabilities() {
            Map<Action, Double> probs = new HashMap<>();
            if (totalCounts == 0) {
                // distribution uniforme par défaut si aucune donnée
                return probs;
            }
            for (Map.Entry<Action, Integer> entry : actionCounts.entrySet()) {
                probs.put(entry.getKey(), entry.getValue() / (double) totalCounts);
            }
            return probs;
        }
    }
}
