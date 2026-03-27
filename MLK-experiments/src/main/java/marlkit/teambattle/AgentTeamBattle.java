package marlkit.teambattle;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;

/**
 * Base agent type for TeamBattle.
 *
 * <p>This class defines the default discrete action space used by TeamBattle
 * agents: four moves and four directional attacks.</p>
 */
public abstract class AgentTeamBattle extends AgentStandard {

	public static final Action MOVE_LEFT = ActionTeamBattle.move(-1, 0);
	public static final Action MOVE_RIGHT = ActionTeamBattle.move(1, 0);
	public static final Action MOVE_UP = ActionTeamBattle.move(0, 1);
	public static final Action MOVE_DOWN = ActionTeamBattle.move(0, -1);

	public static final Action ATTACK_LEFT = ActionTeamBattle.attack(-1, 0);
	public static final Action ATTACK_RIGHT = ActionTeamBattle.attack(1, 0);
	public static final Action ATTACK_UP = ActionTeamBattle.attack(0, 1);
	public static final Action ATTACK_DOWN = ActionTeamBattle.attack(0, -1);

	public static List<Action> defaultActions() {
		return new ArrayList<>(List.of(
				MOVE_LEFT,
				MOVE_RIGHT,
				MOVE_UP,
				MOVE_DOWN,
				ATTACK_LEFT,
				ATTACK_RIGHT,
				ATTACK_UP,
				ATTACK_DOWN));
	}
}
