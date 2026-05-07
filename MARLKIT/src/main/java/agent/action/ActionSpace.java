package agent.action;

import java.util.ArrayList;
import java.util.List;

/**
 * ActionSpace represents a set of possible actions that an agent or a group of agents can take. 
 */
public class ActionSpace {
    private List<Action> actions;

	public ActionSpace() {
		this.actions = new ArrayList<>();
	}
    
	public ActionSpace(List<Action> actions) {
		this.actions = new ArrayList<>(actions);
	}
    
    public static JointAction of(Action... actions) {
        return new JointAction(List.of(actions));
    }

    public List<Action> getActions() {
        return actions;
    }
    
    public void addAction(Action action) {
    	this.actions.add(action);
    }
    
	public void removeAtIndex(int index) {
		this.actions.remove(index);
	}
}
