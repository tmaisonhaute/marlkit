package marlkit.collectingresource.experiments;

import java.util.List;

import agent.action.Action;
import experiment.configuration.agentspec.AgentSpec;
import marlkit.collectingresource.scenario.ScenarioCollectingResource;

public class CollectingResourceAgentSpec implements AgentSpec {

    private final ScenarioCollectingResource scenario;

    public CollectingResourceAgentSpec(ScenarioCollectingResource scenario) {
        this.scenario = scenario;
    }

    @Override
    public List<Action> getPossibleActions() {
        return scenario.getPossibleActions();
    }

}