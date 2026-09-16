package experiment.configuration;
import java.util.List;

import agent.MLKAgent;
import agent.modelofotheragent.ModelsManager;

/**
 * Creates a models manager for an agent modeling other agents.
 *
 * <p>
 * The factory receives the predicting agent and the agents that it should model.
 * A fresh models manager should be created for each predicting agent.
 * </p>
 */
@FunctionalInterface
public interface ModelsManagerFactory {

    /**
     * Creates a models manager for the specified predicting agent.
     *
     * @param predictingAgent the agent performing the modeling
     * @param modeledAgents the agents that should be modeled
     * @return a fresh models manager
     */
    ModelsManager createModelsManager(MLKAgent predictingAgent, List<MLKAgent> modeledAgents);
}