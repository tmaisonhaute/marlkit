package experiment.configuration.agentspec;

import java.util.Map;

public interface AgentSpecDoubleParameters extends AgentSpec {

    Map<String, Double> doubleParameters();

    default double requireDouble(String name) {
        Double value = doubleParameters().get(name);

        if (value == null) {
            throw new IllegalArgumentException(
                    "AgentSpec requires the double parameter: " + name
            );
        }

        return value;
    }
}