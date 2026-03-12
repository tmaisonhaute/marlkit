package marlkit.pushtheblock;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;
import learning.algorithm.QLearning;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;

public class AgentPTB extends AgentStandard {
	static Action goLeft = Action2DMove.left(); 
	static Action goRight = Action2DMove.right();
	static Action goUp = Action2DMove.up(); 
	static Action goDown = Action2DMove.down();

	public AgentPTB(){
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		QValueBasedPolicy pol = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.001));
    	QLearning algo = new QLearning(pol, possibleActions, 0.2, 0.95);
    	setPolicy(pol);
    	setAlgorithm(algo);
	}

}
