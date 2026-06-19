package agent.communication;

import agent.AgentStandard;
import communication.CommunicationModel;
import communication.NoCommunication;
import learning.Algorithm;
import learning.Policy;

public class AgentStandardCommunicating extends AgentStandard implements MLKAgentCommunicating {
	
	protected CommunicationModel communicationModule;
	
	public AgentStandardCommunicating(Policy policy, Algorithm algorithm, CommunicationModel communicationModule) {
		super(policy, algorithm);
		this.communicationModule = communicationModule;
	}
	
	public AgentStandardCommunicating(Policy policy, Algorithm algorithm) {
		this(policy, algorithm, new NoCommunication());
	}
	
	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE);
	}
	
	@Override
	public CommunicationModel getCommunicationModel() {
		return communicationModule;
	}
	
	@Override
	public void setCommunicationModel(CommunicationModel communicationModule) {
		this.communicationModule = communicationModule;
	}

}

