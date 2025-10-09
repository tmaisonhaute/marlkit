package util.criteria;

import java.util.Optional;

import environment.state.State;

public class AlwaysMet implements Criterion {

	@Override
	public void update(Optional<State> state) {
		//Do nothing
	}

	@Override
	public void reset() {
		//Do nothing
	}

	@Override
	public boolean isMet() {
		return true;
	}

}
