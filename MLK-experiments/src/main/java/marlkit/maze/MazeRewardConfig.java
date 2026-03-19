package marlkit.maze;

public record MazeRewardConfig(double stepReward, double holeReward, double exitReward) {

	public static final MazeRewardConfig DEFAULT = new MazeRewardConfig(-1.0, -10.0, 10.0);
	public static final MazeRewardConfig SOFT_HOLE = new MazeRewardConfig(-1.0, -5.0, 5.0);
}
