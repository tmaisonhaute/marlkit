package marlkit.pushtheblock.agent;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import algorithm.TDActorCritic;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import learning.nn.StateValueCritic;
import learning.policies.ActorNetwork;

public class AgentPTBTDActorCritic extends AgentPTB {
	public AgentPTBTDActorCritic(){
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		ActorNetwork pol = new ActorNetwork(4, 20, 
    			new WrapperVectorObservationPositionsValues(false), possibleActions);
		StateValueCritic critic = new StateValueCritic(4, 20, new WrapperVectorObservationPositionsValues(false));
    	TDActorCritic algo = new TDActorCritic(pol, critic, 0.0001, 0.0001, 0.95);
    	setPolicy(pol);
    	setAlgorithm(algo);
	}
}
