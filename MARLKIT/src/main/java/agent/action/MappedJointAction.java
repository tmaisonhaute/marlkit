package agent.action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import agent.MLKAgent;

/**
 * Represents a joint action where each agent is associated with a specific action.
 * 
 * <p>
 * This class uses a mapping to associate each agent with its corresponding action.
 * </p>
 * 
 * <p>
 * The order of agents is preserved based on the order of insertion into the map.
 * </p>
 * 
 * @see JointAction
 * 
 */
public class MappedJointAction implements JointAction {

    protected Map<MLKAgent, Action> agentActions;

    /**
     * Creates a new MappedJointAction with an empty mapping of agents to actions.
     */
    public MappedJointAction() {
        this.agentActions = new LinkedHashMap<>();
    }

    /**
     * Creates a new MappedJointAction with the specified mapping of agents to actions.
     * @param agentActions the mapping of agents to their corresponding actions
     */
    public MappedJointAction(Map<MLKAgent, Action> agentActions) {
        Objects.requireNonNull(agentActions, "agentActions");

        this.agentActions = new LinkedHashMap<>();

        for (Map.Entry<MLKAgent, Action> entry : agentActions.entrySet()) {
            addAction(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Creates a new MappedJointAction with the specified mapping of agents to actions.
     * @param agentActions the mapping of agents to their corresponding actions
     * @return a new MappedJointAction instance containing the specified agent-action associations
     */
    public static MappedJointAction of(Map<MLKAgent, Action> agentActions) {
        return new MappedJointAction(agentActions);
    }

    /**
     * Adds or replaces the action associated with the given agent.
     *
     * @param agent the agent associated with the action
     * @param action the action to associate with the agent
     * @throws NullPointerException if either the agent or action is null
     */
    public void addAction(MLKAgent agent, Action action) {
    	Objects.requireNonNull(agent, "agent cannot be null during addAction");
    	Objects.requireNonNull(action, "action cannot be null during addAction");
    	
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
     * Returns a copy of the mappings between agents and actions.
     *
     * <p>Modifying the returned map does not modify this joint action. The action
     * objects themselves are not copied.</p>
     *
     * @return a copy of the agent action mappings preserving their insertion order
     */
    public Map<MLKAgent, Action> getMappedActions() {
        return new LinkedHashMap<>(agentActions);
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
