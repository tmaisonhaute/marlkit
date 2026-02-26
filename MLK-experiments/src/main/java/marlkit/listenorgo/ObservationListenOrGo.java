package marlkit.listenorgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import environment.observation.Observation;

/**
 * Represents the observation accumulated by an agent during an episode
 * of the ListenOrGo environment.
 * <p>
 * An observation records two kinds of information:
 * <ul>
 *   <li>the results of the agent's own {@link ActionListen listen} actions (noisy signals
 *       about the correct door), and</li>
 *   <li>the directions chosen by other agents that have already committed.</li>
 * </ul>
 * </p>
 */
public class ObservationListenOrGo implements Observation {
    private final List<Choice> listenResults;
    private final List<Choice> othersDirections;
    
    /**
     * Creates an empty observation with no listen results and no observed directions.
     */
    public ObservationListenOrGo() {
        this.listenResults = new ArrayList<>();
        this.othersDirections = new ArrayList<>();
    }
    
    /**
     * Creates an observation pre-populated with the given listen results and others' directions.
     * Defensive copies of the provided lists are made.
     *
     * @param listenResults    the list of noisy signals received so far.
     * @param othersDirections the list of directions chosen by other agents.
     */
    public ObservationListenOrGo(List<Choice> listenResults, List<Choice> othersDirections) {
        this.listenResults = new ArrayList<>(listenResults);
        this.othersDirections = new ArrayList<>(othersDirections);
    }
    
    /**
     * Records a noisy signal received from a listen action.
     *
     * @param result the direction indicated by the signal ({@link Choice#LEFT} or {@link Choice#RIGHT}).
     */
    public void addListenResult(Choice result) {
        listenResults.add(result);
    }
    
    /**
     * Records the direction committed to by another agent.
     *
     * @param direction the direction chosen by the other agent.
     */
    public void addOtherDirection(Choice direction) {
        othersDirections.add(direction);
    }
    
    /**
     * Returns a defensive copy of the noisy signals accumulated from listen actions.
     *
     * @return list of {@link Choice} values received from listening.
     */
    public List<Choice> getListenResults() {
        return new ArrayList<>(listenResults);
    }
    
    /**
     * Returns a defensive copy of the directions committed to by other agents.
     *
     * @return list of {@link Choice} values observed from other agents.
     */
    public List<Choice> getOthersDirections() {
        return new ArrayList<>(othersDirections);
    }
    
    /**
     * Returns a new observation that combines this observation with {@code other}
     * by concatenating both listen results and others' directions lists.
     *
     * @param other the observation to merge with this one.
     * @return a new {@link ObservationListenOrGo} containing the combined data.
     * @throws IllegalArgumentException if {@code other} is not an {@link ObservationListenOrGo}.
     */
    @Override
    public Observation add(Observation other) {
        if (!(other instanceof ObservationListenOrGo)) {
            throw new IllegalArgumentException("Cannot add different types of observations");
        }
        
        ObservationListenOrGo otherObs = (ObservationListenOrGo) other;
        List<Choice> combinedListens = new ArrayList<>(this.listenResults);
        combinedListens.addAll(otherObs.listenResults);
        
        List<Choice> combinedOthers = new ArrayList<>(this.othersDirections);
        combinedOthers.addAll(otherObs.othersDirections);
        
        return new ObservationListenOrGo(combinedListens, combinedOthers);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ObservationListenOrGo obs) {
            return listenResults.equals(obs.listenResults) && 
                   othersDirections.equals(obs.othersDirections);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(listenResults, othersDirections);
    }
    
    @Override
    public String toString() {
        return "ObservationListenOrGo{listens=" + listenResults + ", others=" + othersDirections + "}";
    }
}
