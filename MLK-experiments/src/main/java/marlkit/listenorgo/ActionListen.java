package marlkit.listenorgo;

import java.util.Objects;

import agent.action.Action;

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
