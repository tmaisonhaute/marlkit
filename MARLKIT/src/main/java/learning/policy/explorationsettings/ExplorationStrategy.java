package learning.policy.explorationsettings;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import agent.action.Action;
import learning.policy.Policy;

/**
 * Interface for exploration strategies in reinforcement learning policies.
 * <p>
 * Defines how a policy should handle the exploration-exploitation trade-off.
 * Implementations can provide various strategies such as epsilon-greedy,
 * softmax, or UCB-based exploration.
 * </p>
 *
 * @see Policy
 */
public interface ExplorationStrategy {

	/**
	 * Determines whether to explore and which action to take if exploring.
	 * <p>
	 * Returns an empty Optional if the policy should exploit (use the greedy action),
	 * or an Optional containing the specific action to take if exploring.
	 * </p>
	 *
	 * @param possibleActions the list of available actions to choose from
	 * @return empty if no exploration (exploit), or the exploratory action to take
	 */
	public Optional<Action> getExploratoryAction(List<Action> possibleActions, RandomGenerator pnrg);
	
	public void update();
	
	/**
	 * Provides information about the exploration strategy for logging purposes.
	 * @return a string describing the current exploration strategy
	 */
	public String getLoggerInfo();
}
