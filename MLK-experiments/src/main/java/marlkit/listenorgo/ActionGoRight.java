package marlkit.listenorgo;

import java.util.Objects;

import agent.action.Action;

public class ActionGoRight implements Action {
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActionGoRight;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash("right");
    }
    
    @Override
    public String toString() {
        return "GoRight";
    }
}
