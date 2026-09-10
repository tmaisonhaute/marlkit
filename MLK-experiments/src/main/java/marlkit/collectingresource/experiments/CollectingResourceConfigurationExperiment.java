package marlkit.collectingresource.experiments;

import java.util.List;

import agentmodule.QLearningAgentModuleBuilder;
import experiment.ConfigurationRunner;
import experiment.configuration.AgentGroupConfiguration;
import experiment.configuration.AgentModule;
import experiment.configuration.ExperimentConfiguration;
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

            SystemEvaluatorModule systemEvaluation =
                    new SystemEvaluatorModule(CollectingResourceEnvEvaluator.class);


            AgentModule qLearningAgentModule = QLearningAgentModuleBuilder.builder()
            		.agentClass(CollectingResourceAgent.class)
            		.epsilonDecay(0.003)
            		.build();
            
            
            ScenarioCollectingResource scenarioFullyCoop = new ScenarioSpatial5();

            ExperimentConfiguration configFullyCoop =
                    ExperimentConfiguration.named("CollectingResource_FullyCooperative_QLearning")
                            .environment(new CollectingResourceEnvironmentModule(ENV_WIDTH, ENV_HEIGHT, scenarioFullyCoop))
                            .rewardModel(FullyCooperativeReward.class)
                            .scheduler(SchedulerCollectingResource.class)
                            .agentGroup(new AgentGroupConfiguration(
                                    scenarioFullyCoop.getNumberOfAgents(),
                                    qLearningAgentModule,
                                    new CollectingResourceAgentSpec(scenarioFullyCoop)
                                    
                            ))
                            .systemEvaluator(systemEvaluation)
                            .build();
            
            ScenarioCollectingResource scenarioMixed = new ScenarioSpatial5();
            
            ExperimentConfiguration configMixed =
                    ExperimentConfiguration.named("CollectingResource_Mixed_QLearning")
                            .environment(new CollectingResourceEnvironmentModule(ENV_WIDTH, ENV_HEIGHT, scenarioMixed))
                            .rewardModel(MixedReward.class)
                            .scheduler(SchedulerCollectingResource.class, DecentralizedTrainingExecutionStrategy.class)
                            .agentGroup(new AgentGroupConfiguration(
                                    scenarioMixed.getNumberOfAgents(),
                                    qLearningAgentModule,
                                    new CollectingResourceAgentSpec(scenarioMixed)
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