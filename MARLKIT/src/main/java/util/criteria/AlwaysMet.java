package util.criteria;

import java.util.Optional;

import environment.state.State;

/**
 * A criterion that is always met, regardless of state.
 * Useful as a placeholder or for conditions that should never trigger.
 */
public class AlwaysMet implements Criterion {

	@Override
	public void update(Optional<State> state) {
		//Do nothing
	}

	@Override
	public void reset() {
		//Do nothing
	}

	/**
	 * Always returns true.
	 *
	 * @return true
	 */
	@Override
	public boolean isMet() {
		return true;
	}

}
