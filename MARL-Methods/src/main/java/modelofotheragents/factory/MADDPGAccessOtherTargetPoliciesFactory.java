package modelofotheragents.factory;

import java.util.List;
import java.util.Objects;

import agent.MLKAgent;
import agent.modelofotheragent.ModelsManager;
import agent.modelofotheragent.PredictionModelsManager;
import agent.modelofotheragent.StandardGroupModelingPredictAction;
import experiment.configuration.ModelsManagerFactory;
import learning.algorithms.MADDPG;
import modelofotheragents.AccessOtherPolicy;

public class MADDPGAccessOtherTargetPoliciesFactory implements ModelsManagerFactory {

	    @Override
	    public ModelsManager createModelsManager(MLKAgent predictingAgent, List<MLKAgent> modeledAgents) {
	        Objects.requireNonNull(predictingAgent, "predictingAgent");
	        Objects.requireNonNull(modeledAgents, "modeledAgents");

	        StandardGroupModelingPredictAction groupModel = new StandardGroupModelingPredictAction();

	        for (MLKAgent modeledAgent : modeledAgents) {
	            Objects.requireNonNull(modeledAgent, "modeledAgent");

	            if (!(modeledAgent.getAlgorithm() instanceof MADDPG modeledMADDPG)) {
	                throw new IllegalArgumentException("MADDPG requires modeled agents using MADDPG: " + modeledAgent.getClass().getName()
	                );
	            }

	            groupModel.addModelPredictAction(modeledAgent, new AccessOtherPolicy(modeledMADDPG.getTargetActor()));
	        }

	        groupModel.setPredictingAgent(predictingAgent);

	        return new PredictionModelsManager(groupModel);
	    }
	}

