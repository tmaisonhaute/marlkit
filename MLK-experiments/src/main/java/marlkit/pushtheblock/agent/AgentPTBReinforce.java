package marlkit.pushtheblock.agent;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import learning.algorithms.Reinforce;
import learning.policies.ActorNetwork;

public class AgentPTBReinforce extends AgentPTB {
	public AgentPTBReinforce(){
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		ActorNetwork pol = new ActorNetwork(4, 20, 
    			new WrapperVectorObservationPositionsValues(false), possibleActions);
		Reinforce algo = new Reinforce(pol, 0.01, 0.95);
    	setPolicy(pol);
    	setAlgorithm(algo);
	}
}
