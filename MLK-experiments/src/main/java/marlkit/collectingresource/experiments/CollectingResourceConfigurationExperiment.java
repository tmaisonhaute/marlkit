package marlkit.collectingresource.experiments;

import java.util.List;

import communication.NoCommunication;
import experiment.ConfigurationRunner;
import experiment.configuration.AgentGroupConfiguration;
import experiment.configuration.CommunicationModule;
import experiment.configuration.ExperimentConfiguration;
import experiment.configuration.LearningModule;
import experiment.configuration.ModelOfOthersModule;
import experiment.configuration.RewardModelModule;
import experiment.configuration.SystemEvaluatorModule;
import madkit.kernel.Agent;
import marlkit.collectingresource.agent.CollectingResourceAgent;
import marlkit.collectingresource.scenario.ScenarioCollectingResource;
import marlkit.collectingresource.scenario.ScenarioSpatial5;
import marlkit.collectingresource.scheduler.SchedulerCollectingResource;
import marlkit.collectingresource.systemevaluator.CollectingResourceEnvEvaluator;
import rewardmodelimplementation.FullyCooperativeReward;
import rewardmodelimplementation.MixedReward;
import trainingexecutionstrategy.DecentralizedTrainingExecutionStrategy;

/**
 * Experiment configuration for the Collecting Resource environment.
 * 
 * <p>
 * This class defines experiment configurations for the Collecting Resource environment.
 * </p>
 */
public class CollectingResourceConfigurationExperiment extends Agent{

    private static final int ENV_WIDTH = 10;
    private static final int ENV_HEIGHT = 10;
    
    @Override
    	protected void onActivation() {
    		super.onActivation();
    		LearningModule qLearning =
                    new LearningModule(new CollectingResourceQLearningComponentsCreator());

            CommunicationModule noCommunication =
                    new CommunicationModule(NoCommunication.class);

            ModelOfOthersModule noModelOfOthers =
                    new ModelOfOthersModule(null);

            SystemEvaluatorModule systemEvaluation =
                    new SystemEvaluatorModule(CollectingResourceEnvEvaluator.class);
            
            ScenarioCollectingResource scenarioFullyCoop = new ScenarioSpatial5();

            ExperimentConfiguration configFullyCoop =
                    ExperimentConfiguration.named("CollectingResource_FullyCooperative_QLearning")
                            .environment(new CollectingResourceEnvironmentModule(ENV_WIDTH, ENV_HEIGHT, scenarioFullyCoop))
                            .rewardModel(new RewardModelModule(FullyCooperativeReward.class))
                            .scheduler(SchedulerCollectingResource.class)
                            .agentGroup(new AgentGroupConfiguration(
                                    CollectingResourceAgent.class,
                                    scenarioFullyCoop.getNumberOfAgents(),
                                    new CollectingResourceAgentSpec(scenarioFullyCoop),
                                    qLearning,
                                    noCommunication,
                                    noModelOfOthers
                            ))
                            .systemEvaluator(systemEvaluation)
                            .build();
            
            ScenarioCollectingResource scenarioMixed = new ScenarioSpatial5();
            
            ExperimentConfiguration configMixed =
                    ExperimentConfiguration.named("CollectingResource_Mixed_QLearning")
                            .environment(new CollectingResourceEnvironmentModule(ENV_WIDTH, ENV_HEIGHT, scenarioMixed))
                            .rewardModel(new RewardModelModule(MixedReward.class))
                            .scheduler(SchedulerCollectingResource.class, DecentralizedTrainingExecutionStrategy.class)
                            .agentGroup(new AgentGroupConfiguration(
                                    CollectingResourceAgent.class,
                                    scenarioMixed.getNumberOfAgents(),
                                    new CollectingResourceAgentSpec(scenarioMixed),
                                    qLearning,
                                    noCommunication,
                                    noModelOfOthers
                            ))
                            .systemEvaluator(systemEvaluation)
                            .build();
    		
            launchAgent(new ConfigurationRunner(List.of(configFullyCoop, configMixed)));
    	}


    public static void main(String[] args) {
    	executeThisAgent("--agentLogLevel", "INFO",
                "--start");
        

    }
}