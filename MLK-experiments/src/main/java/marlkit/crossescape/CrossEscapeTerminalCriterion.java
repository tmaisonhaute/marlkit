package marlkit.crossescape;

import java.util.Optional;

import environment.state.State;
import environment.state.State2DGridInt;
import util.criteria.Criterion;

/**
 * Terminal criterion for CrossEscape episodes.
 *
 * <p>The criterion is met when all four goal markers are present in the grid.</p>
 */
public class CrossEscapeTerminalCriterion implements Criterion {

	private static final int REQUIRED_MARKERS = 4;

	private boolean met;

	public CrossEscapeTerminalCriterion() {
		met = false;
	}

	@Override
	public void update(Optional<State> state) {
		if (state.isEmpty()) {
			met = false;
			return;
		}
		State2DGridInt grid = (State2DGridInt) state.get();
		int markers = 0;
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				if (grid.getValue(x, y) == EnvCrossEscape.GOAL_REACHED_MARKER) {
					markers++;
				}
			}
		}
		met = markers >= REQUIRED_MARKERS;
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
