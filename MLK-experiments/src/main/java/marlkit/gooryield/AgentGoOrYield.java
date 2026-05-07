package marlkit.gooryield;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.algorithm.QLearning;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;

public class AgentGoOrYield extends AgentStandard {
	Action yield = new ActionYield();
	Action go = new ActionGo();
	List<Action> possibleActions = new ArrayList<>(List.of(yield, go));

	public AgentGoOrYield() {
		super();
		initPolicyAndAlgorithm();
		
	}
	
	protected void initPolicyAndAlgorithm() {
		QValueBasedPolicy qPolicy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.001));
		QLearning qLearning = new QLearning(qPolicy, possibleActions, 0.2, 0.95);
		setPolicy(qPolicy);
		setAlgorithm(qLearning);
	}
}
