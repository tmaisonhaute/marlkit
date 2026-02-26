package marlkit.listenorgo;

import java.util.Objects;

import agent.action.Action;

/**
 * Action representing an agent's decision to listen for a noisy clue
 * about the correct door in the ListenOrGo environment.
 * <p>
 * Listening incurs a small negative reward but provides probabilistic
 * information about the correct direction.
 * </p>
 */
public class ActionListen implements Action {
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActionListen;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash("listen");
    }
    
    @Override
    public String toString() {
        return "Listen";
    }
}
