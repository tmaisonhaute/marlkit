package agent.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>A joint action is mutable. Actions can be added or removed from the
 * underlying ordered collection.</p>
 */
public class OrderedJointAction implements JointAction{

    /**
     * Ordered list of individual agent actions.
     */
    protected List<Action> agentActions;

    /**
     * Creates an empty joint action.
     */
    public OrderedJointAction() {
        agentActions = new ArrayList<>();
    }

    /**
     * Creates a joint action containing the specified actions in their iteration
     * order.
     *
     * <p>The provided list is copied. Subsequent structural modifications to the
     * original list therefore do not affect this joint action.</p>
     *
     * @param agentActions the ordered list of individual agent actions
     */
    public OrderedJointAction(List<Action> agentActions) {
        this.agentActions = new ArrayList<>(agentActions);
    }

    /**
     * Creates a joint action containing the specified actions in the given order.
     *
     * @param actions the individual agent actions
     * @return a joint action containing the specified actions
     */
    public static OrderedJointAction of(Action... actions) {
        return new OrderedJointAction(List.of(actions));
    }

    /**
     * Adds an action at the end of this joint action.
     *
     * @param action the action to add
     */
    public void addAction(Action action) {
        agentActions.add(action);
    }

    /**
     * Creates a new joint action with the specified action inserted before all
     * actions currently contained in this joint action.
     *
     * <p>The current joint action is not modified.</p>
     *
     * @param action the action to insert at the beginning
     * @return a new joint action beginning with the specified action
     */
    public OrderedJointAction withActionFirst(Action action) {
    	OrderedJointAction newJointAction = new OrderedJointAction();
        newJointAction.addAction(action);
        for (Action existingAction : agentActions) {
            newJointAction.addAction(existingAction);
        }
        return newJointAction;
    }

    /**
     * Creates a new joint action with the specified action inserted at the given
     * index.
     *
     * <p>The current joint action is not modified. If {@code index} is greater
     * than or equal to the current number of actions, the action is appended. If
     * {@code index} is negative, the action is not inserted.</p>
     *
     * @param action the action to insert
     * @param index the index at which the action must be inserted
     * @return a new joint action containing the inserted action when the index is
     *         non-negative
     */
    public OrderedJointAction withActionAtIndex(Action action, int index) {
    	OrderedJointAction newJointAction = new OrderedJointAction();
        for (int i = 0; i < agentActions.size(); i++) {
            if (i == index) {
                newJointAction.addAction(action);
            }
            newJointAction.addAction(agentActions.get(i));
        }
        if (index >= agentActions.size()) {
            newJointAction.addAction(action);
        }
        return newJointAction;
    }


    @Override
    public List<Action> getActions() {
        return agentActions;
    }

    /**
     * Returns the action stored at the specified agent index.
     *
     * @param agentIndex the index associated with the agent
     * @return the action stored at the specified index
     * @throws IndexOutOfBoundsException if the index is outside the valid range
     */
    public Action getActionAtIndex(int agentIndex) {
        return agentActions.get(agentIndex);
    }

    /**
     * Removes the action stored at the specified index.
     *
     * @param index the index of the action to remove
     * @throws IndexOutOfBoundsException if the index is outside the valid range
     */
    public void removeActionAtIndex(int index) {
        agentActions.remove(index);
    }


    @Override
    public int size() {
        return agentActions.size();
    }

    /**
     * Compares this joint action with another object.
     *
     * <p>Two joint actions are equal when they contain equal actions in the same
     * order.</p>
     *
     * @param obj the object to compare with this joint action
     * @return {@code true} if the objects represent the same ordered actions
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OrderedJointAction other)) {
            return false;
        }
        return Objects.equals(this.agentActions, other.agentActions);
    }

    /**
     * Returns a hash code based on the ordered actions contained in this joint
     * action.
     *
     * @return the hash code of this joint action
     */
    @Override
    public int hashCode() {
        return Objects.hash(agentActions);
    }

    /**
     * Creates a deep copy of this joint action.
     *
     * <p>Each contained action is copied by invoking {@link Action#copy()}.</p>
     *
     * @return an independent copy of this joint action and its actions
     */
    @Override
    public OrderedJointAction copy() {
    	OrderedJointAction copy = new OrderedJointAction();
        for (Action action : agentActions) {
            copy.addAction(action.copy());
        }
        return copy;
    }
}
