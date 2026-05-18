package marlkit.trade2d;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.algorithm.QLearning;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;
import marlkit.trade.ActionRequestResource;
import marlkit.trade.UniteProduction;
import util.Position;

/**
 * Agent with a continuous 2D position for Trade2D.
 */
public class Trade2DAgent extends AgentStandard {
	private Position position;
	List<Action> possibleActions;

	public Trade2DAgent(List<UniteProductionSpatial> unites) {
		super();
		
		possibleActions = new ArrayList<>();
		for (UniteProduction unite : unites) {
			possibleActions.add(new ActionRequestResource(unite));
		}
		QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.003));
		QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.95);
		
		setPolicy(policy);
		setAlgorithm(algorithm);
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
}
