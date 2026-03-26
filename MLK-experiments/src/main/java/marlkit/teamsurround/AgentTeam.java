package marlkit.teamsurround;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;

public abstract class AgentTeam extends AgentStandard {

	public static final Action GO_LEFT = Action2DMove.left();
	public static final Action GO_RIGHT = Action2DMove.right();
	public static final Action GO_UP = Action2DMove.up();
	public static final Action GO_DOWN = Action2DMove.down();

	public static List<Action> defaultActions() {
		return new ArrayList<>(List.of(GO_LEFT, GO_RIGHT, GO_UP, GO_DOWN));
	}
}
