package marlkit.foragingcontinuously.agents;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;
import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyPowerDecay;
import learning.policies.SoftmaxQPolicy;

public class AgentForagingCommunicating extends AgentStandard implements MLKAgentCommunicating {
	protected CommunicationModel communicationModel;
	List<Action> possibleActions = new ArrayList<>(List.of(Move2D.left(), Move2D.right(), Move2D.up(), Move2D.down()));
	
	public AgentForagingCommunicating(CommunicationModel communicationModel) {
		super();
		setCommunicationModel(communicationModel);
		
//		QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyPowerDecay(0.5));
		SoftmaxQPolicy policy = new SoftmaxQPolicy(possibleActions, 1.0, 5, new EpsilonGreedyPowerDecay(0.5));
    	QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.995);
    	
    	setPolicy(policy);
    	setAlgorithm(algorithm);
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
