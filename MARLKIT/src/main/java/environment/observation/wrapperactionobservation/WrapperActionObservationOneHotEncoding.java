package environment.observation.wrapperactionobservation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import agent.action.Action;
import environment.observation.Observation;
import environment.observation.ObservationOneHotEncoding;

/**
 * A wrapper that converts discrete agent actions into one-hot encoded observations.
 *
 * This is useful in multi-agent learning environments where actions need to be
 * transformed into a vector format for learning algorithms.
 */
public class WrapperActionObservationOneHotEncoding implements WrapperActionObservation {

    private Map<Action, ObservationOneHotEncoding> actions2obs = new HashMap<>();

    /**
     * Constructs a wrapper with a given list of possible actions.
     *
     * @param actions the list of all discrete actions the agent can take
     */
    public WrapperActionObservationOneHotEncoding(List<Action> actions) {
        updateWrapper(actions);
    }

    /**
     * Updates the internal mapping from actions to one-hot encoded observations.
     *
     * Each action is mapped to a vector of size equal to the number of actions,
     * where the index corresponding to the action is set to 1.0 and all others are 0.0.
     *
     * @param actions the list of actions to encode
     */
    public void updateWrapper(List<Action> actions) {
        Objects.requireNonNull(this.actions2obs, "actions2obs map must not be null");
        this.actions2obs.clear();

        int size = actions.size();
        for (int i = 0; i < size; i++) {
            List<Double> vector = new ArrayList<>(Collections.nCopies(size, 0.0));
            vector.set(i, 1.0);
            Action action = actions.get(i);
            ObservationOneHotEncoding encoding = new ObservationOneHotEncoding(vector);
            this.actions2obs.put(action, encoding);
        }
    }

    /**
     * Transforms an action into its one-hot encoded observation.
     *
     * @param action the action to be transformed
     * @return a one-hot encoded observation representing the action
     */
    @Override
    public Observation transform(Action action) {
        ObservationOneHotEncoding encoding = actions2obs.get(action);
        return encoding != null ? encoding.copy() : null;
    }
}
