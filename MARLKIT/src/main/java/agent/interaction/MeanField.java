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
    private Map<MLKAgent, Observation> observationAgentsBuffer;

    public MeanField() {
        this.observationMeanField = new HashMap<>();
        this.observationAgentsBuffer = new HashMap<>();
        List<Action> possibleActions = getPossibleAction();

        if (possibleActions == null || possibleActions.isEmpty()) {
            throw new IllegalStateException("La liste d'actions possibles est vide ou nulle !");
        }

        setWrapper(new WrapperActionObservationOneHotEncoding(possibleActions));
    }


    public MeanField(AgentsGroup agentsGroup) { // besoin de revoir structure
        this.observationMeanField = new HashMap<>();
        this.observationAgentsBuffer = new HashMap<>();
        setAgentsGroup(agentsGroup);
        List<Action> possibleActions = getPossibleAction();

        if (possibleActions == null || possibleActions.isEmpty()) {
            throw new IllegalStateException("La liste d'actions possibles est vide ou nulle !");
        }

        setWrapper(new WrapperActionObservationOneHotEncoding(possibleActions));
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
        observationMeanField.clear();
        for (MLKAgent agent : agentsGroup.getAgents()) {
            observationMeanField.put(agent,new HashMap<>());
        }
    }

    @Override
    public Map<MLKAgent, Observation> getInteractionInformation(Map<MLKAgent, Observation> observationAgents) {
        Map<MLKAgent, Observation> interactionInfo = new HashMap<>();
        if (wrapper == null || observationAgents.isEmpty()) {
            return interactionInfo;
        }
        observationAgentsBuffer = observationAgents;

        for (MLKAgent agent : observationAgents.keySet()) {
            if (!observationMeanField.containsKey(agent)) {
                observationMeanField.put(agent, new HashMap<>());
            }

            Observation obs = observationAgents.get(agent);
            if (!observationMeanField.get(agent).containsKey(obs)) {
                interactionInfo.put(agent, new ObservationOneHotEncoding());
            } else {
                interactionInfo.put(agent, observationMeanField.get(agent).get(obs));
            }
        }
        return interactionInfo;
    }

    @Override
    public void update(Map<MLKAgent, Experience> experiences) {
        Map<MLKAgent, Action> actions= extractActions(experiences);
        for (MLKAgent ag : experiences.keySet()) {
            Observation obs = observationAgentsBuffer.get(ag);
            Observation interactionObs = extractAgentMeanField(ag,actions);
            observationMeanField.get(ag).put(obs, (ObservationOneHotEncoding) interactionObs) ;
                }
            }
    private Map<MLKAgent, Action> extractActions(Map<MLKAgent, Experience> experiences) {
        Map<MLKAgent, Action> actions = new HashMap<>();
        for (Map.Entry<MLKAgent, Experience> entry : experiences.entrySet()) {
            MLKAgent agent = entry.getKey();
            Experience experience = entry.getValue();
            actions.put(agent, experience.getAction());
        }
        return actions;
    }

    private ObservationOneHotEncoding extractAgentMeanField(MLKAgent agent ,Map<MLKAgent, Action> actions) {
        ObservationOneHotEncodings observationOneHotEncoding = new ObservationOneHotEncodings();
        for(Map.Entry<MLKAgent, Action> entry : actions.entrySet()) {
            MLKAgent ag = entry.getKey();
            Action action = entry.getValue();
            if (!(ag == agent)) {
                observationOneHotEncoding.add(wrapper.transform(action));
            }
        }
        return (ObservationOneHotEncoding) observationOneHotEncoding.getAverage();
    }
}

