package simulation;

import agent.MLKAgent;
import learning.Experience;
import util.Pair;

import java.util.Map;
import java.util.Optional;

public class StepData {
    private Pair<Map<MLKAgent, Experience>, Optional<Map<String, Double>>> data;

    public StepData(Pair<Map<MLKAgent, Experience>, Optional<Map<String, Double>>> data) {
        this.data = data;
    }
    
    public Pair<Map<MLKAgent, Experience>, Optional<Map<String, Double>>> getData() {
        return data;
    }

    public Map<MLKAgent, Experience> getExperiences() {
        return data.getFirst();
    }

    public Experience getExperience(MLKAgent agent) {
        return data.getFirst().get(agent);
    }

    public Optional<Map<String, Double>> getExtra() {
        return data.getSecond();
    }

    public void setData(Pair<Map<MLKAgent, Experience>, Optional<Map<String, Double>>> data) {
        this.data = data;
    }
}
