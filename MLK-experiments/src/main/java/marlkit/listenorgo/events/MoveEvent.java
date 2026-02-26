package marlkit.listenorgo.events;

import environment.reward.Reward;
import environment.reward.RewardStandard;
import rewardmodeling.ReactionEvent;

/**
 * Reaction event produced when an agent commits to a direction
 * (either {@link marlkit.listenorgo.ActionGoLeft} or {@link marlkit.listenorgo.ActionGoRight}).
 * <p>
 * A large positive reward is granted when the chosen direction matches the
 * correct one; a large negative reward is granted otherwise.
 * </p>
 */
public class MoveEvent extends ReactionEvent {
	private static final double REWARD_CORRECT = 10.0;
	private static final double REWARD_INCORRECT = -10.0;
	
	private boolean correctDirection;

	/**
	 * Creates a new MoveEvent.
	 *
	 * @param correctDirection {@code true} if the agent chose the correct door,
	 *                         {@code false} otherwise.
	 */
	public MoveEvent(boolean correctDirection) {
		this.correctDirection = correctDirection;
	}

	/**
	 * Returns the reward associated with this move.
	 *
	 * @return {@link RewardStandard} with value {@value #REWARD_CORRECT} if the direction
	 *         was correct, or {@value #REWARD_INCORRECT} otherwise.
	 */
	@Override
	public Reward toReward() {
		if (correctDirection) {
			return new RewardStandard(REWARD_CORRECT);
		} else {
			return new RewardStandard(REWARD_INCORRECT);
		}
	}

}
