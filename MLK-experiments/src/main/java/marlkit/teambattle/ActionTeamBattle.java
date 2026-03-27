package marlkit.teambattle;

import java.util.Objects;

import agent.action.Action;
import util.Pair;

/**
 * Action type used by TeamBattle agents.
 *
 * <p>An action can either be a movement or an attack, both parameterized by a
 * cardinal direction vector.</p>
 */
public class ActionTeamBattle implements Action {

	/**
	 * High-level category of TeamBattle actions.
	 */
	public enum Kind {
		MOVE,
		ATTACK
	}

	private final Kind kind;
	private final Pair<Integer, Integer> direction;

	public ActionTeamBattle(Kind kind, Pair<Integer, Integer> direction) {
		this.kind = kind;
		this.direction = direction;
	}

	public Kind getKind() {
		return kind;
	}

	public Pair<Integer, Integer> getDirection() {
		return direction;
	}

	@Override
	public int hashCode() {
		return Objects.hash(direction, kind);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ActionTeamBattle other) {
			return kind == other.kind && direction.equals(other.direction);
		}
		return false;
	}

	@Override
	public String toString() {
		return kind + "(" + direction.getFirst() + "," + direction.getSecond() + ")";
	}

	public static ActionTeamBattle move(int dx, int dy) {
		return new ActionTeamBattle(Kind.MOVE, new Pair<>(dx, dy));
	}

	public static ActionTeamBattle attack(int dx, int dy) {
		return new ActionTeamBattle(Kind.ATTACK, new Pair<>(dx, dy));
	}
}
