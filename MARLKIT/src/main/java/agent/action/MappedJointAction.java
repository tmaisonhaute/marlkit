package agent.action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import agent.MLKAgent;

public class MappedJointAction implements JointAction {

    protected Map<MLKAgent, Action> agentActions;

    public MappedJointAction() {
        this.agentActions = new LinkedHashMap<>();
    }

    public MappedJointAction(Map<MLKAgent, Action> agentActions) {
        this.agentActions = new LinkedHashMap<>(agentActions);
    }

    public static MappedJointAction of(Map<MLKAgent, Action> agentActions) {
        return new MappedJointAction(agentActions);
    }

    /**
     * Adds or replaces the action associated with the given agent.
     *
     * @param agent the agent associated with the action
     * @param action the action to associate with the agent
     */
    public void addAction(MLKAgent agent, Action action) {
        agentActions.put(agent, action);
    }

    /**
     * Creates a new MappedJointAction with the given action associated
     * with the given agent.
     *
     * If the agent already has an action, it is replaced in the new instance.
     *
     * @param agent the agent associated with the action
     * @param action the action to associate with the agent
     * @return a new MappedJointAction instance containing the added action
     */
    public MappedJointAction withAction(MLKAgent agent, Action action) {
        MappedJointAction newJointAction = new MappedJointAction(agentActions);
        newJointAction.addAction(agent, action);
        return newJointAction;
    }

    /**
     * Returns the map of all agent-action associations.
     *
     * @return the map associating each agent with its action
     */
    public Map<MLKAgent, Action> getMappedActions() {
        return agentActions;
    }

    /**
     * Returns the action associated with a specific agent.
     *
     * @param agent the agent whose action is to be retrieved
     * @return the action associated with the specified agent, or null if absent
     */
    public Action getAction(MLKAgent agent) {
        return agentActions.get(agent);
    }

    /**
     * Checks whether an action is associated with the given agent.
     *
     * @param agent the agent to check
     * @return true if the agent has an associated action, false otherwise
     */
    public boolean containsAgent(MLKAgent agent) {
        return agentActions.containsKey(agent);
    }

    /**
     * Removes the action associated with the given agent.
     *
     * @param agent the agent whose action should be removed
     */
    public void removeAction(MLKAgent agent) {
        agentActions.remove(agent);
    }

    /**
     * Returns the number of agent-action associations.
     *
     * @return the number of actions in this joint action
     */
    public int size() {
        return agentActions.size();
    }

    /**
     * Returns whether this mapped joint action contains no action.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return agentActions.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MappedJointAction other)) {
            return false;
        }
        return Objects.equals(this.agentActions, other.agentActions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentActions);
    }

    @Override
    public MappedJointAction copy() {
        MappedJointAction copy = new MappedJointAction();

        for (Map.Entry<MLKAgent, Action> entry : agentActions.entrySet()) {
            MLKAgent agent = entry.getKey();
            Action action = entry.getValue();

            copy.addAction(agent, action.copy());
        }

        return copy;
    }

	@Override
	public List<Action> getActions() {
		return List.copyOf(agentActions.values());
	}
}
