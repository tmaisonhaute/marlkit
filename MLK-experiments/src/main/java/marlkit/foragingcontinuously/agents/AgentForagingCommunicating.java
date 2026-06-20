package marlkit.foragingcontinuously.agents;

import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;

public class AgentForagingCommunicating extends AgentForaging implements MLKAgentCommunicating {
	protected CommunicationModel communicationModel;
	
	public AgentForagingCommunicating(CommunicationModel communicationModel) {
		super();
		setCommunicationModel(communicationModel);
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE);
	}
	
	@Override
	public CommunicationModel getCommunicationModel() {
		return communicationModel;
	}

	@Override
	public void setCommunicationModel(CommunicationModel communicationModel) {
		this.communicationModel = communicationModel;
		
	}


}
