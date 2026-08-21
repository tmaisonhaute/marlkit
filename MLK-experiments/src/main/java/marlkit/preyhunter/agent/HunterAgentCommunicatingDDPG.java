package marlkit.preyhunter.agent;

import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import util.criteria.ReadOnlyCriterion;

public class HunterAgentCommunicatingDDPG extends HunterAgentDDPG implements MLKAgentCommunicating {
	protected CommunicationModel communicationModel;
	
	public HunterAgentCommunicatingDDPG(int maxVisibleHunters, int maxVisiblePreys, double speed, ReadOnlyCriterion evaluationCriterion,
			CommunicationModel communicationModel) {
		super(maxVisibleHunters, maxVisiblePreys, speed, evaluationCriterion);
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
