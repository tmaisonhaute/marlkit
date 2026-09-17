package modelofotheragents.factory;

import java.util.List;

import agent.MLKAgent;
import agent.action.Action;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import agent.modelofotheragent.StandardGroupModelingPredictAction;
import experiment.configuration.ModelsManagerFactory;
import modelofotheragents.FictitiousPlayDeterministicPredictAction;

public class FictitiousPlayModelsManagerFactory implements ModelsManagerFactory {

    private final Action actionDefault;

    public FictitiousPlayModelsManagerFactory(Action actionDefault) {
        this.actionDefault = actionDefault;
    }

    @Override
    public ModelsManager createModelsManager(MLKAgent predictingAgent, List<MLKAgent> modeledAgents) {
        StandardGroupModelingPredictAction groupModel = new StandardGroupModelingPredictAction();

        for (MLKAgent modeledAgent : modeledAgents) {
            FictitiousPlayDeterministicPredictAction model = new FictitiousPlayDeterministicPredictAction(actionDefault.copy());

            groupModel.addModelPredictAction(modeledAgent, model);
        }
        groupModel.setPredictingAgent(predictingAgent);
        return new PredictionModelsManager(groupModel);
    }

}