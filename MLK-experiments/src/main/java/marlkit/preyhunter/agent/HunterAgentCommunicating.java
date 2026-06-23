package marlkit.preyhunter.agent;

import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;

/**
 * Hunter agent for the continuous PreyHunter environment.
 *
 * By default, hunters use a PPO categorical policy over a fixed set of
 * directional Move2DDouble actions.
 */
public class HunterAgentCommunicating extends HunterAgent implements MLKAgentCommunicating {
	protected CommunicationModel communicationModel;
	
	public HunterAgentCommunicating(int maxVisibleHunters, int maxVisiblePreys, int numberOfDirections, double speed, CommunicationModel communicationModel) {
		super(maxVisibleHunters, maxVisiblePreys, numberOfDirections, speed);
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