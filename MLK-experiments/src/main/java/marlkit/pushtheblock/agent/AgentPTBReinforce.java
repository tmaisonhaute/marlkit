package marlkit.pushtheblock.agent;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import learning.algorithms.Reinforce;
import learning.policies.NeuralNetworkCategoricalPolicy;

public class AgentPTBReinforce extends AgentPTB {
	public AgentPTBReinforce(){
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		NeuralNetworkCategoricalPolicy pol = new NeuralNetworkCategoricalPolicy(possibleActions, 
				new WrapperVectorObservationPositionsValues(false), 4, new int[] { 20 }, 1.0);
		Reinforce algo = new Reinforce(pol, 0.01, 0.95);
    	setPolicy(pol);
    	setAlgorithm(algo);
	}
}
