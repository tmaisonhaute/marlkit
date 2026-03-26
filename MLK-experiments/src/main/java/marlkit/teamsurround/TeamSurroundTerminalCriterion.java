package marlkit.teamsurround;

import java.util.Optional;

import environment.state.State;
import environment.state.State2DGridInt;
import util.criteria.Criterion;

public class TeamSurroundTerminalCriterion implements Criterion {

	private boolean met;

	public TeamSurroundTerminalCriterion() {
		met = false;
	}

	@Override
	public void update(Optional<State> state) {
		if (state.isEmpty()) {
			met = false;
			return;
		}
		State2DGridInt grid = (State2DGridInt) state.get();
		boolean team1Alive = false;
		boolean team2Alive = false;
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				if (grid.getValue(x, y) == 1) {
					team1Alive = true;
				} else if (grid.getValue(x, y) == 2) {
					team2Alive = true;
				}
			}
		}
		met = !(team1Alive && team2Alive);
	}

	@Override
	public void reset() {
		met = false;
	}

	@Override
	public boolean isMet() {
		return met;
	}
}
