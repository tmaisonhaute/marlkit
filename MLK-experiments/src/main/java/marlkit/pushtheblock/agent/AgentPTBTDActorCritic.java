package marlkit.pushtheblock.agent;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import algorithm.TDActorCritic;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import learning.nn.StateValueCritic;
import learning.policies.NeuralNetworkCategoricalPolicy;

public class AgentPTBTDActorCritic extends AgentPTB {
	public AgentPTBTDActorCritic(){
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		NeuralNetworkCategoricalPolicy pol = new NeuralNetworkCategoricalPolicy(possibleActions, 
				new WrapperVectorObservationPositionsValues(false), 4, new int[] { 20 }, 1.0);
		StateValueCritic critic = new StateValueCritic(4, 20, new WrapperVectorObservationPositionsValues(false));
    	TDActorCritic algo = new TDActorCritic(pol, critic, 0.0001, 0.0001, 0.95);
    	setPolicy(pol);
    	setAlgorithm(algo);
	}
}