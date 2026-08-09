package marlkit.pushtheblocktogether;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import algorithm.TDActorCritic;
import centralizedtraining.CentralizedCriticTrainingExecutionStrategy;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import learning.nn.StateValueCritic;
import learning.policies.NeuralNetworkCategoricalPolicy;
import marlkit.pushtheblock.agent.AgentPTB;

public class AgentPTBTogetherTDActorCriticCentralized extends AgentPTB {
	public AgentPTBTogetherTDActorCriticCentralized(){
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		
		NeuralNetworkCategoricalPolicy pol = new NeuralNetworkCategoricalPolicy(possibleActions, 
				new WrapperVectorObservationPositionsValues(false), 6, new int[] { 64 }, 10.0);
		StateValueCritic critic = new StateValueCritic(12, 64, new WrapperVectorObservationPositionsValues(false));
    	TDActorCritic algo = new TDActorCritic(pol, critic, 0.0001, 0.0001, 0.95);
    	setPolicy(pol);
    	setAlgorithm(algo);
	}
	
	/**
	 * Requests a team-specific role for shared-experience centralized training.
	 */
	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), CentralizedCriticTrainingExecutionStrategy.CENTRALIZED_CRITIC_AGENT_ROLE);
	}
}
