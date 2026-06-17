package marlkit.pushtheblocktogether;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import algorithm.TDActorCritic;
import centralizedtraining.SchedulerCentralizedCritic;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import learning.nn.StateValueCritic;
import learning.policies.ActorNetwork;
import marlkit.pushtheblock.agent.AgentPTB;

public class AgentPTBTogetherTDActorCriticCentralized extends AgentPTB {
	public AgentPTBTogetherTDActorCriticCentralized(){
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		ActorNetwork pol = new ActorNetwork(6, 20, 
    			new WrapperVectorObservationPositionsValues(false), possibleActions);
		StateValueCritic critic = new StateValueCritic(12, 20, new WrapperVectorObservationPositionsValues(false));
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
		requestRole(getCommunity(), getModelGroup(), SchedulerCentralizedCritic.CENTRALIZED_CRITIC_AGENT_ROLE);
	}
}
