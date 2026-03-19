package marlkit.maze.events;

import rewardmodeling.ReactionEventDefault;

public class MazeExitEvent extends ReactionEventDefault {

	public MazeExitEvent(double exitReward) {
		super(exitReward);
	}
}
