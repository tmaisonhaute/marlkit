package experiment.configuration.agentspec;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;

public class DefaultAgentSpec implements AgentSpec {
	protected List<Action> possibleActions;
	protected List<String> otherRoleNames;
	
	public DefaultAgentSpec(List<Action> possibleActions, List<String> otherRoleNames) {
	    this.possibleActions = List.copyOf(possibleActions);
	    this.otherRoleNames = List.copyOf(otherRoleNames);
	}
	
	public DefaultAgentSpec(List<Action> possibleActions) {
	    this(possibleActions, new ArrayList<>());
	}

	public void setPossibleActions(List<Action> actions) {
	    this.possibleActions = List.copyOf(actions);
	}

	@Override
	public List<Action> getPossibleActions() {
		return possibleActions;
	}
	
	@Override 
	public List<String> getOtherRoleNames() {
		return otherRoleNames;
	}


}
