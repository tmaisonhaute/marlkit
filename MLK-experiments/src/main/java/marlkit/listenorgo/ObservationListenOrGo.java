package marlkit.listenorgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import environment.observation.Observation;

public class ObservationListenOrGo implements Observation {
    private final List<Choice> listenResults;
    private final List<Choice> othersDirections;
    
    public ObservationListenOrGo() {
        this.listenResults = new ArrayList<>();
        this.othersDirections = new ArrayList<>();
    }
    
    public ObservationListenOrGo(List<Choice> listenResults, List<Choice> othersDirections) {
        this.listenResults = new ArrayList<>(listenResults);
        this.othersDirections = new ArrayList<>(othersDirections);
    }
    
    public void addListenResult(Choice result) {
        listenResults.add(result);
    }
    
    public void addOtherDirection(Choice direction) {
        othersDirections.add(direction);
    }
    
    public List<Choice> getListenResults() {
        return new ArrayList<>(listenResults);
    }
    
    public List<Choice> getOthersDirections() {
        return new ArrayList<>(othersDirections);
    }
    
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
