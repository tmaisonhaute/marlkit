package marlkit.maze;

import java.util.Optional;

import environment.state.State;
import environment.state.State2DGridInt;
import util.Pair;
import util.criteria.Criterion;

public class MazeTerminalCriterion implements Criterion {
	private boolean met;

	public MazeTerminalCriterion() {
		met = false;
	}

	@Override
	public void update(Optional<State> state) {
		if (state.isEmpty()) {
			met = false;
			return;
		}
		State2DGridInt grid = (State2DGridInt) state.get();
		for (Pair<Integer, Integer> position : grid.getAgentsPositions().values()) {
			MazeCellType type = MazeCellType.fromCode(grid.getValue(position));
			if (type == MazeCellType.HOLE || type == MazeCellType.EXIT) {
				met = true;
				return;
			}
		}
		met = false;
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
