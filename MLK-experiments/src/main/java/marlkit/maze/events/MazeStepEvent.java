package marlkit.maze.events;

import reward.ReactionEventDefault;

public class MazeStepEvent extends ReactionEventDefault {

	public MazeStepEvent(double stepReward) {
		super(stepReward);
	}
}
