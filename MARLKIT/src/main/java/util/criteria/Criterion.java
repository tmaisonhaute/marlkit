package util.criteria;

import java.util.Optional;

import environment.state.State;

public interface Criterion {
	
	public void update(Optional<State> state);
	public void reset();
	
	public boolean isMet();
}
