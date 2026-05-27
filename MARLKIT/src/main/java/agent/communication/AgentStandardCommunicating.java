package agent.communication;

import agent.AgentStandard;
import communication.CommunicationModule;
import communication.NoCommunication;
import learning.algorithm.Algorithm;
import learning.policy.Policy;

public class AgentStandardCommunicating extends AgentStandard implements MLKAgentCommunicating {
	
	protected CommunicationModule communicationModule;
	
	public AgentStandardCommunicating(Policy policy, Algorithm algorithm, CommunicationModule communicationModule) {
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
	public CommunicationModule getCommunicationModule() {
		return communicationModule;
	}
	
	@Override
	public void setCommunicationModule(CommunicationModule communicationModule) {
		this.communicationModule = communicationModule;
	}

}

