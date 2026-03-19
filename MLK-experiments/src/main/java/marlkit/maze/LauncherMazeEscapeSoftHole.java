package marlkit.maze;

import madkit.simulation.EngineAgents;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerMazeEscape.class, model = MLKModel.class, viewers = {
		ViewerMazeEscape.class })
public class LauncherMazeEscapeSoftHole extends LauncherMazeEscapeBase {

	@Override
	protected MazeRewardConfig getRewardConfig() {
		return MazeRewardConfig.SOFT_HOLE;
	}

	public static void main(String[] args) {
		runMazeSimulation();
	}
}
