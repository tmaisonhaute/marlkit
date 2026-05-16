package util.grafana;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpisodeData {

    private final Map<String, Double> extras;

    public EpisodeData(List<? extends Extra> extras) {
        this.extras = new HashMap<>();

        for (Extra extra : extras) {
            this.extras.put(
                extra.toString(),
                extra.toDouble()
            );
        }
    }

    public Map<String, Double> getExtras() {
        return extras;
    }
}
