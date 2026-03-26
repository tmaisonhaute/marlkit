package marlkit.teambattle;

import java.util.Optional;

import environment.state.State;
import environment.state.State2DGridInt;
import util.criteria.Criterion;

public class TeamBattleTerminalCriterion implements Criterion {

	private boolean met;

	public TeamBattleTerminalCriterion() {
		met = false;
	}

	@Override
	public void update(Optional<State> state) {
		if (state.isEmpty()) {
			met = false;
			return;
		}
		State2DGridInt grid = (State2DGridInt) state.get();
		boolean teamAAlive = false;
		boolean teamBAlive = false;
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				if (grid.getValue(x, y) == 1) {
					teamAAlive = true;
				} else if (grid.getValue(x, y) == 2) {
					teamBAlive = true;
				}
			}
		}
		met = !(teamAAlive && teamBAlive);
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
