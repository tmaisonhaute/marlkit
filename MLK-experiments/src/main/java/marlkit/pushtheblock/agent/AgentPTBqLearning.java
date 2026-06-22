package marlkit.pushtheblock.agent;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2DInt;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;

public class AgentPTBqLearning extends AgentPTB {

	public AgentPTBqLearning(){
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		QValueBasedPolicy pol = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.001));
    	QLearning algo = new QLearning(pol, possibleActions, 0.2, 0.95);
    	setPolicy(pol);
    	setAlgorithm(algo);
	}

}
