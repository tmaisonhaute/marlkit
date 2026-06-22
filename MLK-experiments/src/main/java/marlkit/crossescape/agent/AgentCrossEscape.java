package marlkit.crossescape.agent;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2DInt;

/**
 * Base agent type for the CrossEscape experiment.
 *
 * <p>This class exposes the four cardinal movement actions shared by all
 * CrossEscape agent implementations.</p>
 */
public abstract class AgentCrossEscape extends AgentStandard {
	public static final Action GO_LEFT = Move2DInt.left();
	public static final Action GO_RIGHT = Move2DInt.right();
	public static final Action GO_UP = Move2DInt.up();
	public static final Action GO_DOWN = Move2DInt.down();
}
