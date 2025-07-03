package environment.observation.Wrapperactionobservation;

import agent.action.Action;
import environment.observation.Observation;

public interface WrapperActionObservation{
    /**
     * Transforms an action to his Observation
     *
     * @param action action wrap
     * @return an observation representing the action
     */
    Observation transform(Action action);
}
