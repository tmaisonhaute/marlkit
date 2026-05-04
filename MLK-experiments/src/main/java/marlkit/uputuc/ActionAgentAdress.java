package marlkit.uputuc;

import agent.action.Action;
import madkit.kernel.AgentAddress;

public class ActionAgentAdress implements Action {
    private AgentAddress action;

    public ActionAgentAdress(AgentAddress action){
        this.action = action;
    }

    public AgentAddress getAction() {
        return action;
    }

	@Override
	public Action copy() {
		return new ActionAgentAdress(this.action);
	}
}
