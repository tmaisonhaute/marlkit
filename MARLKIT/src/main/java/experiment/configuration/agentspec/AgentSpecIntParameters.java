package experiment.configuration.agentspec;

import java.util.Map;

public interface AgentSpecIntParameters extends AgentSpec {

	    Map<String, Integer> integerParameters();

	    default int requireInteger(String name) {
	        Integer value = integerParameters().get(name);

	        if (value == null) {
	            throw new IllegalArgumentException(
	                    "AgentSpec requires the integer parameter: " + name
	            );
	        }

	        return value;
	    }
	}