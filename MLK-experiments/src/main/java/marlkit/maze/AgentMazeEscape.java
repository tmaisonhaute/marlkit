package marlkit.maze;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;

public abstract class AgentMazeEscape extends AgentStandard {
	public static final Action GO_LEFT = Move2D.left();
	public static final Action GO_RIGHT = Move2D.right();
	public static final Action GO_UP = Move2D.up();
	public static final Action GO_DOWN = Move2D.down();
}
