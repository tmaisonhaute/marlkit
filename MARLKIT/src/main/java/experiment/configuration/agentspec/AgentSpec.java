package experiment.configuration.agentspec;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;

/**
 * Minimal description of an agent required by experiment modules.
 * <p>
 * This interface intentionally contains only information that is broadly useful
 * across learning modules. More specific capabilities, such as vector input size
 * or input wrappers, should be exposed through optional sub-interfaces.
 * </p>
 */
public interface AgentSpec {

    /**
     * Returns the actions available to the agent.
     *
     * @return the list of possible actions
     */
    List<Action> getPossibleActions();
    
    /**
     * Returns the names of the roles that the agent should request.
     * @return the list of role names
     */
    default List<String> getOtherRoleNames() {
    	return new ArrayList<>();
    }
}