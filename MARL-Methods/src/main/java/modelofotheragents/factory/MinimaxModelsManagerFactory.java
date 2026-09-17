package modelofotheragents.factory;

import java.util.List;

import agent.MLKAgent;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import algorithm.QValueBasedJALPolicy;
import experiment.configuration.ModelsManagerFactory;
import modelofotheragents.MinimaxValueFunctionPredictAction;

public class MinimaxModelsManagerFactory implements ModelsManagerFactory {

    @Override
    public ModelsManager createModelsManager(MLKAgent predictingAgent, List<MLKAgent> modeledAgents) {
        if (!(predictingAgent.getPolicy() instanceof QValueBasedJALPolicy jalPolicy)) {
            throw new IllegalArgumentException("Minimax requires a QLearningJAL policy.");
        }

        MinimaxValueFunctionPredictAction groupModel = new MinimaxValueFunctionPredictAction(jalPolicy.getTable());
        groupModel.setPredictingAgent(predictingAgent);

        return new PredictionModelsManager(groupModel);
    }

}