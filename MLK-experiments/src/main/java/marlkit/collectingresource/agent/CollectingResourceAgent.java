package marlkit.collectingresource.agent;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;
import marlkit.collectingresource.environment.ProductionUnit;
import marlkit.collectingresource.environment.UniteProductionSpatial;
import util.Position;

/**
 * Agent with a continuous 2D position for CollectingResource.
 */
public class CollectingResourceAgent extends AgentStandard {
	private Position position;
	List<Action> possibleActions;

	public CollectingResourceAgent(List<UniteProductionSpatial> unites) {
		super();
		
		possibleActions = new ArrayList<>();
		for (ProductionUnit unite : unites) {
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
