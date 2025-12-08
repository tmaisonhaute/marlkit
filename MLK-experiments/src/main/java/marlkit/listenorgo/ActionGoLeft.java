package marlkit.listenorgo;

import java.util.Objects;

import agent.action.Action;

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
}
