package modelofotheragents.factory;

import java.util.List;
import java.util.Objects;

import agent.MLKAgent;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import agent.modelofotheragent.StandardGroupModelingPredictAction;
import experiment.configuration.ModelsManagerFactory;
import modelofotheragents.AccessOtherPolicy;

/**
 * Creates prediction model managers that directly access the policies of the
 * modeled agents.
 */
public class AccessOtherPoliciesFactory implements ModelsManagerFactory {

    @Override
    public ModelsManager createModelsManager(MLKAgent predictingAgent, List<MLKAgent> modeledAgents) {
        Objects.requireNonNull(predictingAgent, "predictingAgent");
        Objects.requireNonNull(modeledAgents, "modeledAgents");

        StandardGroupModelingPredictAction groupModel = new StandardGroupModelingPredictAction();

        for (MLKAgent modeledAgent : modeledAgents) {
            Objects.requireNonNull(modeledAgent, "modeledAgent");

            if (modeledAgent == predictingAgent) {
                continue;
            }

            groupModel.addModelPredictAction(
                    modeledAgent,
                    new AccessOtherPolicy(modeledAgent.getPolicy())
            );
        }

        groupModel.setPredictingAgent(predictingAgent);

        return new PredictionModelsManager(groupModel);
    }
}