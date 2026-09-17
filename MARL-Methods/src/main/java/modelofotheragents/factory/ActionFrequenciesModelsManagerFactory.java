package modelofotheragents.factory;

import java.util.List;

import agent.MLKAgent;
import agent.action.Action;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import agent.modelofotheragent.StandardGroupModelingPredictAction;
import experiment.configuration.ModelsManagerFactory;
import modelofotheragents.NActionFrequenciesDeterministicPredictionAction;

public class ActionFrequenciesModelsManagerFactory implements ModelsManagerFactory {

    private final Action actionDefault;
    private final int windowSize;

    public ActionFrequenciesModelsManagerFactory(Action actionDefault, int windowSize) {
        this.actionDefault = actionDefault;

        if (windowSize <= 0) {
            throw new IllegalArgumentException("windowSize must be greater than 0.");
        }

        this.windowSize = windowSize;
    }

    @Override
    public ModelsManager createModelsManager(MLKAgent predictingAgent, List<MLKAgent> modeledAgents) {
        StandardGroupModelingPredictAction groupModel = new StandardGroupModelingPredictAction();

        for (MLKAgent modeledAgent : modeledAgents) {
            NActionFrequenciesDeterministicPredictionAction model = new NActionFrequenciesDeterministicPredictionAction(actionDefault.copy(), windowSize);
            groupModel.addModelPredictAction(modeledAgent, model);
        }

        groupModel.setPredictingAgent(predictingAgent);
        return new PredictionModelsManager(groupModel);
    }

}