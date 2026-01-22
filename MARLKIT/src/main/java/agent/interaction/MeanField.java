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
import environment.observation.wrapperactionobservation.WrapperActionObservation;
import environment.observation.wrapperactionobservation.WrapperActionObservationOneHotEncoding;
import learning.Experience;

/**
 * Implementation of the Mean Field interaction model for multi-agent learning.
 *
 * In this model, each agent perceives the average (mean field) of the actions
 * taken by the other agents in the environment. This information is encoded
 * and stored per observed state and updated incrementally with new experiences.
 */
public class MeanField implements MLKInteraction {

    private Map<MLKAgent, Map<Observation, ObservationOneHotEncoding>> observationMeanField;
    private WrapperActionObservation wrapper;
    private Map<MLKAgent, Observation> observationAgentsBuffer;

    /**
     * Default constructor. Initializes internal data structures and sets up
     * the wrapper using Von Neumann movement actions.
     */
    public MeanField() {
        this.observationMeanField = new HashMap<>();
        this.observationAgentsBuffer = new HashMap<>();
        List<Action> possibleActions = getPossibleActions();

        if (possibleActions == null || possibleActions.isEmpty()) {
            throw new IllegalStateException("The list of possible actions is null or empty!");
        }

        setWrapper(new WrapperActionObservationOneHotEncoding(possibleActions));
    }

    /**
     * Constructor that takes a group of agents to initialize.
     *
     * @param agentsGroup the group of agents in the environment
     */
    public MeanField(AgentsGroup agentsGroup) {
        this.observationMeanField = new HashMap<>();
        this.observationAgentsBuffer = new HashMap<>();
        setAgentsGroup(agentsGroup);

        List<Action> possibleActions = getPossibleActions();

        if (possibleActions == null || possibleActions.isEmpty()) {
            throw new IllegalStateException("The list of possible actions is null or empty!");
        }

        setWrapper(new WrapperActionObservationOneHotEncoding(possibleActions));
    }

    /**
     * Returns the list of possible actions for agents.
     *
     * @return a list of allowed actions based on Von Neumann movement
     */
    public List<Action> getPossibleActions() {
        return Action2DMove.getVonNeumannmove();
    }

    /**
     * Sets the wrapper for action-observation transformation.
     *
     * @param wrapper the wrapper to be used for encoding actions
     */
    public void setWrapper(WrapperActionObservation wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Initializes the mean field observation storage for each agent in the group.
     *
     * @param agentsGroup the group of agents to register
     */
    @Override
    public void setAgentsGroup(AgentsGroup agentsGroup) {
        observationMeanField.clear();
        for (MLKAgent agent : agentsGroup.getAgents()) {
            observationMeanField.put(agent, new HashMap<>());
        }
    }

    /**
     * Returns the mean field interaction information for each agent based on the current observations.
     *
     * @param observationAgents a map of agents and their current observations
     * @return a map of agents and the corresponding mean field observations
     */
    @Override
    public Map<MLKAgent, Observation> getInteractionInformation(Map<MLKAgent, Observation> observationAgents) {
        Map<MLKAgent, Observation> interactionInfo = new HashMap<>();

        if (wrapper == null || observationAgents.isEmpty()) {
            return interactionInfo;
        }

        observationAgentsBuffer = observationAgents;

        for (MLKAgent agent : observationAgents.keySet()) {
            observationMeanField.putIfAbsent(agent, new HashMap<>());
            Observation obs = observationAgents.get(agent);

            if (!observationMeanField.get(agent).containsKey(obs)) {
                interactionInfo.put(agent, new ObservationOneHotEncoding());
            } else {
                interactionInfo.put(agent, observationMeanField.get(agent).get(obs));
            }
        }

        return interactionInfo;
    }

    /**
     * Updates the mean field representation based on the experiences of agents.
     *
     * @param experiences a map of agents and their respective experiences
     */
    @Override
    public void update(Map<MLKAgent, Experience> experiences) {
        Map<MLKAgent, Action> actions = extractActions(experiences);

        for (MLKAgent agent : experiences.keySet()) {
            Observation obs = observationAgentsBuffer.get(agent);
            ObservationOneHotEncoding meanFieldObs = extractAgentMeanField(agent, actions);
            observationMeanField.get(agent).put(obs, meanFieldObs);
        }
    }

    /**
     * Extracts the action taken by each agent from their experience.
     *
     * @param experiences a map of agents and their experiences
     * @return a map of agents and their respective actions
     */
    private Map<MLKAgent, Action> extractActions(Map<MLKAgent, Experience> experiences) {
        Map<MLKAgent, Action> actions = new HashMap<>();
        for (Map.Entry<MLKAgent, Experience> entry : experiences.entrySet()) {
            actions.put(entry.getKey(), entry.getValue().getAction());
        }
        return actions;
    }

    /**
     * Computes the mean field observation (average action distribution)
     * for a specific agent, excluding its own action.
     *
     * @param agent the agent for whom the mean field is computed
     * @param actions a map of agents and their most recent actions
     * @return the one-hot encoded average observation of others' actions
     */
    private ObservationOneHotEncoding extractAgentMeanField(MLKAgent agent, Map<MLKAgent, Action> actions) {
        ObservationOneHotEncodings encodings = new ObservationOneHotEncodings();

        for (Map.Entry<MLKAgent, Action> entry : actions.entrySet()) {
            MLKAgent other = entry.getKey();
            if (!other.equals(agent)) {
                encodings.add(wrapper.transform(entry.getValue()));
            }
        }

        return (ObservationOneHotEncoding) encodings.getAverage();
    }
}
