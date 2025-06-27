package environment.observation.Wrapperactionobservation;

import agent.action.Action;
import environment.observation.Observation;
import environment.observation.ObservationOneHotEncoding;

import java.util.*;

public class WrapperActionObservationOneHotEncoding implements WrapperActionObservation {
    private Map<Action, ObservationOneHotEncoding> actions2obs = new HashMap<>();

    public WrapperActionObservationOneHotEncoding(List<Action> actions) {
        updateWrapper(actions);
    }

    public void updateWrapper(List<Action> actions) {
        assert this.actions2obs != null;
        this.actions2obs.clear();
        int size = actions.size();
        for (int i = 0; i < size; i++) {
            List<Double> liste = new ArrayList<>(Collections.nCopies(size, 0.));
            liste.set(i,1.);
            Action action = actions.get(i);
            ObservationOneHotEncoding OHE = new ObservationOneHotEncoding(liste);
            this.actions2obs.put(action, OHE);
        }
    }

    @Override
    public Observation transform(Action action) {
        return actions2obs.get(action).clone();
    }
}
