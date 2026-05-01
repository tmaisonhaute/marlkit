package agent.action;

/**
 * Represents an action that an agent can take in the environment.
 */
public interface Action {

    /**
     * Implementations should override equals to ensure correct comparison of actions.
     */
    @Override
    boolean equals(Object obj);
    
    public Action copy();
}
