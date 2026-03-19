package marlkit.maze;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;

public abstract class AgentMazeEscape extends AgentStandard {
	public static final Action GO_LEFT = Action2DMove.left();
	public static final Action GO_RIGHT = Action2DMove.right();
	public static final Action GO_UP = Action2DMove.up();
	public static final Action GO_DOWN = Action2DMove.down();
}
