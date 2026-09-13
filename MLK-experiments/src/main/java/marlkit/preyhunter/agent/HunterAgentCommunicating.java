package marlkit.preyhunter.agent;

import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import communication.NoCommunication;
import learning.Algorithm;
import learning.Policy;

public class HunterAgentCommunicating extends HunterAgent implements MLKAgentCommunicating {

	protected CommunicationModel communicationModel;
	
	public HunterAgentCommunicating(Policy policy, Algorithm algorithm) {
		this(policy, algorithm, new NoCommunication());
	}
	
	public HunterAgentCommunicating(Policy policy, Algorithm algorithm, CommunicationModel communicationModel) {
		super(policy, algorithm);
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
