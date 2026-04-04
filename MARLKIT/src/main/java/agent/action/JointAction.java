package agent.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JointAction implements Action {
	public final List<Action> agentActions;
	
	public JointAction() {
		agentActions = new ArrayList<>();
	}

    /**
     * Adds an action to the joint action.
     * @param action the action to be added for the agent
     */
    public void addActions(Action action) {
    	agentActions.add(action);
    }


    /**
     * Returns the list of all actions in the joint action.
     */
    public List<Action> getActions(){
    	return agentActions;
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

}
