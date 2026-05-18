package marlkit.maze.events;

import reward.ReactionEventDefault;

public class MazeExitEvent extends ReactionEventDefault {

	public MazeExitEvent(double exitReward) {
		super(exitReward);
	}
}
