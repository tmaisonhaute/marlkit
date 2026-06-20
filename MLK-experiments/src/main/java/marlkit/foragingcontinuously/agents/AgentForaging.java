package marlkit.foragingcontinuously.agents;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyPowerDecay;
import learning.policies.SoftmaxQPolicy;

public class AgentForaging extends AgentStandard {
	List<Action> possibleActions = new ArrayList<>(List.of(Move2D.left(), Move2D.right(), Move2D.up(), Move2D.down()));

	public AgentForaging() {
		super();
//		QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyPowerDecay(0.5));
		SoftmaxQPolicy policy = new SoftmaxQPolicy(possibleActions, 1.0, 5, new EpsilonGreedyPowerDecay(0.5));
    	QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.995);
    	
    	setPolicy(policy);
    	setAlgorithm(algorithm);
	}
}
