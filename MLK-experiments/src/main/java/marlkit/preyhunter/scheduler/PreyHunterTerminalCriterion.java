package marlkit.preyhunter.scheduler;

import java.util.Optional;

import environment.state.State;
import marlkit.preyhunter.environment.StatePreyHunter2D;
import util.criteria.Criterion;

public class PreyHunterTerminalCriterion implements Criterion {

    private boolean met;

    public PreyHunterTerminalCriterion() {
        this.met = false;
    }

    @Override
    public void update(Optional<State> state) {
        if (state.isEmpty()) {
            met = false;
            return;
        }

        StatePreyHunter2D preyHunterState = (StatePreyHunter2D) state.get();
        met = preyHunterState.areAllPreysCaptured();
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