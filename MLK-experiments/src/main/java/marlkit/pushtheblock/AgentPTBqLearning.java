package marlkit.pushtheblock;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;
import learning.algorithm.QLearning;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;

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
