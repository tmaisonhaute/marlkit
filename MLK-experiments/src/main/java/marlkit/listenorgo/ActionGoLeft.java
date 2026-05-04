package marlkit.listenorgo;

import java.util.Objects;

import agent.action.Action;

/**
 * Action representing an agent's choice to move to the left door
 * in the ListenOrGo environment.
 */
public class ActionGoLeft implements Action {
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActionGoLeft;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash("left");
    }
    
    @Override
    public String toString() {
        return "GoLeft";
    }

	@Override
	public Action copy() {
		
		return new ActionGoLeft();
	}
}
