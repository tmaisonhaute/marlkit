package marlkit.foraging;

import java.util.Optional;

import environment.state.State;
import environment.state.State2DGridInt;
import util.criteria.Criterion;

public class AllCollectedCriterion implements Criterion{
	public boolean isMet;

	public AllCollectedCriterion() {
		this.isMet = false;
	}

	@Override
	public void update(Optional<State> state) {
		State2DGridInt stateGrid = (State2DGridInt) state.get();
		for(int i = 0; i < stateGrid.getWidth(); i++) {
			for(int j = 0; j < stateGrid.getHeight(); j++) {
				if(stateGrid.getValue(i, j) > 0) {
					isMet = false;
					return;
				}
			}
		}
		isMet = true;
	}

	@Override
	public void reset() {
		isMet = false;
		
	}

	@Override
	public boolean isMet() {
		return isMet;
	}

}
