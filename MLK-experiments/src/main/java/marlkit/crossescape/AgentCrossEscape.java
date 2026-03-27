package marlkit.crossescape;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;

/**
 * Base agent type for the CrossEscape experiment.
 *
 * <p>This class exposes the four cardinal movement actions shared by all
 * CrossEscape agent implementations.</p>
 */
public abstract class AgentCrossEscape extends AgentStandard {
	public static final Action GO_LEFT = Action2DMove.left();
	public static final Action GO_RIGHT = Action2DMove.right();
	public static final Action GO_UP = Action2DMove.up();
	public static final Action GO_DOWN = Action2DMove.down();
}
