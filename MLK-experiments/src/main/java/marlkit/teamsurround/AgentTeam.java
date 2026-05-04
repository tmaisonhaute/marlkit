package marlkit.teamsurround;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;

/**
 * Base agent type for the TeamSurround experiment.
 *
 * <p>Agents can only perform cardinal movement actions.</p>
 */
public abstract class AgentTeam extends AgentStandard {

	public static final Action GO_LEFT = Move2D.left();
	public static final Action GO_RIGHT = Move2D.right();
	public static final Action GO_UP = Move2D.up();
	public static final Action GO_DOWN = Move2D.down();

	public static List<Action> defaultActions() {
		return new ArrayList<>(List.of(GO_LEFT, GO_RIGHT, GO_UP, GO_DOWN));
	}
}
