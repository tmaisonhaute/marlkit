package agent.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JointAction implements Action {
	protected List<Action> agentActions;
	
	public JointAction() {
		agentActions = new ArrayList<>();
	}
	

	public JointAction(List<Action> agentActions) {
        this.agentActions = List.copyOf(agentActions);
    }

    public static JointAction of(Action... actions) {
        return new JointAction(List.of(actions));
    }


    /**
     * Adds an action to the joint action.
     * @param action the action to be added for the agent
     */
    public void addAction(Action action) {
    	agentActions.add(action);
    }

    
    /**
     * Creates a new JointAction with the given action added at the beginning of the existing joint action.
     * @param action the action to be added at the beginning of the joint action
     * @return a new JointAction instance with the specified action followed by the existing actions in the joint action
     */
	public JointAction withActionFirst(Action action) {
		JointAction newJointAction = new JointAction();
		newJointAction.addAction(action);
		for (Action existingAction : agentActions) {
			newJointAction.addAction(existingAction);
		}
		return newJointAction;
	}
    
	/**
	 * Creates a new JointAction with the given action inserted at the specified index of the existing joint action.
	 * 
	 * <p>
	 * Note:
	 * If the specified index is lower than 0, no action will be added.
	 * If the specified index is greater than the current size of the joint action, the new action will be added at the end.
	 * </p>
	 * 
	 * @param action the action to be added at the specified index of the joint action
	 * @param index the index at which the action should be inserted in the joint action
	 * @return a new JointAction instance with the specified action inserted at the given index, followed by the existing actions in the joint action
	 */
	public JointAction withActionAtIndex(Action action, int index) {

		JointAction newJointAction = new JointAction();
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

    /**
     * Returns the list of all actions in the joint action.
     */
    public List<Action> getActions(){
    	return agentActions;
    }
    
    /**
     * Returns the action for a specific agent index in the joint action.
     * @param agentIndex the index of the agent whose action is to be retrieved
     * @return the action corresponding to the specified agent index
     */
    public Action getActionAtIndex(int agentIndex) {
        return agentActions.get(agentIndex);
    }
    
    /**
     * Remove the action at the specified index from the joint action.
     * @param index the index of the action to be removed from the joint action
     */
	public void removeActionAtIndex(int index) {
		agentActions.remove(index);
	}

    
    /**
     * Returns the number of actions in the joint action.
     */
	public int size() {
		return agentActions.size();
	}
	

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JointAction other)) {
            return false;
        }
        return Objects.equals(this.agentActions, other.agentActions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentActions);
    }
    
    @Override
	public Action copy() {
		JointAction copy = new JointAction();
		for (Action action : agentActions) {
			copy.addAction(action.copy());
		}
		return copy;
	}

}
