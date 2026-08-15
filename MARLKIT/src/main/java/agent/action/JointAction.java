package agent.action;

import java.util.List;

/**
 * Represents an ordered collection of individual agent actions.
 *
 * <p>The position of an action in the collection identifies the corresponding
 * agent according to the ordering convention used by the caller. This class
 * does not directly associate actions with agent instances.</p>
 *
 * 
 */
public interface JointAction extends Action {



    /**
     * Returns the ordered list of actions contained in this joint action.
     *
     * <p>The returned list is the mutable internal list. Modifications made to it
     * directly affect this joint action.</p>
     *
     * @return the mutable ordered list of individual actions
     */
    public List<Action> getActions();



    /**
     * Returns the number of individual actions contained in this joint action.
     *
     * @return the number of actions
     */
    public int size();


}