package marlkit.maze.events;

import rewardmodeling.ReactionEventDefault;

public class MazeStepEvent extends ReactionEventDefault {

	public MazeStepEvent(double stepReward) {
		super(stepReward);
	}
}
