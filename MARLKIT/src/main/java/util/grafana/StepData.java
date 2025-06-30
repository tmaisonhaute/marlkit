package util.grafana;

import agent.MLKAgent;
import learning.Experience;
import util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StepData {
    private Pair<Map<MLKAgent, Experience>, Optional<List<Extra>>> data;

    public StepData(Map<MLKAgent, Experience> experiences, Optional<List<Extra>> extra) {
        this(new Pair<>(experiences, extra));
    }

    public StepData(Map<MLKAgent, Experience> experiences) {
        this(new Pair<>(experiences, Optional.empty()));
    }

    public StepData(Pair<Map<MLKAgent, Experience>, Optional<List<Extra>>> data) {
        this.data = data;
    }
    
    public Pair<Map<MLKAgent, Experience>, Optional<List<Extra>>> getData() {
        return data;
    }

    public Map<MLKAgent, Experience> getExperiences() {
        return data.getFirst();
    }

    public Experience getExperience(MLKAgent agent) {
        return data.getFirst().get(agent);
    }

    public Optional<List<Extra>> getExtra() {
        return data.getSecond();
    }

    public void setData(Pair<Map<MLKAgent, Experience>, Optional<List<Extra>>> data) {
        this.data = data;
    }
}
