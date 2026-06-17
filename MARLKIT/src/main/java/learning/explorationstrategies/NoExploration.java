package learning.explorationstrategies;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import agent.action.Action;
import learning.ExplorationStrategy;

public class NoExploration implements ExplorationStrategy {

	@Override
	public Optional<Action> getExploratoryAction(List<Action> possibleActions, RandomGenerator pnrg) {
		return Optional.empty();
	}

	@Override
	public void update() {
		// No update needed for NoExploration
	}

	@Override
	public String getLoggerInfo() {
		return "NoExploration";
	}

}
